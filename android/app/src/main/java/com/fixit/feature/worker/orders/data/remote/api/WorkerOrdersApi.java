package com.fixit.feature.worker.orders.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.orders.data.remote.dto.WorkerBookingDetailResponseDto;
import com.fixit.feature.worker.orders.data.remote.dto.BookingActionResponseDto;
import com.fixit.feature.worker.orders.data.remote.dto.WorkerScheduleResponseDto;
import com.fixit.feature.worker.orders.data.remote.dto.WorkerHistoryResponseDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WorkerOrdersApi {

    @GET("api/v1/bookings/{bookingId}")
    Call<ApiResponse<WorkerBookingDetailResponseDto>> getBookingDetails(
            @Path("bookingId") String bookingId
    );

    @POST("api/v1/bookings/{bookingId}/start-moving")
    Call<ApiResponse<BookingActionResponseDto>> startMoving(
            @Path("bookingId") String bookingId
    );

    @POST("api/v1/bookings/{bookingId}/arrive")
    Call<ApiResponse<BookingActionResponseDto>> arrive(
            @Path("bookingId") String bookingId
    );

    @POST("api/v1/bookings/{bookingId}/start-survey")
    Call<ApiResponse<BookingActionResponseDto>> startSurvey(
            @Path("bookingId") String bookingId
    );

    @POST("api/v1/bookings/{bookingId}/start-repair")
    Call<ApiResponse<BookingActionResponseDto>> startRepair(
            @Path("bookingId") String bookingId
    );

    @POST("api/v1/bookings/{bookingId}/worker-complete")
    Call<ApiResponse<BookingActionResponseDto>> workerComplete(
            @Path("bookingId") String bookingId
    );

    @GET("api/v1/workers/me/schedule")
    Call<ApiResponse<WorkerScheduleResponseDto>> getSchedule();

    @GET("api/v1/workers/me/history")
    Call<ApiResponse<WorkerHistoryResponseDto>> getHistory(
            @Query("status") String status
    );
}
