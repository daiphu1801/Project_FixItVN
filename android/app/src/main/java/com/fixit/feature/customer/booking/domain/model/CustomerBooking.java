package com.fixit.feature.customer.booking.domain.model;

import java.math.BigDecimal;

public class CustomerBooking {
    private String bookingId;
    private Integer serviceId;
    private String address;
    private BigDecimal destinationLat;
    private BigDecimal destinationLng;
    private String issueDescription;
    private String paymentMethod;
    private String status;
    private String createdAt;
    private BookingWorkerInfo worker;

    public CustomerBooking(String bookingId, Integer serviceId, String address, BigDecimal destinationLat, BigDecimal destinationLng, String issueDescription, String paymentMethod, String status, String createdAt, BookingWorkerInfo worker) {
        this.bookingId = bookingId;
        this.serviceId = serviceId;
        this.address = address;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.issueDescription = issueDescription;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.createdAt = createdAt;
        this.worker = worker;
    }

    public String getBookingId() { return bookingId; }
    public Integer getServiceId() { return serviceId; }
    public String getAddress() { return address; }
    public BigDecimal getDestinationLat() { return destinationLat; }
    public BigDecimal getDestinationLng() { return destinationLng; }
    public String getIssueDescription() { return issueDescription; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public BookingWorkerInfo getWorker() { return worker; }
}
