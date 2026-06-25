package com.fixit.feature.customer.complaint.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ComplaintResponseDto {
    @SerializedName("id")
    private String id;

    @SerializedName("bookingId")
    private String bookingId;

    @SerializedName("customerReason")
    private String customerReason;

    @SerializedName("workerResponse")
    private String workerResponse;

    @SerializedName("evidenceImageUrls")
    private List<String> evidenceImageUrls;

    @SerializedName("workerEvidenceImageUrls")
    private List<String> workerEvidenceImageUrls;

    @SerializedName("status")
    private String status;

    @SerializedName("deadlineToRespond")
    private String deadlineToRespond;

    @SerializedName("createdAt")
    private String createdAt;

    public ComplaintResponseDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getCustomerReason() { return customerReason; }
    public void setCustomerReason(String customerReason) { this.customerReason = customerReason; }

    public String getWorkerResponse() { return workerResponse; }
    public void setWorkerResponse(String workerResponse) { this.workerResponse = workerResponse; }

    public List<String> getEvidenceImageUrls() { return evidenceImageUrls; }
    public void setEvidenceImageUrls(List<String> evidenceImageUrls) { this.evidenceImageUrls = evidenceImageUrls; }

    public List<String> getWorkerEvidenceImageUrls() { return workerEvidenceImageUrls; }
    public void setWorkerEvidenceImageUrls(List<String> workerEvidenceImageUrls) { this.workerEvidenceImageUrls = workerEvidenceImageUrls; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeadlineToRespond() { return deadlineToRespond; }
    public void setDeadlineToRespond(String deadlineToRespond) { this.deadlineToRespond = deadlineToRespond; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
