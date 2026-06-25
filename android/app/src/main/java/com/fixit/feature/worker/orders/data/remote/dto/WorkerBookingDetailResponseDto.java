package com.fixit.feature.worker.orders.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WorkerBookingDetailResponseDto {

    @SerializedName("bookingId")
    private String bookingId;

    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerPhone")
    private String customerPhone;

    @SerializedName("customerAvatar")
    private String customerAvatar;

    @SerializedName("address")
    private String address;

    @SerializedName("destinationLat")
    private Double destinationLat;

    @SerializedName("destinationLng")
    private Double destinationLng;

    @SerializedName("issueDescription")
    private String issueDescription;

    @SerializedName("scheduledTime")
    private String scheduledTime;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("finalPrice")
    private Double finalPrice;

    @SerializedName("status")
    private String status;

    @SerializedName("statusText")
    private String statusText;

    @SerializedName("nextAction")
    private String nextAction;

    @SerializedName("doneActions")
    private List<String> doneActions;

    @SerializedName("proofOfWorks")
    private List<com.fixit.feature.upload.data.remote.dto.response.ProofOfWorkResponse> proofOfWorks;

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerAvatar() { return customerAvatar; }
    public void setCustomerAvatar(String customerAvatar) { this.customerAvatar = customerAvatar; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getDestinationLat() { return destinationLat; }
    public void setDestinationLat(Double destinationLat) { this.destinationLat = destinationLat; }

    public Double getDestinationLng() { return destinationLng; }
    public void setDestinationLng(Double destinationLng) { this.destinationLng = destinationLng; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(Double finalPrice) { this.finalPrice = finalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }

    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }

    public List<String> getDoneActions() { return doneActions; }
    public void setDoneActions(List<String> doneActions) { this.doneActions = doneActions; }

    public List<com.fixit.feature.upload.data.remote.dto.response.ProofOfWorkResponse> getProofOfWorks() { return proofOfWorks; }
    public void setProofOfWorks(List<com.fixit.feature.upload.data.remote.dto.response.ProofOfWorkResponse> proofOfWorks) { this.proofOfWorks = proofOfWorks; }
}
