package com.fixit.feature.upload.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkerKycSubmitRequest {

    @SerializedName("frontImageUploadId")
    private final String frontImageUploadId;

    @SerializedName("backImageUploadId")
    private final String backImageUploadId;

    @SerializedName("certificateUploadIds")
    private final List<String> certificateUploadIds;

    public WorkerKycSubmitRequest(
            String frontImageUploadId,
            String backImageUploadId,
            List<String> certificateUploadIds
    ) {
        this.frontImageUploadId = frontImageUploadId;
        this.backImageUploadId = backImageUploadId;
        this.certificateUploadIds = certificateUploadIds;
    }
}
