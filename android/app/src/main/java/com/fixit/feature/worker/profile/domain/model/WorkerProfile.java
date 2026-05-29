package com.fixit.feature.worker.profile.domain.model;

public class WorkerProfile {

    private final String workerId;
    private final String fullName;
    private final String phoneNumber;
    private final String email;
    private final String avatarUrl;
    private final String identityCard;
    private final String verificationStatus;
    private final boolean available;
    private final double reputationScore;
    private final String experienceDescription;
    private final String serviceArea;

    public WorkerProfile(
            String workerId,
            String fullName,
            String phoneNumber,
            String email,
            String avatarUrl,
            String identityCard,
            String verificationStatus,
            boolean available,
            double reputationScore,
            String experienceDescription,
            String serviceArea
    ) {
        this.workerId = workerId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.identityCard = identityCard;
        this.verificationStatus = verificationStatus;
        this.available = available;
        this.reputationScore = reputationScore;
        this.experienceDescription = experienceDescription;
        this.serviceArea = serviceArea;
    }

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

    public boolean isAvailable() {
        return available;
    }

    public double getReputationScore() {
        return reputationScore;
    }

    public String getExperienceDescription() {
        return experienceDescription;
    }

    public String getServiceArea() {
        return serviceArea;
    }
}