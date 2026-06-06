package com.fixit.feature.upload.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

public class ProofOfWorkCreateRequest {

    @SerializedName("uploadId")
    private final String uploadId;

    @SerializedName("proofType")
    private final String proofType;

    public ProofOfWorkCreateRequest(String uploadId, String proofType) {
        this.uploadId = uploadId;
        this.proofType = proofType;
    }
}
