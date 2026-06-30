package com.fixit.feature.customer.booking.domain.model;

public class BookingWorkerInfo {
    private String workerId;
    private String fullName;
    private String avatarUrl;
    private String phoneNumber;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;

    public BookingWorkerInfo(String workerId, String fullName, String avatarUrl, String phoneNumber,
                             java.math.BigDecimal latitude, java.math.BigDecimal longitude) {
        this.workerId = workerId;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getWorkerId() { return workerId; }
    public String getFullName() { return fullName; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getPhoneNumber() { return phoneNumber; }
    public java.math.BigDecimal getLatitude() { return latitude; }
    public java.math.BigDecimal getLongitude() { return longitude; }
}
