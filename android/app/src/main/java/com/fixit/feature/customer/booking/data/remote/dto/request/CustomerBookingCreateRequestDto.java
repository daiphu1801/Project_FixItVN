package com.fixit.feature.customer.booking.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class CustomerBookingCreateRequestDto {
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

    public CustomerBookingCreateRequestDto(Integer serviceId, String address, BigDecimal destinationLat, BigDecimal destinationLng, String issueDescription, String paymentMethod) {
        this.serviceId = serviceId;
        this.address = address;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.issueDescription = issueDescription;
        this.paymentMethod = paymentMethod;
    }

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
}
