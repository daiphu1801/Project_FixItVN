package com.fixit.feature.auth.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
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
        authApi.login(new AuthRequest.Login(identifier, password, role))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (!response.isSuccessful()) {
                            ApiResponse<?> apiResponse = ApiResponse.parseError(response);
                            String errorMessage = (apiResponse != null && apiResponse.getMessage() != null)
                                    ? apiResponse.getMessage()
                                    : "Đăng nhập thất bại. HTTP " + response.code();
                            callback.onResult(Result.error(new AppError(errorMessage)));
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
                    ApiResponse<?> apiResponse = ApiResponse.parseError(response);
                    String errorMessage = (apiResponse != null && apiResponse.getMessage() != null)
                            ? apiResponse.getMessage()
                            : "Đăng ký thất bại: " + response.code();
                    callback.onResult(Result.error(new AppError(errorMessage)));
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
        String refreshToken = sessionStorage.getRefreshToken();
        sessionStorage.clear();
        if (refreshToken != null && !refreshToken.isEmpty()) {
            authApi.logout(refreshToken).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    callback.onResult(Result.success(null));
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    callback.onResult(Result.success(null));
                }
            });
        } else {
            callback.onResult(Result.success(null));
        }
    }

    @Override
    public void refreshToken(String refreshToken, ResultCallback<Session> callback) {
        authApi.refreshToken(new AuthRequest.RefreshToken(refreshToken))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (!response.isSuccessful()) {
                            callback.onResult(Result.error(
                                    new AppError("Refresh token thất bại. HTTP " + response.code())
                            ));
                            return;
                        }

                        Session session = AuthMapper.toSession(response.body());
                        if (session == null) {
                            callback.onResult(Result.error(
                                    new AppError("Dữ liệu refresh token không hợp lệ")
                            ));
                            return;
                        }

                        sessionStorage.saveSession(session);
                        callback.onResult(Result.success(session));
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        callback.onResult(Result.error(
                                new AppError("Lỗi kết nối refresh token: " + t.getMessage(), t)
                        ));
                    }
                });
    }

    @Override
    public Session getCurrentSession() {
        return sessionStorage.getSession();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, ResultCallback<Void> callback) {
        authApi.changePassword(new AuthRequest.ChangePassword(oldPassword, newPassword)).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onResult(Result.success(null));
                } else {
                    ApiResponse<?> apiResponse = ApiResponse.parseError(response);
                    String errorMessage = (apiResponse != null && apiResponse.getMessage() != null)
                            ? apiResponse.getMessage()
                            : "Đổi mật khẩu thất bại: " + response.code();
                    callback.onResult(Result.error(new AppError(errorMessage)));
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
            }
        });
    }

    @Override
    public void loginWithGoogle(String idToken, String role, ResultCallback<Session> callback) {
        authApi.loginWithGoogle(new AuthRequest.GoogleLogin(idToken, role))
                .enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (!response.isSuccessful()) {
                            ApiResponse<?> apiResponse = ApiResponse.parseError(response);
                            String errorMessage = (apiResponse != null && apiResponse.getMessage() != null)
                                    ? apiResponse.getMessage()
                                    : "Đăng nhập Google thất bại. HTTP " + response.code();
                            callback.onResult(Result.error(new AppError(errorMessage)));
                            return;
                        }

                        Session session = AuthMapper.toSession(response.body());
                        if (session == null) {
                            callback.onResult(Result.error(
                                    new AppError("Dữ liệu đăng nhập Google không hợp lệ")
                            ));
                            return;
                        }

                        sessionStorage.saveSession(session);
                        callback.onResult(Result.success(session));
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        callback.onResult(Result.error(
                                new AppError("Lỗi kết nối đăng nhập Google: " + t.getMessage(), t)
                        ));
                    }
                });
    }

    @Override
    public void forgotPassword(String email, String phone, ResultCallback<Void> callback) {
        authApi.forgotPassword(new AuthRequest.ForgotPassword(email, phone))
                .enqueue(new Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                        if (response.isSuccessful()) {
                            callback.onResult(Result.success(null));
                        } else {
                            ApiResponse<?> apiResponse = ApiResponse.parseError(response);
                            String errorMessage = (apiResponse != null && apiResponse.getMessage() != null)
                                    ? apiResponse.getMessage()
                                    : "Gửi yêu cầu thất bại: " + response.code();
                            callback.onResult(Result.error(new AppError(errorMessage)));
                        }
                    }

                    @Override
                    public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                        callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
                    }
                });
    }

    @Override
    public void resetPassword(String email, String phone, String otpCode, String newPassword, ResultCallback<Void> callback) {
        authApi.resetPassword(new AuthRequest.ResetPassword(email, phone, otpCode, newPassword))
                .enqueue(new Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                        if (response.isSuccessful()) {
                            callback.onResult(Result.success(null));
                        } else {
                            ApiResponse<?> apiResponse = ApiResponse.parseError(response);
                            String errorMessage = (apiResponse != null && apiResponse.getMessage() != null)
                                    ? apiResponse.getMessage()
                                    : "Đặt lại mật khẩu thất bại: " + response.code();
                            callback.onResult(Result.error(new AppError(errorMessage)));
                        }
                    }

                    @Override
                    public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                        callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
                    }
                });
    }
}
