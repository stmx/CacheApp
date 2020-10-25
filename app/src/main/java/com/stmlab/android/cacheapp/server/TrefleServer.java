package com.stmlab.android.cacheapp.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stmlab.android.cacheapp.models.ObjectResponseFromTrefleAPI;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrefleServer{
    private final String token = "yKQEbQgyXQgbjsWsUZj1Z6duFKXzKjxLpGIkgi-pcOA";

    Server mServer;

    public TrefleServer() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://trefle.io/api/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
        mServer = retrofit.create(Server.class);
    }

    public Observable<ObjectResponseFromTrefleAPI> getPage(int page) {
        return mServer.get(token, "asc", page);
    }
    public Observable<ObjectResponseFromTrefleAPI> get() {
        return mServer.get(token);
    }


}
