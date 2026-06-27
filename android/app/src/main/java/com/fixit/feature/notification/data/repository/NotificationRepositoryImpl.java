package com.fixit.feature.notification.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.notification.data.remote.api.NotificationApi;
import com.fixit.feature.notification.data.remote.dto.DeviceTokenRequest;
import com.fixit.feature.notification.domain.repository.NotificationRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationApi notificationApi;

    @Inject
    public NotificationRepositoryImpl(NotificationApi notificationApi) {
        this.notificationApi = notificationApi;
    }

    @Override
    public void registerDeviceToken(String deviceToken, String deviceOs, ResultCallback<Void> callback) {
        notificationApi.registerDeviceToken(new DeviceTokenRequest(deviceToken, deviceOs))
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            callback.onResult(Result.success(null));
                        } else {
                            String errorMsg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                            callback.onResult(Result.error(new AppError("Đăng ký token thất bại: " + errorMsg)));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        callback.onResult(Result.error(new AppError("Lỗi kết nối khi đăng ký token: " + t.getMessage(), t)));
                    }
                });
    }

    @Override
    public void removeDeviceToken(String deviceToken, ResultCallback<Void> callback) {
        notificationApi.removeDeviceToken(deviceToken)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            callback.onResult(Result.success(null));
                        } else {
                            String errorMsg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                            callback.onResult(Result.error(new AppError("Xóa token thất bại: " + errorMsg)));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        callback.onResult(Result.error(new AppError("Lỗi kết nối khi xóa token: " + t.getMessage(), t)));
                    }
                });
    }
}
