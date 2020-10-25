package com.stmlab.android.cacheapp.server;

import com.stmlab.android.cacheapp.models.ObjectResponseFromTrefleAPI;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Server {
    @GET("plants?filter_not[common_name]=null")
    Observable<ObjectResponseFromTrefleAPI> get(@Query("token") String token, @Query("order[common_name]")String order, @Query("page")int page);
    @GET("plants?")
    Observable<ObjectResponseFromTrefleAPI> get(@Query("token") String token);
}
