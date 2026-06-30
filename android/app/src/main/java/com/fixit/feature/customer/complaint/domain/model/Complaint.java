package com.fixit.feature.customer.complaint.domain.model;

import java.util.List;

public class Complaint {
    private String id;
    private String bookingId;
    private String customerReason;
    private String workerResponse;
    private List<String> customerEvidenceUrls;
    private List<String> workerEvidenceUrls;
    private String status; // PENDING, WORKER_RESPONDED, RESOLVED
    private String deadlineToRespond;
    private String createdAt;

    public Complaint() {}

    public Complaint(String id, String bookingId, String customerReason, String workerResponse,
                     List<String> customerEvidenceUrls, List<String> workerEvidenceUrls,
                     String status, String deadlineToRespond, String createdAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.customerReason = customerReason;
        this.workerResponse = workerResponse;
        this.customerEvidenceUrls = customerEvidenceUrls;
        this.workerEvidenceUrls = workerEvidenceUrls;
        this.status = status;
        this.deadlineToRespond = deadlineToRespond;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getCustomerReason() { return customerReason; }
    public void setCustomerReason(String customerReason) { this.customerReason = customerReason; }

    public String getWorkerResponse() { return workerResponse; }
    public void setWorkerResponse(String workerResponse) { this.workerResponse = workerResponse; }

    public List<String> getCustomerEvidenceUrls() { return customerEvidenceUrls; }
    public void setCustomerEvidenceUrls(List<String> customerEvidenceUrls) { this.customerEvidenceUrls = customerEvidenceUrls; }

    public List<String> getWorkerEvidenceUrls() { return workerEvidenceUrls; }
    public void setWorkerEvidenceUrls(List<String> workerEvidenceUrls) { this.workerEvidenceUrls = workerEvidenceUrls; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeadlineToRespond() { return deadlineToRespond; }
    public void setDeadlineToRespond(String deadlineToRespond) { this.deadlineToRespond = deadlineToRespond; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
