package com.fixit.feature.customer.booking.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.booking.data.remote.dto.request.CustomerBookingCreateRequestDto;
import com.fixit.feature.customer.booking.data.remote.dto.response.CustomerBookingResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CustomerBookingApi {

    @POST("api/v1/customers/me/bookings")
    Call<ApiResponse<CustomerBookingResponseDto>> createBooking(@Body CustomerBookingCreateRequestDto request);

    @GET("api/v1/customers/me/bookings/{bookingId}")
    Call<ApiResponse<CustomerBookingResponseDto>> getBookingDetail(@Path("bookingId") String bookingId);

    @GET("api/v1/customers/me/bookings")
    Call<ApiResponse<List<CustomerBookingResponseDto>>> getBookings();

    @POST("api/v1/customers/me/bookings/{bookingId}/cancel")
    Call<ApiResponse<Void>> cancelBooking(
            @Path("bookingId") String bookingId, 
            @Body com.fixit.feature.customer.booking.data.remote.dto.request.CustomerBookingCancelRequestDto request
    );

    @POST("api/v1/customers/me/bookings/{bookingId}/quotations/{quotationId}/accept")
    Call<ApiResponse<Void>> acceptQuotation(
            @Path("bookingId") String bookingId,
            @Path("quotationId") String quotationId
    );

    @POST("api/v1/customers/me/bookings/{bookingId}/payments")
    Call<ApiResponse<Void>> processPayment(
            @Path("bookingId") String bookingId
    );
}
