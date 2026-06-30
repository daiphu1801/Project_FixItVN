package com.fixit.feature.customer.review.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.review.domain.model.Review;
import java.util.List;

public interface ReviewRepository {
    void createReview(String bookingId, int rating, String comment, ResultCallback<Review> callback);
    void getBookingReview(String bookingId, ResultCallback<Review> callback);
    void getWorkerReviews(String workerId, ResultCallback<List<Review>> callback);
}
