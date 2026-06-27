package com.fixit.feature.notification.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class DeviceTokenRequest {

    @SerializedName("deviceToken")
    private final String deviceToken;

    @SerializedName("deviceOs")
    private final String deviceOs;

    public DeviceTokenRequest(String deviceToken, String deviceOs) {
        this.deviceToken = deviceToken;
        this.deviceOs = deviceOs;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getDeviceOs() {
        return deviceOs;
    }
}
