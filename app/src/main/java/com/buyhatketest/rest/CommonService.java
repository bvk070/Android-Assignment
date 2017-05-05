package com.buyhatketest.rest;


import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public interface CommonService {

    @GET("/PickCoupon/FreshCoupon/getCoupons.php")
    void getCoupens(
            @Query("pos") String pos,
            Callback<Response> callback);

}
