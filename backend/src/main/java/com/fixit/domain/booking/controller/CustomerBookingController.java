package com.fixit.domain.booking.controller;

import com.fixit.domain.booking.dto.request.CustomerBookingCreateRequest;
import com.fixit.domain.booking.dto.response.CustomerBookingResponse;
import com.fixit.domain.booking.service.CustomerBookingService;
import com.fixit.global.response.ApiResponse;
import com.fixit.domain.auth.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import com.fixit.domain.booking.service.QuotationPaymentService;

/**
 * Controller dành riêng cho thao tác đặt đơn của Khách hàng.
 */
@RestController
@RequestMapping("/api/v1/customers/me/bookings")
@RequiredArgsConstructor
public class CustomerBookingController {

    private final CustomerBookingService customerBookingService;
    private final QuotationPaymentService quotationPaymentService;

    /**
     * Khách hàng tạo một đơn đặt thợ mới.
     * Đơn sẽ được lưu trạng thái Pending. Sau đó Nhạc trưởng (WorkerMatchingScheduler)
     * sẽ tự động quét đơn này và dùng thuật toán tìm anh thợ tối ưu nhất.
     */
    @PostMapping
    @PreAuthorize("hasRole('Customer')")
    public ApiResponse<CustomerBookingResponse> createBooking(
            @AuthenticationPrincipal User userDetails,
            @Valid @RequestBody CustomerBookingCreateRequest request
    ) {
        CustomerBookingResponse response = customerBookingService.createBooking(userDetails.getId(), request);
        return ApiResponse.success(response, "Đặt đơn thành công, hệ thống đang tìm thợ cho bạn.");
    }

    /**
     * Khách hàng lấy chi tiết đơn hàng.
     * Sử dụng API này để Polling (hỏi liên tục) xem hệ thống đã ghép được thợ chưa.
     */
    @GetMapping("/{bookingId}")
    @PreAuthorize("hasRole('Customer')")
    public ApiResponse<CustomerBookingResponse> getBookingDetail(
            @AuthenticationPrincipal User userDetails,
            @PathVariable UUID bookingId
    ) {
        CustomerBookingResponse response = customerBookingService.getBookingDetail(userDetails.getId(), bookingId);
        return ApiResponse.success(response);
    }

    /**
     * Lấy danh sách lịch sử đơn hàng.
     */
    @GetMapping
    @PreAuthorize("hasRole('Customer')")
    public ApiResponse<java.util.List<CustomerBookingResponse>> getBookings(
            @AuthenticationPrincipal User userDetails
    ) {
        return ApiResponse.success(customerBookingService.getBookings(userDetails.getId()));
    }

    /**
     * Hủy đơn (hoặc yêu cầu đổi thợ).
     */
    @PostMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('Customer')")
    public ApiResponse<Void> cancelBooking(
            @AuthenticationPrincipal User userDetails,
            @PathVariable UUID bookingId,
            @Valid @RequestBody com.fixit.domain.booking.dto.request.CustomerBookingCancelRequest request
    ) {
        customerBookingService.cancelBooking(userDetails.getId(), bookingId, request);
        return ApiResponse.success(null, "Đã xử lý yêu cầu hủy");
    }

    /**
     * Khách hàng chấp nhận báo giá
     */
    @PostMapping("/{bookingId}/quotations/{quotationId}/accept")
    @PreAuthorize("hasRole('Customer')")
    public ApiResponse<Void> acceptQuotation(
            @AuthenticationPrincipal User userDetails,
            @PathVariable UUID bookingId,
            @PathVariable UUID quotationId
    ) {
        // Assume QuotationPaymentService is autowired or we can call it via a new injected service.
        // Wait, CustomerBookingController only has customerBookingService injected right now.
        // It's better to add these to CustomerBookingService or inject QuotationPaymentService.
        // Let's inject QuotationPaymentService! I will update the constructor shortly.
        quotationPaymentService.acceptQuotation(userDetails.getId(), bookingId, quotationId);
        return ApiResponse.success(null, "Đã chấp nhận báo giá");
    }

    /**
     * Khách hàng thanh toán tiền mặt
     */
    @PostMapping("/{bookingId}/payments")
    @PreAuthorize("hasRole('Customer')")
    public ApiResponse<Void> processPayment(
            @AuthenticationPrincipal User userDetails,
            @PathVariable UUID bookingId
    ) {
        quotationPaymentService.processPayment(userDetails.getId(), bookingId);
        return ApiResponse.success(null, "Thanh toán thành công");
    }
}
