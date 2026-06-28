package com.fixit.feature.worker.orders.domain.model;

/**
 * Model đại diện cho một Đơn hàng của Thợ.
 *
 * Trạng thái (status):
 * "pending" → Chờ làm
 * "ongoing" → Đang làm
 * "completed" → Hoàn thành (Lịch sử)
 * "cancelled" → Đã huỷ (Lịch sử)
 */
public class WorkerOrder {

    private String orderId;
    private String customerId;
    private String serviceTitle;
    private String address;
    private String timeSlot; // Ví dụ: "Hôm nay 14:30"
    private String price; // Ví dụ: "150.000 đ"
    private String status; // "pending" | "ongoing" | "completed" | "cancelled"
    private String customerName;
    private String complaintStatus; // "none", "pending", "responded", "resolved"
    private String complaintReason;
    private String complaintDeadline; // Countdown timer string
    private JobStatus jobStatus;
    private String paymentMethod;
    private String issueDescription;
    private String customerPhone;
    private String customerAvatar;
    private Double destinationLat;
    private Double destinationLng;
    private Double finalPrice;

    public WorkerOrder(String orderId, String customerId, String serviceTitle, String address,
            String timeSlot, String price, String status, String customerName) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.serviceTitle = serviceTitle;
        this.address = address;
        this.timeSlot = timeSlot;
        this.price = price;
        this.status = status;
        this.customerName = customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public String getAddress() {
        return address;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(String complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public String getComplaintReason() {
        return complaintReason;
    }

    public void setComplaintReason(String complaintReason) {
        this.complaintReason = complaintReason;
    }

    public String getComplaintDeadline() {
        return complaintDeadline;
    }

    public void setComplaintDeadline(String complaintDeadline) {
        this.complaintDeadline = complaintDeadline;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerAvatar() {
        return customerAvatar;
    }

    public void setCustomerAvatar(String customerAvatar) {
        this.customerAvatar = customerAvatar;
    }

    public Double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(Double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public Double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(Double destinationLng) {
        this.destinationLng = destinationLng;
    }

    private String proofBeforeUrl;
    private String proofAfterUrl;

    public String getProofBeforeUrl() {
        return proofBeforeUrl;
    }

    public void setProofBeforeUrl(String proofBeforeUrl) {
        this.proofBeforeUrl = proofBeforeUrl;
    }

    public String getProofAfterUrl() {
        return proofAfterUrl;
    }

    public void setProofAfterUrl(String proofAfterUrl) {
        this.proofAfterUrl = proofAfterUrl;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }
}

