package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.WorkerQuotationCreateRequest;
import com.fixit.domain.booking.dto.response.WorkerQuotationResponse;

import java.util.UUID;

public interface QuotationPaymentService {

    /**
     * Thợ gửi báo giá cho khách hàng
     */
    WorkerQuotationResponse submitQuotation(UUID workerId, UUID bookingId, WorkerQuotationCreateRequest request);

    /**
     * Khách hàng chấp nhận báo giá
     */
    void acceptQuotation(UUID customerId, UUID bookingId, UUID quotationId);

    /**
     * Khách hàng nghiệm thu - xác nhận chất lượng công việc.
     * Booking chuyển sang Waiting_Payment (chờ thợ xác nhận nhận tiền).
     * @param paymentMethod phương thức khách chọn lúc nghiệm thu (CASH hoặc BANK_TRANSFER)
     */
    void customerConfirmAcceptance(UUID customerId, UUID bookingId,
                                   com.fixit.domain.booking.entity.BookingPaymentMethod paymentMethod);

    /**
     * Thợ xác nhận đã nhận đủ tiền mặt.
     * Booking chuyển sang Completed.
     */
    void workerConfirmPayment(UUID workerId, UUID bookingId);
}
