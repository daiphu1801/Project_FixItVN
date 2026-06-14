package com.fixit.feature.customer.booking.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class CustomerBookingResponseDto {
    @SerializedName("bookingId")
    private String bookingId;

    @SerializedName("serviceId")
    private Integer serviceId;

    @SerializedName("address")
    private String address;

    @SerializedName("destinationLat")
    private BigDecimal destinationLat;

    @SerializedName("destinationLng")
    private BigDecimal destinationLng;

    @SerializedName("issueDescription")
    private String issueDescription;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("worker")
    private BookingWorkerInfoDto worker;

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getDestinationLat() { return destinationLat; }
    public void setDestinationLat(BigDecimal destinationLat) { this.destinationLat = destinationLat; }

    public BigDecimal getDestinationLng() { return destinationLng; }
    public void setDestinationLng(BigDecimal destinationLng) { this.destinationLng = destinationLng; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public BookingWorkerInfoDto getWorker() { return worker; }
    public void setWorker(BookingWorkerInfoDto worker) { this.worker = worker; }
}
