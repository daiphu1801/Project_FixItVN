package com.fixit.feature.customer.booking.domain.model;

public class BookingWorkerInfo {
    private String workerId;
    private String fullName;
    private String avatarUrl;
    private String phoneNumber;

    public BookingWorkerInfo(String workerId, String fullName, String avatarUrl, String phoneNumber) {
        this.workerId = workerId;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.phoneNumber = phoneNumber;
    }

    public String getWorkerId() { return workerId; }
    public String getFullName() { return fullName; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getPhoneNumber() { return phoneNumber; }
}
