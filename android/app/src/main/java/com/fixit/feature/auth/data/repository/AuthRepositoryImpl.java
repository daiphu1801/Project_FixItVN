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
    public void login(String identifier, String password, String role, ResultCallback<Session> callback) {
        authApi.login(new AuthRequest.Login(identifier, password))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (!response.isSuccessful()) {
                            callback.onResult(Result.error(
                                    new AppError("Đăng nhập thất bại. HTTP " + response.code())
                            ));
                            return;
                        }

                        Session session = AuthMapper.toSession(response.body());
                        if (session == null) {
                            callback.onResult(Result.error(
                                    new AppError("Dữ liệu đăng nhập không hợp lệ")
                            ));
                            return;
                        }

                        sessionStorage.saveSession(session);
                        callback.onResult(Result.success(session));
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        callback.onResult(Result.error(
                                new AppError("Lỗi kết nối đăng nhập: " + t.getMessage(), t)
                        ));
                    }
                });
    }

    @Override
    public void register(String phone, String email, String password, String fullName, String role, ResultCallback<Session> callback) {
        authApi.register(new AuthRequest.Register(phone, email, password, fullName, role)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    Session session = AuthMapper.toSession(response.body());
                    if (session != null) {
                        sessionStorage.saveSession(session);
                        callback.onResult(Result.success(session));
                    } else {
                        callback.onResult(Result.error(new AppError("Đăng ký thất bại: dữ liệu phản hồi không hợp lệ")));
                    }
                } else {
                    callback.onResult(Result.error(new AppError("Đăng ký thất bại: " + response.code())));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
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
