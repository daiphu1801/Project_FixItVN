package com.fixit.feature.customer.review.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.review.data.remote.api.ReviewApi;
import com.fixit.feature.customer.review.data.remote.dto.ReviewRequestDto;
import com.fixit.feature.customer.review.data.remote.dto.ReviewResponseDto;
import com.fixit.feature.customer.review.data.remote.mapper.ReviewMapper;
import com.fixit.feature.customer.review.domain.model.Review;
import com.fixit.feature.customer.review.domain.repository.ReviewRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ReviewRepositoryImpl implements ReviewRepository {
    private static final boolean USE_MOCK = true; // Bật true để test giao diện offline với mock data

    private final ReviewApi reviewApi;
    private final List<Review> mockReviews = new ArrayList<>();
    private final Map<String, Review> mockBookingReviews = new HashMap<>();

    @Inject
    public ReviewRepositoryImpl(ReviewApi reviewApi) {
        this.reviewApi = reviewApi;
        initMockData();
    }

    private void initMockData() {
        // Mock các đánh giá cũ hiển thị trên Hồ sơ Thợ sửa chữa
        mockReviews.add(new Review("REV001", "Nguyễn Văn An", "https://i.pravatar.cc/150?img=11", 5, "Thợ sửa ống nước nhiệt tình, chuyên nghiệp, khắc phục rò rỉ rất nhanh!", "2026-05-30T10:00:00Z"));
        mockReviews.add(new Review("REV002", "Trần Thị Bình", "https://i.pravatar.cc/150?img=20", 4, "Sửa khóa cửa nhanh chóng nhưng giá hơi cao chút, tuy nhiên chất lượng tốt.", "2026-05-29T14:30:00Z"));
        mockReviews.add(new Review("REV003", "Phan Văn Cường", "https://i.pravatar.cc/150?img=33", 5, "Rất hài lòng, thợ sạch sẽ và giải thích cặn kẽ trước khi làm.", "2026-05-28T09:15:00Z"));

        // Mock đơn hàng ORD004 đã được đánh giá trước đó để test giao diện
        mockBookingReviews.put("ORD004", new Review("REV004", "Khách hàng cũ", "", 5, "Tuyệt vời, sửa rất có tâm!", "2026-05-27T08:00:00Z"));
    }

    @Override
    public void createReview(String bookingId, int rating, String comment, ResultCallback<Review> callback) {
        if (USE_MOCK) {
            // Giả lập độ trễ mạng 1 giây để hiển thị progress bar xoay xoay trên giao diện
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Review newReview = new Review(
                        "REV_" + System.currentTimeMillis(),
                        "Khách Hàng Test",
                        "",
                        rating,
                        comment,
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date())
                );
                mockBookingReviews.put(bookingId, newReview);
                mockReviews.add(0, newReview); // Thêm lên đầu danh sách review
                callback.onResult(Result.success(newReview));
            }, 1000);
            return;
        }

        // Thực tế gọi API Backend
        ReviewRequestDto req = new ReviewRequestDto(rating, comment);
        reviewApi.createReview(bookingId, req).enqueue(new Callback<ApiResponse<ReviewResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReviewResponseDto>> call, Response<ApiResponse<ReviewResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(ReviewMapper.toDomain(response.body().getData())));
                } else {
                    callback.onResult(Result.error(new AppError("Lỗi tạo đánh giá từ server")));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReviewResponseDto>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    @Override
    public void getBookingReview(String bookingId, ResultCallback<Review> callback) {
        if (USE_MOCK) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Review review = mockBookingReviews.get(bookingId);
                callback.onResult(Result.success(review)); // Trả về review hoặc null nếu chưa đánh giá
            }, 500);
            return;
        }

        // Thực tế gọi API Backend
        reviewApi.getBookingReview(bookingId).enqueue(new Callback<ApiResponse<ReviewResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReviewResponseDto>> call, Response<ApiResponse<ReviewResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(ReviewMapper.toDomain(response.body().getData())));
                } else {
                    callback.onResult(Result.success(null)); // Lỗi hoặc chưa có review
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReviewResponseDto>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    @Override
    public void getWorkerReviews(String workerId, ResultCallback<List<Review>> callback) {
        if (USE_MOCK) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                callback.onResult(Result.success(new ArrayList<>(mockReviews)));
            }, 800);
            return;
        }

        // Thực tế gọi API Backend
        reviewApi.getWorkerReviews(workerId).enqueue(new Callback<ApiResponse<List<ReviewResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReviewResponseDto>>> call, Response<ApiResponse<List<ReviewResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(ReviewMapper.toDomainList(response.body().getData())));
                } else {
                    callback.onResult(Result.error(new AppError("Lỗi tải danh sách đánh giá từ server")));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReviewResponseDto>>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }
}
