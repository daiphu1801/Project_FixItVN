package com.fixit.feature.upload.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

public class ProofOfWorkResponse {

    @SerializedName("proofId")
    private String proofId;

    @SerializedName("bookingId")
    private String bookingId;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("proofType")
    private String proofType;

    public String getProofId() {
        return proofId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getProofType() {
        return proofType;
    }
}
