package com.fixit.feature.customer.complaint.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.complaint.data.remote.dto.ComplaintRequestDto;
import com.fixit.feature.customer.complaint.data.remote.dto.ComplaintResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ComplaintApi {
    @POST("api/v1/bookings/{bookingId}/complaints")
    Call<ApiResponse<ComplaintResponseDto>> createComplaint(
            @Path("bookingId") String bookingId,
            @Body ComplaintRequestDto request
    );

    @GET("api/v1/bookings/{bookingId}/complaints")
    Call<ApiResponse<ComplaintResponseDto>> getBookingComplaint(
            @Path("bookingId") String bookingId
    );

    @POST("api/v1/bookings/{bookingId}/complaints/{complaintId}/cancel")
    Call<ApiResponse<Void>> cancelComplaint(
            @Path("bookingId") String bookingId,
            @Path("complaintId") String complaintId
    );

    @POST("api/v1/bookings/{bookingId}/complaints/respond")
    Call<ApiResponse<com.fixit.feature.customer.complaint.data.remote.dto.ComplaintResponseDto>> respondToComplaint(
            @Path("bookingId") String bookingId,
            @Body com.fixit.feature.customer.complaint.data.remote.dto.WorkerComplaintResponseRequestDto request
    );
}
