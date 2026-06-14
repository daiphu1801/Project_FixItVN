package com.fixit.feature.auth.data.remote.api;

import com.fixit.feature.auth.data.remote.dto.AuthRequest;
import com.fixit.feature.auth.data.remote.dto.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {
    @POST("api/v1/auth/login")
    Call<AuthResponse> login(@Body AuthRequest.Login request);

    @POST("api/v1/auth/register")
    Call<AuthResponse> register(@Body AuthRequest.Register request);

    @POST("api/v1/auth/login/google")
    Call<AuthResponse> loginWithGoogle(@Body AuthRequest.GoogleLogin request);

    @POST("api/v1/auth/logout")
    Call<Void> logout(@Query("refreshToken") String refreshToken);

    @POST("api/v1/auth/refresh-token")
    Call<AuthResponse> refreshToken(@Body AuthRequest.RefreshToken request);

    @POST("api/v1/auth/otp/send")
    Call<String> sendOtp(@Body AuthRequest.SendOtp request);

    @POST("api/v1/auth/otp/verify")
    Call<AuthResponse> verifyOtp(@Body AuthRequest.VerifyOtp request);

    @POST("api/v1/auth/forgot-password")
    Call<String> forgotPassword(@Body AuthRequest.SendOtp request);

    @POST("api/v1/auth/reset-password")
    Call<String> resetPassword(@Body AuthRequest.ResetPassword request);

    @PATCH("api/v1/auth/change-password")
    Call<String> changePassword(@Body AuthRequest.ChangePassword request);

    @GET("api/v1/users/me")
    Call<AuthResponse.UserInfo> getCurrentUser();

    @PATCH("api/v1/users/me")
    Call<AuthResponse.UserInfo> updateCurrentUser(@Body AuthRequest.UpdateCurrentUser request);

    @GET("ping")
    Call<String> pingServer();
}
