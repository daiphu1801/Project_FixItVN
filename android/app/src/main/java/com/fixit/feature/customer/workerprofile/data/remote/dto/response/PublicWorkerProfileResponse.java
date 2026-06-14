package com.fixit.feature.customer.workerprofile.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class PublicWorkerProfileResponse {
    @SerializedName("workerId")
    private String workerId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("reputationScore")
    private BigDecimal reputationScore;

    @SerializedName("totalReviews")
    private Integer totalReviews;

    @SerializedName("experienceDescription")
    private String experienceDescription;

    @SerializedName("serviceArea")
    private String serviceArea;

    @SerializedName("available")
    private Boolean available;

    // Getters and Setters
    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public BigDecimal getReputationScore() { return reputationScore; }
    public void setReputationScore(BigDecimal reputationScore) { this.reputationScore = reputationScore; }
    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }
    public String getExperienceDescription() { return experienceDescription; }
    public void setExperienceDescription(String experienceDescription) { this.experienceDescription = experienceDescription; }
    public String getServiceArea() { return serviceArea; }
    public void setServiceArea(String serviceArea) { this.serviceArea = serviceArea; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}
