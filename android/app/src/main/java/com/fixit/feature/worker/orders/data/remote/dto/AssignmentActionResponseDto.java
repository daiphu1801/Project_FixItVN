package com.fixit.feature.worker.orders.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class AssignmentActionResponseDto {

    @SerializedName("bookingId")
    private String bookingId;

    @SerializedName("assignmentId")
    private String assignmentId;

    @SerializedName("assignmentStatus")
    private String assignmentStatus;

    @SerializedName("bookingStatus")
    private String bookingStatus;

    @SerializedName("nextAction")
    private String nextAction;

    @SerializedName("message")
    private String message;

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }

    public String getAssignmentStatus() { return assignmentStatus; }
    public void setAssignmentStatus(String assignmentStatus) { this.assignmentStatus = assignmentStatus; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getNextAction() { return nextAction; }
    public void setNextAction(String nextAction) { this.nextAction = nextAction; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
