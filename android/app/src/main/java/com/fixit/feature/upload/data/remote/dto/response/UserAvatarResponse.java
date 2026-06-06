package com.fixit.feature.upload.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

public class UserAvatarResponse {

    @SerializedName("userId")
    private String userId;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    public String getUserId() {
        return userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
