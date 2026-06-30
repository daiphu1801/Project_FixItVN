package com.fixit.feature.worker.profile.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

public class WorkerProfileUpdateRequest {

    @SerializedName("fullName")
    private final String fullName;

    @SerializedName("email")
    private final String email;

    @SerializedName("avatarUrl")
    private final String avatarUrl;

    @SerializedName("experienceDescription")
    private final String experienceDescription;

    @SerializedName("serviceArea")
    private final String serviceArea;

    @SerializedName("latitude")
    private final Double latitude;

    @SerializedName("longitude")
    private final Double longitude;

    public WorkerProfileUpdateRequest(
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
}