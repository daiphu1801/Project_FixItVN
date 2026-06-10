package com.fixit.feature.worker.orders.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class BookingActionResponseDto {

    @SerializedName("bookingId")
    private String bookingId;

    @SerializedName("bookingStatus")
    private String bookingStatus;

    @SerializedName("action")
    private String action;

    @SerializedName("nextAction")
    private String nextAction;

    @SerializedName("message")
    private String message;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
