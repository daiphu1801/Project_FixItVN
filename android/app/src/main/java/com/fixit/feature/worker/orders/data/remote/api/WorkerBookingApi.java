package com.fixit.feature.worker.orders.data.remote.api;

import com.fixit.core.common.ApiResponse;
import com.fixit.feature.worker.orders.data.remote.dto.request.WorkerQuotationRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WorkerBookingApi {
    @POST("api/v1/workers/me/bookings/{bookingId}/quotations")
    Call<ApiResponse<Void>> submitQuotation(
            @Path("bookingId") String bookingId,
            @Body WorkerQuotationRequestDto request
    );
}
