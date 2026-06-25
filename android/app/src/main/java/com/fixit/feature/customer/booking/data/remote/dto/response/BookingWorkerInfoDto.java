package com.fixit.feature.customer.booking.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

public class BookingWorkerInfoDto {
    @SerializedName("workerId")
    private String workerId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("avatarUrl")
    private String avatarUrl;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
