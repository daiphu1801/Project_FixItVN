package com.fixit.feature.worker.orders.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WorkerScheduleResponseDto {

    @SerializedName("date")
    private String date;

    @SerializedName("totalItems")
    private Integer totalItems;

    @SerializedName("empty")
    private Boolean empty;

    @SerializedName("items")
    private List<ScheduleItemDto> items;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }

    public Boolean getEmpty() { return empty; }
    public void setEmpty(Boolean empty) { this.empty = empty; }

    public List<ScheduleItemDto> getItems() { return items; }
    public void setItems(List<ScheduleItemDto> items) { this.items = items; }

    public static class ScheduleItemDto {
        @SerializedName("bookingId")
        private String bookingId;

        @SerializedName("serviceName")
        private String serviceName;

        @SerializedName("customerName")
        private String customerName;

        @SerializedName("address")
        private String address;

        @SerializedName("status")
        private String status;

        @SerializedName("statusText")
        private String statusText;

        @SerializedName("scheduledTime")
        private String scheduledTime;

        @SerializedName("finalPrice")
        private Double finalPrice;

        @SerializedName("paymentMethod")
        private String paymentMethod;

        @SerializedName("issueDescription")
        private String issueDescription;

        public String getBookingId() { return bookingId; }
        public void setBookingId(String bookingId) { this.bookingId = bookingId; }

        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getStatusText() { return statusText; }
        public void setStatusText(String statusText) { this.statusText = statusText; }

        public String getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

        public Double getFinalPrice() { return finalPrice; }
        public void setFinalPrice(Double finalPrice) { this.finalPrice = finalPrice; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public String getIssueDescription() { return issueDescription; }
        public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }
    }
}
