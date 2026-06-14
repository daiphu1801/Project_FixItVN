package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.WorkerQuotationCreateRequest;
import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.WorkerQuotation;

import java.util.UUID;

public interface QuotationPaymentService {

    /**
     * Thợ gửi báo giá cho khách hàng
     */
    WorkerQuotation submitQuotation(UUID workerId, UUID bookingId, WorkerQuotationCreateRequest request);

    /**
     * Khách hàng chấp nhận báo giá
     */
    void acceptQuotation(UUID customerId, UUID bookingId, UUID quotationId);

    /**
     * Khách hàng thanh toán
     */
    void processPayment(UUID customerId, UUID bookingId);
}
