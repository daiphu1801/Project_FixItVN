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
    private String serviceName;
    private BigDecimal laborCost;
    private BigDecimal materialCost;
    private String cancellationReason;
    private BigDecimal finalPrice;
    private BookingWorkerInfo worker;

    public CustomerBooking(String bookingId, Integer serviceId, String address, BigDecimal destinationLat, BigDecimal destinationLng, String issueDescription, String paymentMethod, String status, String createdAt, String serviceName, BigDecimal laborCost, BigDecimal materialCost, String cancellationReason, BigDecimal finalPrice, BookingWorkerInfo worker) {
        this.bookingId = bookingId;
        this.serviceId = serviceId;
        this.address = address;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.issueDescription = issueDescription;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.createdAt = createdAt;
        this.serviceName = serviceName;
        this.laborCost = laborCost;
        this.materialCost = materialCost;
        this.cancellationReason = cancellationReason;
        this.finalPrice = finalPrice;
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
    public String getServiceName() { return serviceName; }
    public BigDecimal getLaborCost() { return laborCost; }
    public BigDecimal getMaterialCost() { return materialCost; }
    public String getCancellationReason() { return cancellationReason; }
    public BigDecimal getFinalPrice() { return finalPrice; }
    public BookingWorkerInfo getWorker() { return worker; }
}
