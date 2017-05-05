package com.buyhatketest.rest;

import com.buyhatketest.util.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;


public class RestClient {
    private static CommonService REST_CLIENT_COMMON_SERVICE;

    static {
        setupRestClient();
    }

    private RestClient() {
    }

    private static void setupRestClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory()) // This is the important line ;)
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        REST_CLIENT_COMMON_SERVICE = buildAdapter(Constant.URL, gson).create(CommonService.class);
    }

    private static RestAdapter buildAdapter(String endPoint, Gson gson) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(endPoint)
                .setConverter(new GsonConverter(gson))
                .build();

        return restAdapter;


    }


    public static CommonService getCommonService() {
        return REST_CLIENT_COMMON_SERVICE;
    }

}
