package com.fixit.feature.auth.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.storage.SessionStorage;
import com.fixit.feature.auth.data.remote.api.AuthApi;
import com.fixit.feature.auth.data.remote.dto.AuthRequest;
import com.fixit.feature.auth.data.remote.dto.AuthResponse;
import com.fixit.feature.auth.data.remote.mapper.AuthMapper;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.model.User;
import com.fixit.feature.auth.domain.model.UserRole;
import com.fixit.feature.auth.domain.repository.AuthRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AuthRepositoryImpl implements AuthRepository {
    private final AuthApi authApi;
    private final SessionStorage sessionStorage;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Inject
    public AuthRepositoryImpl(AuthApi authApi, SessionStorage sessionStorage) {
        this.authApi = authApi;
        this.sessionStorage = sessionStorage;
    }

    @Override
    public void login(String phone, String password, String role, ResultCallback<Session> callback) {
        mainHandler.postDelayed(() -> {
            User user = new User(
                    "mock_id_123",
                    phone,
                    "NgÆ°á»i dÃ¹ng Thá»­ nghiá»‡m",
                    UserRole.from(role)
            );
            Session session = new Session("mock_access_token_xyz", "mock_refresh_token_abc", user);
            sessionStorage.saveSession(session);
            callback.onResult(Result.success(session));
        }, 1000);
    }

    @Override
    public void register(String phone, String password, String fullName, String role, ResultCallback<Session> callback) {
        authApi.register(new AuthRequest.Register(phone, password, fullName, role)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    Session session = AuthMapper.toSession(response.body());
                    if (session != null) {
                        sessionStorage.saveSession(session);
                        callback.onResult(Result.success(session));
                    } else {
                        callback.onResult(Result.error(new AppError("ÄÄƒng kÃ½ tháº¥t báº¡i: dá»¯ liá»‡u pháº£n há»“i khÃ´ng há»£p lá»‡")));
                    }
                } else {
                    callback.onResult(Result.error(new AppError("ÄÄƒng kÃ½ tháº¥t báº¡i: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onResult(Result.error(new AppError("Lá»—i káº¿t ná»‘i: " + t.getMessage(), t)));
            }
        });
    }

    @Override
    public void logout(ResultCallback<Void> callback) {
        sessionStorage.clear();
        callback.onResult(Result.success(null));
    }

    @Override
    public Session getCurrentSession() {
        return sessionStorage.getSession();
    }
}
