package com.fixit.feature.upload.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

public class UserAvatarUpdateRequest {

    @SerializedName("uploadId")
    private final String uploadId;

    public UserAvatarUpdateRequest(String uploadId) {
        this.uploadId = uploadId;
    }
}
