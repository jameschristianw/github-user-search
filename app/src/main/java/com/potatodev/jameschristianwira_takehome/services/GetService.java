package com.potatodev.jameschristianwira_takehome.services;

import com.potatodev.jameschristianwira_takehome.models.Results;
import com.potatodev.jameschristianwira_takehome.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetService {

    @GET("search/users?per_page=100")
    Call<Results> getUsers(@Query("q") String username, @Query("page") int page);

    @GET("search/users?q=jameschristian&page=1&per_page=20")
    Call<Results> getTestUser();

}
