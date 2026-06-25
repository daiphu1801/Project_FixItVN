package com.fixit.domain.review.controller;

import com.fixit.domain.review.dto.request.CreateReviewRequest;
import com.fixit.domain.review.dto.response.ReviewResponse;
import com.fixit.domain.review.service.ReviewService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 1. Tạo Đánh giá cho Đơn hàng (Booking)
    @PostMapping("/api/v1/bookings/{bookingId}/reviews")
    public ApiResponse<ReviewResponse> createReview(
            @PathVariable UUID bookingId,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse response = reviewService.createReview(bookingId, request);
        return ApiResponse.success(response, "Đánh giá đơn hàng thành công");
    }

    // 2. Lấy thông tin Đánh giá của một Đơn hàng (Booking)
    @GetMapping("/api/v1/bookings/{bookingId}/reviews")
    public ApiResponse<ReviewResponse> getBookingReview(@PathVariable UUID bookingId) {
        ReviewResponse response = reviewService.getBookingReview(bookingId);
        return ApiResponse.success(response);
    }

    // 3. Lấy danh sách Đánh giá của một Thợ (Worker)
    @GetMapping("/api/v1/workers/{workerId}/reviews")
    public ApiResponse<List<ReviewResponse>> getWorkerReviews(@PathVariable UUID workerId) {
        List<ReviewResponse> response = reviewService.getWorkerReviews(workerId);
        return ApiResponse.success(response, "Lấy danh sách đánh giá của thợ thành công");
    }
}
