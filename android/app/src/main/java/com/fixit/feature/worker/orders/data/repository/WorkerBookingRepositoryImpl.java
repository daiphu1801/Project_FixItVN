package com.fixit.feature.worker.orders.data.repository;

import androidx.annotation.NonNull;
import com.fixit.core.common.ApiResponse;
import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.orders.data.remote.api.WorkerBookingApi;
import com.fixit.feature.worker.orders.data.remote.dto.request.WorkerQuotationRequestDto;
import com.fixit.feature.worker.orders.domain.repository.WorkerBookingRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.math.BigDecimal;
import javax.inject.Inject;

public class WorkerBookingRepositoryImpl implements WorkerBookingRepository {

    private final WorkerBookingApi api;

    @Inject
    public WorkerBookingRepositoryImpl(WorkerBookingApi api) {
        this.api = api;
    }

    @Override
    public void submitQuotation(String bookingId, BigDecimal laborCost, BigDecimal materialCost, ResultCallback<Void> callback) {
        WorkerQuotationRequestDto request = new WorkerQuotationRequestDto(laborCost, materialCost);
        api.submitQuotation(bookingId, request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error(new AppError("Không thể gửi báo giá")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage())));
            }
        });
    }
}
