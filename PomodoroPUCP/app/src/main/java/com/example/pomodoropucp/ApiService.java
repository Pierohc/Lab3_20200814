package com.example.pomodoropucp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("todos/user/{userId}")
    Call<List<Tarea>> getUserTasks(@Path("userId") int userId);
}
