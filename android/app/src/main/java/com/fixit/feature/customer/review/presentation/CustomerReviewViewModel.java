package com.fixit.feature.customer.review.presentation;

import androidx.lifecycle.ViewModel;
import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.review.domain.model.Review;
import com.fixit.feature.customer.review.domain.usecase.CreateReviewUseCase;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CustomerReviewViewModel extends ViewModel {
    private final CreateReviewUseCase createReviewUseCase;

    @Inject
    public CustomerReviewViewModel(CreateReviewUseCase createReviewUseCase) {
        this.createReviewUseCase = createReviewUseCase;
    }

    public void submitReview(String bookingId, int rating, String comment, ResultCallback<Review> callback) {
        createReviewUseCase.execute(bookingId, rating, comment, callback);
    }
}
