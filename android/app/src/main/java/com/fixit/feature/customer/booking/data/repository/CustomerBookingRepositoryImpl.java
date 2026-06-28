package com.fixit.feature.customer.booking.data.repository;

import androidx.annotation.NonNull;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.booking.data.remote.api.CustomerBookingApi;
import com.fixit.feature.customer.booking.data.remote.dto.request.CustomerBookingCancelRequestDto;
import com.fixit.feature.customer.booking.data.remote.dto.request.CustomerBookingCreateRequestDto;
import com.fixit.feature.customer.booking.data.remote.dto.response.CustomerBookingResponseDto;
import com.fixit.feature.customer.booking.data.remote.mapper.CustomerBookingMapper;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;
import com.fixit.feature.customer.booking.domain.repository.CustomerBookingRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerBookingRepositoryImpl implements CustomerBookingRepository {

    private final CustomerBookingApi api;

    public CustomerBookingRepositoryImpl(CustomerBookingApi api) {
        this.api = api;
    }

    @Override
    public void createBooking(Integer serviceId, String address, BigDecimal lat, BigDecimal lng, String issueDescription, String paymentMethod, ResultCallback<CustomerBooking> callback) {
        CustomerBookingCreateRequestDto request = new CustomerBookingCreateRequestDto(serviceId, address, lat, lng, issueDescription, paymentMethod);
        api.createBooking(request).enqueue(new Callback<ApiResponse<CustomerBookingResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<CustomerBookingResponseDto>> call, @NonNull Response<ApiResponse<CustomerBookingResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    CustomerBooking booking = CustomerBookingMapper.toDomain(response.body().getData());
                    callback.onResult(Result.success(booking));
                } else {
                    callback.onResult(Result.error(new AppError("Không thể tạo đơn đặt thợ")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<CustomerBookingResponseDto>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage())));
            }
        });
    }

    @Override
    public void getBookingDetail(String bookingId, ResultCallback<CustomerBooking> callback) {
        api.getBookingDetail(bookingId).enqueue(new Callback<ApiResponse<CustomerBookingResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<CustomerBookingResponseDto>> call, @NonNull Response<ApiResponse<CustomerBookingResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    CustomerBooking booking = CustomerBookingMapper.toDomain(response.body().getData());
                    callback.onResult(Result.success(booking));
                } else {
                    callback.onResult(Result.error(new AppError("Không thể lấy chi tiết đơn")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<CustomerBookingResponseDto>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage())));
            }
        });
    }

    @Override
    public void getBookings(ResultCallback<List<CustomerBooking>> callback) {
        api.getBookings().enqueue(new Callback<ApiResponse<List<CustomerBookingResponseDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<CustomerBookingResponseDto>>> call, @NonNull Response<ApiResponse<List<CustomerBookingResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<CustomerBooking> list = new ArrayList<>();
                    if (response.body().getData() != null) {
                        for (CustomerBookingResponseDto dto : response.body().getData()) {
                            list.add(CustomerBookingMapper.toDomain(dto));
                        }
                    }
                    callback.onResult(Result.success(list));
                } else {
                    callback.onResult(Result.error(new AppError("Không thể tải danh sách đơn")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<CustomerBookingResponseDto>>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage())));
            }
        });
    }

    @Override
    public void cancelBooking(String bookingId, String reason, boolean isWorkerFault, ResultCallback<Void> callback) {
        CustomerBookingCancelRequestDto request = new CustomerBookingCancelRequestDto(reason, isWorkerFault);
            
        api.cancelBooking(bookingId, request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error(new AppError("Không thể hủy đơn")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage())));
            }
        });
    }

    @Override
    public void acceptQuotation(String bookingId, String quotationId, ResultCallback<Void> callback) {
        api.acceptQuotation(bookingId, quotationId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error(new AppError("Không thể chấp nhận báo giá")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage())));
            }
        });
    }

    @Override
    public void processPayment(String bookingId, String paymentMethod, ResultCallback<Void> callback) {
        api.processPayment(bookingId, paymentMethod).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error(new AppError("Thanh toán thất bại")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage())));
            }
        });
    }

    @Override
    public void simulateBankTransfer(String bookingId, ResultCallback<Void> callback) {
        api.simulateBankTransfer(bookingId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error(new AppError("Giả lập chuyển khoản thất bại")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage())));
            }
        });
    }
}
