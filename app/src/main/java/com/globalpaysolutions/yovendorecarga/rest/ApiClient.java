package com.globalpaysolutions.yovendorecarga.rest;

import com.globalpaysolutions.yovendorecarga.customs.StringsURL;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Josué Chávez on 28/11/2016.
 */
public class ApiClient
{

    private static Retrofit retrofit = null;


    public static Retrofit getClient()
    {
        if (retrofit==null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(StringsURL.TOPUP)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(18, TimeUnit.SECONDS)
            .writeTimeout(18, TimeUnit.SECONDS)
            .connectTimeout(18, TimeUnit.SECONDS)
            .build();
}
