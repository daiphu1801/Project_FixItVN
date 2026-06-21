package com.fixit.feature.upload.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WorkerKycResponse {

    @SerializedName("kycId")
    private String kycId;

    @SerializedName("status")
    private String status;

    @SerializedName("frontImageUrl")
    private String frontImageUrl;

    @SerializedName("backImageUrl")
    private String backImageUrl;

    @SerializedName("selfieImageUrl")
    private String selfieImageUrl;

    @SerializedName("ocrFullName")
    private String ocrFullName;

    @SerializedName("ocrIdentityCard")
    private String ocrIdentityCard;

    @SerializedName("similarityScore")
    private Double similarityScore;

    @SerializedName("certificateUrls")
    private List<String> certificateUrls;

    public String getKycId() {
        return kycId;
    }

    public String getStatus() {
        return status;
    }

    public String getFrontImageUrl() {
        return frontImageUrl;
    }

    public String getBackImageUrl() {
        return backImageUrl;
    }

    public String getSelfieImageUrl() {
        return selfieImageUrl;
    }

    public String getOcrFullName() {
        return ocrFullName;
    }

    public String getOcrIdentityCard() {
        return ocrIdentityCard;
    }

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public List<String> getCertificateUrls() {
        return certificateUrls;
    }
}
