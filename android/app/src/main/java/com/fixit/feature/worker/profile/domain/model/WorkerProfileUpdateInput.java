package com.fixit.feature.worker.profile.domain.model;

public class WorkerProfileUpdateInput {

    private final String fullName;
    private final String email;
    private final String avatarUrl;
    private final String experienceDescription;
    private final String serviceArea;

    public WorkerProfileUpdateInput(
            String fullName,
            String email,
            String avatarUrl,
            String experienceDescription,
            String serviceArea
    ) {
        this.fullName = fullName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.experienceDescription = experienceDescription;
        this.serviceArea = serviceArea;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getExperienceDescription() {
        return experienceDescription;
    }

    public String getServiceArea() {
        return serviceArea;
    }
}