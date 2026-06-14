package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.CustomerBookingCreateRequest;
import com.fixit.domain.booking.dto.response.CustomerBookingResponse;

import java.util.UUID;

public interface CustomerBookingService {

    /**
     * Khách hàng tạo đơn đặt thợ.
     * Hệ thống sẽ lưu đơn với trạng thái Pending.
     */
    CustomerBookingResponse createBooking(UUID customerId, CustomerBookingCreateRequest request);

    /**
     * Khách hàng lấy chi tiết đơn hàng (Dùng để polling xem đã ghép được thợ chưa).
     */
    CustomerBookingResponse getBookingDetail(UUID customerId, UUID bookingId);

    /**
     * Khách hàng lấy danh sách lịch sử đơn hàng.
     */
    java.util.List<CustomerBookingResponse> getBookings(UUID customerId);

    /**
     * Khách hàng hủy đơn hàng (hoặc yêu cầu đổi thợ).
     */
    void cancelBooking(UUID customerId, UUID bookingId, com.fixit.domain.booking.dto.request.CustomerBookingCancelRequest request);
}
