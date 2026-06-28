package com.fixit.feature.worker.orders.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PendingAssignmentResponseDto {

    @SerializedName("totalItems")
    private Integer totalItems;

    @SerializedName("empty")
    private Boolean empty;

    @SerializedName("items")
    private List<PendingAssignmentItemDto> items;

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }

    public Boolean getEmpty() { return empty; }
    public void setEmpty(Boolean empty) { this.empty = empty; }

    public List<PendingAssignmentItemDto> getItems() { return items; }
    public void setItems(List<PendingAssignmentItemDto> items) { this.items = items; }

    public static class PendingAssignmentItemDto {
        @SerializedName("assignmentId")
        private String assignmentId;

        @SerializedName("bookingId")
        private String bookingId;

        @SerializedName("serviceName")
        private String serviceName;

        @SerializedName("customerName")
        private String customerName;

        @SerializedName("addressPreview")
        private String addressPreview;

        @SerializedName("issueDescription")
        private String issueDescription;

        @SerializedName("scheduledTime")
        private String scheduledTime;

        @SerializedName("assignedAt")
        private String assignedAt;

        @SerializedName("expiresAt")
        private String expiresAt;

        @SerializedName("remainingSeconds")
        private Integer remainingSeconds;

        @SerializedName("destinationLat")
        private Double destinationLat;

        @SerializedName("destinationLng")
        private Double destinationLng;

        @SerializedName("finalPrice")
        private Double finalPrice;

        @SerializedName("paymentMethod")
        private String paymentMethod;

        public String getAssignmentId() { return assignmentId; }
        public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }

        public String getBookingId() { return bookingId; }
        public void setBookingId(String bookingId) { this.bookingId = bookingId; }

        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getAddressPreview() { return addressPreview; }
        public void setAddressPreview(String addressPreview) { this.addressPreview = addressPreview; }

        public String getIssueDescription() { return issueDescription; }
        public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

        public String getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

        public String getAssignedAt() { return assignedAt; }
        public void setAssignedAt(String assignedAt) { this.assignedAt = assignedAt; }

        public String getExpiresAt() { return expiresAt; }
        public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }

        public Integer getRemainingSeconds() { return remainingSeconds; }
        public void setRemainingSeconds(Integer remainingSeconds) { this.remainingSeconds = remainingSeconds; }

        public Double getDestinationLat() { return destinationLat; }
        public void setDestinationLat(Double destinationLat) { this.destinationLat = destinationLat; }

        public Double getDestinationLng() { return destinationLng; }
        public void setDestinationLng(Double destinationLng) { this.destinationLng = destinationLng; }

        public Double getFinalPrice() { return finalPrice; }
        public void setFinalPrice(Double finalPrice) { this.finalPrice = finalPrice; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
}
