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
import com.fixit.domain.wallet.entity.TransactionHistory;
import com.fixit.domain.wallet.entity.TransactionType;
import com.fixit.domain.wallet.entity.TransactionStatus;
import com.fixit.domain.wallet.dto.request.SepayWebhookRequest;
import com.fixit.domain.wallet.service.WorkerWalletService;
import com.fixit.domain.wallet.repository.TransactionHistoryRepository;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;

/**
 * Controller dành riêng cho thao tác đặt đơn của Khách hàng.
 */
@RestController
@RequestMapping("/api/v1/customers/me/bookings")
@RequiredArgsConstructor
public class CustomerBookingController {

    private final CustomerBookingService customerBookingService;
    private final QuotationPaymentService quotationPaymentService;
    private final WorkerWalletService workerWalletService;
    private final TransactionHistoryRepository transactionHistoryRepository;

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
     * Khách hàng xác nhận nghiệm thu chất lượng công việc
     * → Booking chuyển sang Waiting_Payment (chờ thợ xác nhận đã nhận tiền)
     */
    @PostMapping("/{bookingId}/payments")
    @PreAuthorize("hasRole('Customer')")
    public ApiResponse<Void> confirmAcceptance(
            @AuthenticationPrincipal User userDetails,
            @PathVariable UUID bookingId,
            @RequestParam(required = false) com.fixit.domain.booking.entity.BookingPaymentMethod paymentMethod
    ) {
        quotationPaymentService.customerConfirmAcceptance(userDetails.getId(), bookingId, paymentMethod);
        return ApiResponse.success(null, "Nghiệm thu thành công, chờ thợ xác nhận nhận tiền");
    }

    /**
     * Giả lập thanh toán chuyển khoản ngân hàng (Option B) qua SePay webhook
     */
    @PostMapping("/{bookingId}/payments/simulate-bank-transfer")
    @PreAuthorize("hasRole('Customer')")
    public ApiResponse<Void> simulateBankTransfer(
            @AuthenticationPrincipal User userDetails,
            @PathVariable UUID bookingId
    ) {
        TransactionHistory transaction = transactionHistoryRepository.findByBooking_IdAndTransactionTypeAndStatus(
                bookingId, TransactionType.Release, TransactionStatus.Pending
        ).orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND));

        SepayWebhookRequest request = new SepayWebhookRequest();
        request.setTransactionType("in");
        request.setContent("Chuyen khoan " + transaction.getTransactionCode());
        request.setTransferAmount(transaction.getAmount());
        request.setReferenceCode("MOCK_REF_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        request.setGateway("MOCK_BANK");
        request.setTransactionDate(java.time.LocalDateTime.now().toString());

        workerWalletService.processDepositWebhook(request);

        return ApiResponse.success(null, "Giả lập chuyển khoản thành công");
    }
}
