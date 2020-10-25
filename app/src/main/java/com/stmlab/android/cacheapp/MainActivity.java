package com.stmlab.android.cacheapp;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.stmlab.android.cacheapp.adapters.TrefleAdapter;
import com.stmlab.android.cacheapp.database.DBHelper;
import com.stmlab.android.cacheapp.database.TrefleSchema;
import com.stmlab.android.cacheapp.models.TrefleModel;
import com.stmlab.android.cacheapp.server.TrefleServer;
import com.stmlab.android.cacheapp.workers.DeleteDataFromTableWorker;
import com.stmlab.android.cacheapp.workers.DurationUpdateWorker;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import retrofit2.adapter.rxjava2.HttpException;

public class MainActivity extends AppCompatActivity {

    private static final int DURATION_UPDATE_IN_MINUTE = 5;
    private static final TimeUnit TIME_UNIT_DURATION = TimeUnit.MINUTES;
    private static final int TIME_BEFORE_DELETE_IN_HOURS = 24;
    private static final TimeUnit TIME_UNIT_TIME_BEFORE_DELETE = TimeUnit.HOURS;
    private static final String TAG_DURATION_UPDATE = "com.stmlab.android.casheapp.workers.update";
    private static final String TAG_TIME_BEFORE_DELETE = "com.stmlab.android.casheapp.workers.clear";
    private static final String TAG_LOG = "casheapp.rx";




    Button mButton;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    TrefleAdapter mAdapter;
    TrefleServer mServer;
    static public Boolean activityStarted = false;


    //для отписки после пересоздания активити
    Disposable mDisposableFromServerToAdapter;
    Disposable mDisposableFromDatabaseToAdapter;
    Disposable mDisposableFromAdapterToDatabase;
    Disposable mDisposablePager;
    Disposable mDisposableGetPage;
    Disposable mDisposableTimer;

    //для отсчета  времени после посленей удачной загрузки
    OneTimeWorkRequest mRequestFiveMinitsCounter;
    //для отсчета времени для удаления бузы данных
    OneTimeWorkRequest mRequstDeleteDataBase;

    //источник данных с сети
    private Observable<ArrayList<TrefleModel>> mObservableFromNewtwork;
    //источник данных при требованиии пагинации
    private Observable<ArrayList<TrefleModel>> mObservablePager;
    //источник данных при изменении адаптера
    private Observable<TrefleModel> mObservableFromAdapter;

    //слушатель при загрузке данных из сети при старте
    private Subject<Integer> mPublishSubject = BehaviorSubject.create();
    //слушатель при загрузке данных из сети при пагинации
    private Subject<Integer> mPublish = BehaviorSubject.create();

    public interface CursorCallback<T> {
        T callback(Cursor cursor) throws SQLException;
    }

