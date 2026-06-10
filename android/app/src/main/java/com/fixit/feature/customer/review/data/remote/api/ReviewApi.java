package com.fixit.feature.customer.review.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.review.data.remote.dto.ReviewRequestDto;
import com.fixit.feature.customer.review.data.remote.dto.ReviewResponseDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewApi {
    @POST("api/v1/bookings/{bookingId}/reviews")
    Call<ApiResponse<ReviewResponseDto>> createReview(
            @Path("bookingId") String bookingId,
            @Body ReviewRequestDto request
    );

    @GET("api/v1/bookings/{bookingId}/reviews")
    Call<ApiResponse<ReviewResponseDto>> getBookingReview(
            @Path("bookingId") String bookingId
    );

    @GET("api/v1/workers/{workerId}/reviews")
    Call<ApiResponse<List<ReviewResponseDto>>> getWorkerReviews(
            @Path("workerId") String workerId
    );
}
