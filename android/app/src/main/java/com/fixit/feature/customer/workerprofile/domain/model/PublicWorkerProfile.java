package com.fixit.feature.customer.workerprofile.domain.model;

import java.math.BigDecimal;

public class PublicWorkerProfile {
    private String workerId;
    private String fullName;
    private String avatarUrl;
    private BigDecimal reputationScore;
    private Integer totalReviews;
    private String experienceDescription;
    private String serviceArea;
    private Boolean available;

    public PublicWorkerProfile(String workerId, String fullName, String avatarUrl, BigDecimal reputationScore, Integer totalReviews, String experienceDescription, String serviceArea, Boolean available) {
        this.workerId = workerId;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.reputationScore = reputationScore;
        this.totalReviews = totalReviews;
        this.experienceDescription = experienceDescription;
        this.serviceArea = serviceArea;
        this.available = available;
    }

    // Getters
    public String getWorkerId() { return workerId; }
    public String getFullName() { return fullName; }
    public String getAvatarUrl() { return avatarUrl; }
    public BigDecimal getReputationScore() { return reputationScore; }
    public Integer getTotalReviews() { return totalReviews; }
    public String getExperienceDescription() { return experienceDescription; }
    public String getServiceArea() { return serviceArea; }
    public Boolean isAvailable() { return available; }
}
