package com.fixit.domain.booking.controller;

import com.fixit.domain.auth.entity.User;
import com.fixit.domain.booking.dto.request.ComplaintRequest;
import com.fixit.domain.booking.dto.request.WorkerComplaintResponseRequest;
import com.fixit.domain.booking.dto.response.ComplaintResponse;
import com.fixit.domain.booking.service.ComplaintWarrantyService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class ComplaintWarrantyController {

    private final ComplaintWarrantyService complaintWarrantyService;

    /**
     * Khách hàng tạo khiếu nại cho đơn hàng đã hoàn thành.
     */
    @PostMapping("/{bookingId}/complaints")
    @PreAuthorize("hasRole('Customer')")
    public ApiResponse<ComplaintResponse> createComplaint(
            @AuthenticationPrincipal User userDetails,
            @PathVariable UUID bookingId,
            @Valid @RequestBody ComplaintRequest request
    ) {
        ComplaintResponse response = complaintWarrantyService.createComplaint(userDetails.getId(), bookingId, request);
        return ApiResponse.success(response, "Đã gửi khiếu nại thành công.");
    }

    /**
     * Lấy chi tiết khiếu nại của đơn hàng.
     * Cho phép cả Khách hàng, Thợ sửa và Admin xem chi tiết.
     */
    @GetMapping("/{bookingId}/complaints")
    @PreAuthorize("hasAnyRole('Customer', 'Worker', 'Admin')")
    public ApiResponse<ComplaintResponse> getComplaint(
            @PathVariable UUID bookingId
    ) {
        ComplaintResponse response = complaintWarrantyService.getComplaint(bookingId);
        return ApiResponse.success(response);
    }

    /**
     * Khách hàng hủy khiếu nại (khi thợ chưa phản hồi).
     */
    @PostMapping("/{bookingId}/complaints/{complaintId}/cancel")
    @PreAuthorize("hasRole('Customer')")
    public ApiResponse<Void> cancelComplaint(
            @AuthenticationPrincipal User userDetails,
            @PathVariable UUID bookingId,
            @PathVariable UUID complaintId
    ) {
        complaintWarrantyService.cancelComplaint(userDetails.getId(), bookingId, complaintId);
        return ApiResponse.success(null, "Đã hủy khiếu nại thành công.");
    }

    /**
     * Thợ sửa gửi giải trình khiếu nại.
     */
    @PostMapping("/{bookingId}/complaints/respond")
    @PreAuthorize("hasRole('Worker')")
    public ApiResponse<ComplaintResponse> respondToComplaint(
            @AuthenticationPrincipal User userDetails,
            @PathVariable UUID bookingId,
            @Valid @RequestBody WorkerComplaintResponseRequest request
    ) {
        ComplaintResponse response = complaintWarrantyService.respondToComplaint(userDetails.getId(), bookingId, request);
        return ApiResponse.success(response, "Gửi giải trình khiếu nại thành công.");
    }
}