    //обработчик при загрузке данных
    Consumer<ArrayList<TrefleModel>> onActionDownloading = trefleList -> {
        Log.d(TAG_LOG, "addDataToAdapter = " + trefleList.size());
        mAdapter.addTrefleList(trefleList, 1);
//        Toast.makeText(MainActivity.this, "Данные успешно обновлены", Toast.LENGTH_SHORT).show();
        resetTimerTask();
        resetDeleteDatabaseTask();
        activityStarted = true;
    };
    //обработчик при ошибке загрузки данных
    Consumer<Throwable> onErrorDownloading = e -> {
        Log.d(TAG_LOG, "errorDownloading");
        if ( e instanceof HttpException ) {
            Toast.makeText(MainActivity.this, "Не удалось загрузить данные " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Не удалось загрузить данные", Toast.LENGTH_SHORT).show();
        }
        activityStarted = true;
    };
    //обработчик при запуске активити и наличии элементов в БД
    Consumer<ArrayList<TrefleModel>> onNextStartApp = trefleList -> {
        Log.d(TAG_LOG, "setAdapter = " + trefleList.size());
        mAdapter.setupTrefleList(trefleList);
        if ( !MainActivity.this.getCurrentStateTimerTask() ) {
            activityStarted = true;
        } else {
            mPublishSubject.onNext(1);
        }
    };
    //обработчик при запуске активити и отсутствии элементов в БД
    Action onCompleteStartApp = () -> {
        Log.d(TAG_LOG, "onComplete");
        mPublishSubject.onNext(1);
    };
    //обработчик вызываемый при необходимости вставки данных в БД
    Consumer<TrefleModel> onNextInsertToDatabase = model -> {
        MainActivity.this.getContentResolver().insert(TrefleSchema.TrefleEntry.CONTENT_URI, DBHelper.getContentValue(model));
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new TrefleAdapter();
        mServer = new TrefleServer();
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener((v -> resetTimerTask()));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if ( ((mLayoutManager.getChildCount() + mLayoutManager.findFirstVisibleItemPosition()) >= mLayoutManager.getItemCount()) || (mLayoutManager.getItemCount() == 0) ) {
                    int page = (mLayoutManager.getItemCount() / 20) + 1;
                    //при ошибке загрузки данных происходит отписка, необходимо заново подписать
                    if ( mDisposablePager.isDisposed() ) {
                        mDisposablePager = mObservablePager.subscribe(trefleList -> mAdapter.addTrefleList(trefleList), onErrorDownloading);
                    }
                    //посылаем обработчику событие о необходимости загрузки следующей страницы
                    mPublish.onNext(page);
                }
            }
        });
        if ( getDeleteDatabaseTask() ) {
            Toast.makeText(MainActivity.this, "С момента последней загрузки данных прошло слишком много времени, данные удалены!", Toast.LENGTH_SHORT).show();
        }

        mDisposableTimer = Observable.interval(DURATION_UPDATE_IN_MINUTE, TIME_UNIT_DURATION)
                .filter(time -> activityStarted)
                .subscribe(time -> {
                    Log.d(TAG_LOG, "mObservable.isDisposed = " + mDisposableGetPage.isDisposed());
                    //при ошибке загрузки данных происходит отписка, необходимо заново подписать
                    if ( mDisposableGetPage.isDisposed() ) {
                        mDisposableGetPage = mObservableFromNewtwork.subscribe(onActionDownloading, onErrorDownloading);
                    }
                    //посылаем обработчику событие о необходимости загрузки первой страницы для обновления БД
                    mPublishSubject.onNext(1);
                });

        mObservableFromNewtwork = ObservableGetter.getObservableNetwork(mPublishSubject, mServer, getContentResolver());
        mObservableFromAdapter = ObservableGetter.getObservableFromAdapter(mAdapter);
        mObservablePager = ObservableGetter.getObservablePager(mPublish, mServer);

        mDisposablePager = mObservablePager.subscribe(trefleList -> mAdapter.addTrefleList(trefleList),onErrorDownloading);
        mDisposableGetPage = mObservableFromNewtwork.subscribe(onActionDownloading, onErrorDownloading);
        mDisposableFromAdapterToDatabase = mObservableFromAdapter.subscribe(onNextInsertToDatabase);
        mDisposableFromDatabaseToAdapter = ObservableGetter.queryInBackground(
                getContentResolver(),
                TrefleSchema.TrefleEntry.CONTENT_URI,
                null,
                null,
                null,
                null,
                DBHelper::cursorToModelList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextStartApp, (e) -> {
                }, onCompleteStartApp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unSubscribe();
    }

    private void unSubscribe() {
        if ( mDisposableFromServerToAdapter != null ) {
            mDisposableFromServerToAdapter.dispose();
        }
        if ( mDisposableFromDatabaseToAdapter != null ) {
            mDisposableFromDatabaseToAdapter.dispose();
        }
        if ( mDisposableFromAdapterToDatabase != null ) {
            mDisposableFromAdapterToDatabase.dispose();
        }
        if ( mDisposableGetPage != null ) {
            mDisposableGetPage.dispose();
        }
        if ( mDisposableTimer != null ) {
            mDisposableTimer.dispose();
        }
        if ( mDisposablePager !=null) {
            mDisposablePager.dispose();
        }
    }


    public boolean getCurrentStateTimerTask() {
        try {
            return WorkManager.getInstance(getApplication()).getWorkInfosByTag(TAG_DURATION_UPDATE).get().get(0).getState().isFinished();
        } catch (ExecutionException | InterruptedException e) {
            return true;
        }
    }

    public void resetTimerTask() {
        WorkManager.getInstance(getApplication()).cancelAllWorkByTag(TAG_DURATION_UPDATE);
        WorkManager.getInstance(getApplication()).pruneWork();
        mRequestFiveMinitsCounter = new OneTimeWorkRequest.Builder(DurationUpdateWorker.class)
                .addTag(TAG_DURATION_UPDATE)
                .setInitialDelay(DURATION_UPDATE_IN_MINUTE, TIME_UNIT_DURATION)
                .build();
        WorkManager.getInstance(getApplication()).enqueue(mRequestFiveMinitsCounter);
    }

    public boolean getDeleteDatabaseTask() {
        try {
            if ( WorkManager.getInstance(getApplication()).getWorkInfosByTag(TAG_TIME_BEFORE_DELETE).get().size() > 0 ) {
                return WorkManager.getInstance(getApplication()).getWorkInfosByTag(TAG_TIME_BEFORE_DELETE).get().get(0).getState().isFinished();
            }
            return false;
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
    }

    public void resetDeleteDatabaseTask() {
        WorkManager.getInstance(getApplication()).cancelAllWorkByTag(TAG_TIME_BEFORE_DELETE);
        WorkManager.getInstance(getApplication()).pruneWork();
        mRequstDeleteDataBase = new OneTimeWorkRequest.Builder(DeleteDataFromTableWorker.class)
                .addTag(TAG_TIME_BEFORE_DELETE)
                .setInitialDelay(TIME_BEFORE_DELETE_IN_HOURS, TIME_UNIT_TIME_BEFORE_DELETE)
                .build();
        WorkManager.getInstance(getApplication()).enqueue(mRequstDeleteDataBase);
    }
}
