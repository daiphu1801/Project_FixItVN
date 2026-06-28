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

    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("laborCost")
    private BigDecimal laborCost;

    @SerializedName("materialCost")
    private BigDecimal materialCost;

    @SerializedName("cancellationReason")
    private String cancellationReason;

    @SerializedName("finalPrice")
    private BigDecimal finalPrice;

    @SerializedName("quotationId")
    private String quotationId;

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

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public BigDecimal getLaborCost() { return laborCost; }
    public void setLaborCost(BigDecimal laborCost) { this.laborCost = laborCost; }

    public BigDecimal getMaterialCost() { return materialCost; }
    public void setMaterialCost(BigDecimal materialCost) { this.materialCost = materialCost; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }

    public String getQuotationId() { return quotationId; }
    public void setQuotationId(String quotationId) { this.quotationId = quotationId; }

    @SerializedName("quotationStatus")
    private String quotationStatus;

    public String getQuotationStatus() { return quotationStatus; }
    public void setQuotationStatus(String quotationStatus) { this.quotationStatus = quotationStatus; }

    @SerializedName("paymentCode")
    private String paymentCode;

    public String getPaymentCode() { return paymentCode; }
    public void setPaymentCode(String paymentCode) { this.paymentCode = paymentCode; }

    public BookingWorkerInfoDto getWorker() { return worker; }
    public void setWorker(BookingWorkerInfoDto worker) { this.worker = worker; }
}
