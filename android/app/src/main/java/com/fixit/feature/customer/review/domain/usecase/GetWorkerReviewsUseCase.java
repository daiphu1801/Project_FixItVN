package com.fixit.feature.customer.review.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.review.domain.model.Review;
import com.fixit.feature.customer.review.domain.repository.ReviewRepository;

import java.util.List;
import javax.inject.Inject;

public class GetWorkerReviewsUseCase {
    private final ReviewRepository repository;

    @Inject
    public GetWorkerReviewsUseCase(ReviewRepository repository) {
        this.repository = repository;
    }

    public void execute(String workerId, ResultCallback<List<Review>> callback) {
        repository.getWorkerReviews(workerId, callback);
    }
}
