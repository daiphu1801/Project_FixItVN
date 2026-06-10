package com.fixit.feature.customer.review.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.review.domain.model.Review;
import com.fixit.feature.customer.review.domain.repository.ReviewRepository;

import javax.inject.Inject;

public class CreateReviewUseCase {
    private final ReviewRepository repository;

    @Inject
    public CreateReviewUseCase(ReviewRepository repository) {
        this.repository = repository;
    }

    public void execute(String bookingId, int rating, String comment, ResultCallback<Review> callback) {
        repository.createReview(bookingId, rating, comment, callback);
    }
}
