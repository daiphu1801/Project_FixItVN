package com.fixit.feature.notification.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.notification.data.remote.dto.DeviceTokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationApi {

    @POST("api/v1/users/me/device-tokens")
    Call<ApiResponse<Void>> registerDeviceToken(@Body DeviceTokenRequest request);

    @DELETE("api/v1/users/me/device-tokens/{deviceToken}")
    Call<ApiResponse<Void>> removeDeviceToken(@Path("deviceToken") String deviceToken);
}
