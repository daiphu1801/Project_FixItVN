package com.fixit.domain.review.service;

import com.fixit.domain.auth.entity.User;
import com.fixit.domain.auth.entity.UserRole;
import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingStatus;
import com.fixit.domain.booking.repository.BookingRepository;
import com.fixit.domain.customer.entity.Customer;
import com.fixit.domain.customer.repository.CustomerRepository;
import com.fixit.domain.review.dto.request.CreateReviewRequest;
import com.fixit.domain.review.dto.response.ReviewResponse;
import com.fixit.domain.review.entity.Review;
import com.fixit.domain.review.repository.ReviewRepository;
import com.fixit.domain.worker.entity.Worker;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final WorkerRepository workerRepository;

    private UUID getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (user.getRole() != UserRole.Customer) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        return user.getId();
    }

    private ReviewResponse mapToResponse(Review review) {
        if (review == null) {
            return null;
        }

        Customer customer = review.getCustomer();
        User user = customer != null ? customer.getUser() : null;

        return ReviewResponse.builder()
                .id(review.getId())
                .bookingId(review.getBooking() != null ? review.getBooking().getId() : null)
                .customerId(customer != null ? customer.getCustomerId() : null)
                .customerName(customer != null ? customer.getFullName() : null)
                .customerAvatar(user != null ? user.getAvatarUrl() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public ReviewResponse createReview(UUID bookingId, CreateReviewRequest request) {
        UUID customerId = getCurrentCustomerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        // 1. Kiểm tra xem booking có phải của khách hàng này không
        if (!booking.getCustomer().getCustomerId().equals(customerId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        // 2. Kiểm tra xem trạng thái booking có phải Completed không
        if (booking.getStatus() != BookingStatus.Completed) {
            throw new AppException(ErrorCode.BOOKING_NOT_COMPLETED);
        }

        // 3. Kiểm tra xem booking đã được đánh giá chưa
        if (reviewRepository.existsByBookingId(bookingId)) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // 4. Tạo review mới
        Review review = Review.builder()
                .booking(booking)
                .customer(customer)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(OffsetDateTime.now())
                .build();

        review = reviewRepository.save(review);

        // 5. Cập nhật reputationScore của thợ
        Worker worker = booking.getWorker();
        if (worker != null) {
            Double avgRating = reviewRepository.getAverageRatingByWorkerId(worker.getWorkerId());
            if (avgRating != null) {
                BigDecimal score = BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP);
                worker.setReputationScore(score);
                workerRepository.save(worker);
            }
        }

        return mapToResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getBookingReview(UUID bookingId) {
        Review review = reviewRepository.findByBookingId(bookingId).orElse(null);
        return mapToResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getWorkerReviews(UUID workerId) {
        List<Review> reviews = reviewRepository.findByWorkerId(workerId);
        return reviews.stream()
                .map(this::mapToResponse)
                .toList();
    }
}
