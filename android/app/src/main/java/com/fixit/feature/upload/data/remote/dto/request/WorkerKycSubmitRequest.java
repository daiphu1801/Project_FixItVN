package com.fixit.feature.upload.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkerKycSubmitRequest {

    @SerializedName("frontImageUploadId")
    private final String frontImageUploadId;

    @SerializedName("backImageUploadId")
    private final String backImageUploadId;

    @SerializedName("selfieImageUploadId")
    private final String selfieImageUploadId;

    @SerializedName("certificateUploadIds")
    private final List<String> certificateUploadIds;

    public WorkerKycSubmitRequest(
            String frontImageUploadId,
            String backImageUploadId,
            String selfieImageUploadId,
            List<String> certificateUploadIds
    ) {
        this.frontImageUploadId = frontImageUploadId;
        this.backImageUploadId = backImageUploadId;
        this.selfieImageUploadId = selfieImageUploadId;
        this.certificateUploadIds = certificateUploadIds;
    }
}
