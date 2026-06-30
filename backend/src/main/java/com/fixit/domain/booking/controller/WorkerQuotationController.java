package com.fixit.domain.booking.controller;

import com.fixit.domain.auth.entity.User;
import com.fixit.domain.booking.dto.request.WorkerQuotationCreateRequest;
import com.fixit.domain.booking.entity.WorkerQuotation;
import com.fixit.domain.booking.service.QuotationPaymentService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workers/me/bookings")
@RequiredArgsConstructor
public class WorkerQuotationController {

    private final QuotationPaymentService quotationPaymentService;

    @PostMapping("/{bookingId}/quotations")
    @PreAuthorize("hasRole('Worker')")
    public ApiResponse<com.fixit.domain.booking.dto.response.WorkerQuotationResponse> submitQuotation(
            @AuthenticationPrincipal User userDetails,
            @PathVariable UUID bookingId,
            @Valid @RequestBody WorkerQuotationCreateRequest request
    ) {
        com.fixit.domain.booking.dto.response.WorkerQuotationResponse quotation = quotationPaymentService.submitQuotation(userDetails.getId(), bookingId, request);
        return ApiResponse.success(quotation, "Đã gửi báo giá cho khách hàng");
    }
}
