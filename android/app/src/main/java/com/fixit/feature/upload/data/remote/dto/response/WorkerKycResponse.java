package com.fixit.feature.upload.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkerKycResponse {

    @SerializedName("kycId")
    private String kycId;

    @SerializedName("status")
    private String status;

    @SerializedName("certificateUrls")
    private List<String> certificateUrls;

    public String getKycId() {
        return kycId;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getCertificateUrls() {
        return certificateUrls;
    }
}
