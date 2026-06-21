package com.fixit.feature.worker.kyc.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

public class VnptKycConfigResponse {

    @SerializedName("tokenId")
    private String tokenId;

    @SerializedName("tokenKey")
    private String tokenKey;

    @SerializedName("apiUrl")
    private String apiUrl;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
