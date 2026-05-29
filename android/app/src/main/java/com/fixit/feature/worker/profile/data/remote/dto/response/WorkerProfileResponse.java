package com.fixit.feature.worker.profile.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class WorkerProfileResponse {

    @SerializedName("workerId")
    private String workerId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("email")
    private String email;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("identityCard")
    private String identityCard;

    @SerializedName("verificationStatus")
    private String verificationStatus;

    @SerializedName("available")
    private Boolean available;

    @SerializedName("reputationScore")
    private BigDecimal reputationScore;

    @SerializedName("experienceDescription")
    private String experienceDescription;

    @SerializedName("serviceArea")
    private String serviceArea;

    public String getWorkerId() {
        return workerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public Boolean getAvailable() {
        return available;
    }

    public BigDecimal getReputationScore() {
        return reputationScore;
    }

    public String getExperienceDescription() {
        return experienceDescription;
    }

    public String getServiceArea() {
        return serviceArea;
    }
}