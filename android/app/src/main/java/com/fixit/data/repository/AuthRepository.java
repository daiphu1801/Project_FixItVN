package com.fixit.data.repository;

import com.fixit.data.remote.api.ApiService;
import com.fixit.data.remote.dto.AuthRequest;
import com.fixit.data.remote.dto.AuthResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthRepository {
    private final ApiService apiService;

    @Inject
    public AuthRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void login(String phone, String password, Callback<AuthResponse> callback) {
        apiService.login(new AuthRequest.Login(phone, password)).enqueue(callback);
    }

    public void register(String phone, String password, String fullName, String role, Callback<AuthResponse> callback) {
        apiService.register(new AuthRequest.Register(phone, password, fullName, role)).enqueue(callback);
    }
}
