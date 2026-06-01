package com.fixit.feature.customer.review.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ReviewResponseDto {
    @SerializedName("id") private String id;
    @SerializedName("bookingId") private String bookingId;
    @SerializedName("customerId") private String customerId;
    @SerializedName("customerName") private String customerName;
    @SerializedName("customerAvatar") private String customerAvatar;
    @SerializedName("rating") private int rating;
    @SerializedName("comment") private String comment;
    @SerializedName("createdAt") private String createdAt;

    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerAvatar() { return customerAvatar; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
}
