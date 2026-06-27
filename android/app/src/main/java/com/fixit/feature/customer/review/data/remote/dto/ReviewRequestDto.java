package com.fixit.feature.customer.review.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ReviewRequestDto {
    @SerializedName("rating")
    private final int rating;

    @SerializedName("comment")
    private final String comment;

    public ReviewRequestDto(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
