package com.fixit.feature.auth.data.remote.api;

import com.fixit.feature.auth.data.remote.dto.AuthRequest;
import com.fixit.feature.auth.data.remote.dto.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/v1/auth/login")
    Call<AuthResponse> login(@Body AuthRequest.Login request);

    @POST("api/v1/auth/register")
    Call<AuthResponse> register(@Body AuthRequest.Register request);

    @GET("ping")
    Call<String> pingServer();
}
