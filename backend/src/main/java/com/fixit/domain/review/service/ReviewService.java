package com.fixit.domain.review.service;

import com.fixit.domain.review.dto.request.CreateReviewRequest;
import com.fixit.domain.review.dto.response.ReviewResponse;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse createReview(UUID bookingId, CreateReviewRequest request);
    ReviewResponse getBookingReview(UUID bookingId);
    List<ReviewResponse> getWorkerReviews(UUID workerId);
}
