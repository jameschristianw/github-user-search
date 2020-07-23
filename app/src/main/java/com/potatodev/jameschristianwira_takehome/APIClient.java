package com.potatodev.jameschristianwira_takehome;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    public static Retrofit retrofit;
    public static final String BASE_URL = "https://api.github.com";

    public static Retrofit getRetrofitInstance(){
        if (retrofit == null){
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
