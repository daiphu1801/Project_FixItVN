package com.fixit.feature.worker.profile.domain.model;

public class WorkerProfileUpdateInput {

    private final String fullName;
    private final String email;
    private final String avatarUrl;
    private final String experienceDescription;
    private final String serviceArea;
    private final Double latitude;
    private final Double longitude;

    public WorkerProfileUpdateInput(
            String fullName,
            String email,
            String avatarUrl,
            String experienceDescription,
            String serviceArea,
            Double latitude,
            Double longitude
    ) {
        this.fullName = fullName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.experienceDescription = experienceDescription;
        this.serviceArea = serviceArea;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}