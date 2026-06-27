package com.fixit.feature.customer.review.domain.model;

public class Review {
    private final String id;
    private final String customerName;
    private final String customerAvatar;
    private final int rating;
    private final String comment;
    private final String createdAt;

    public Review(String id, String customerName, String customerAvatar, int rating, String comment, String createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.customerAvatar = customerAvatar;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getCustomerAvatar() { return customerAvatar; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
}
