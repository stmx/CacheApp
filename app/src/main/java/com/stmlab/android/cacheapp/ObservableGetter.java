package com.stmlab.android.cacheapp;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.stmlab.android.cacheapp.adapters.TrefleAdapter;
import com.stmlab.android.cacheapp.database.TrefleSchema;
import com.stmlab.android.cacheapp.models.TrefleModel;
import com.stmlab.android.cacheapp.server.TrefleServer;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;

class ObservableGetter {

    static public Observable<TrefleModel> getObservableFromAdapter(TrefleAdapter adapter) {
        return adapter.getObservable()
                .flatMap(trefleList -> Observable.fromIterable(trefleList))
                .observeOn(Schedulers.io());
    }

    static public Observable<ArrayList<TrefleModel>> getObservableNetwork(Subject<Integer> subject, TrefleServer server, ContentResolver contentResolver) {
        return subject.flatMap(page -> server.getPage(page))
                .map(oobjectResponse -> oobjectResponse.getData())
                .filter(trefleList -> trefleList.size() > 0)
                .doOnNext(trefleList -> contentResolver.delete(TrefleSchema.TrefleEntry.CONTENT_URI, null, null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static public Observable<ArrayList<TrefleModel>> getObservablePager(Subject<Integer> subject, TrefleServer server) {
        return subject.flatMap(page -> server.getPage(page))
                .map(oobjectResponse -> oobjectResponse.getData())
                .filter(trefleList -> trefleList.size() > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static public <T> Observable<T> queryInBackground(
            final ContentResolver contentResolver,
            final Uri uri,
            final String[] projection,
            final String selection,
            final String[] selectionArgs,
            final String sortOrder,
            final MainActivity.CursorCallback<T> cursorCallback) {
        return Observable.create((ObservableOnSubscribe<T>) emitter -> {

            if ( !emitter.isDisposed() ) {
                Cursor cursor = null;
                try {
                    cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
                    if ( cursor != null && cursor.getCount() > 0 ) {
                        emitter.onNext(cursorCallback.callback(cursor));
                    } else {
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }

}


