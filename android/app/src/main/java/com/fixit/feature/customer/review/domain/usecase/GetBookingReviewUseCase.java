package com.fixit.feature.customer.review.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.review.domain.model.Review;
import com.fixit.feature.customer.review.domain.repository.ReviewRepository;

import javax.inject.Inject;

public class GetBookingReviewUseCase {
    private final ReviewRepository repository;

    @Inject
    public GetBookingReviewUseCase(ReviewRepository repository) {
        this.repository = repository;
    }

    public void execute(String bookingId, ResultCallback<Review> callback) {
        repository.getBookingReview(bookingId, callback);
    }
}
