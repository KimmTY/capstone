package com.capstone.sejong.homenect;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by 12aud on 2017-05-24.
 */

public class ApiGenerator {

    private static ApiService instance; // 싱글톤
    private static final String BASE_URL = "192.168.4.1/";

    public static ApiService getInstance(){
        if(instance == null) {
            instance = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()).build().create(ApiService.class);
        }
        return instance;
    }
}
