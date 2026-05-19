package com.fixit.domain.booking.controller;

import com.fixit.domain.booking.dto.request.RejectAssignmentRequest;
import com.fixit.domain.booking.dto.response.AssignmentActionResponse;
import com.fixit.domain.booking.dto.response.PendingAssignmentResponse;
import com.fixit.domain.booking.service.WorkerAssignmentService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WorkerAssignmentController {

    private final WorkerAssignmentService workerAssignmentService;

    /**
     * Lấy danh sách assignment đang chờ thợ phản hồi.
     *
     * Dùng cho:
     * - dialog_incoming_order.xml
     * - WorkerHomeFragment kiểm tra có đơn mới không
     *
     * Test tạm khi chưa có Auth:
     * Header: X-Debug-Worker-Id: <workerId>
     */
    @GetMapping("/workers/me/assignments/pending")
    public ApiResponse<PendingAssignmentResponse> getPendingAssignments() {
        PendingAssignmentResponse response = workerAssignmentService.getPendingAssignments();
        return ApiResponse.success(response);
    }

    /**
     * Thợ chấp nhận đơn.
     *
     * Sau khi accept:
     * - assignment.status = Accepted
     * - booking.worker_id = currentWorkerId
     * - booking.status = Accepted
     */
    @PostMapping("/bookings/{bookingId}/assignments/{assignmentId}/accept")
    public ApiResponse<AssignmentActionResponse> accept(
            @PathVariable UUID bookingId,
            @PathVariable UUID assignmentId
    ) {
        AssignmentActionResponse response = workerAssignmentService.accept(bookingId, assignmentId);
        return ApiResponse.success(response, "Nhận đơn thành công");
    }

    /**
     * Thợ từ chối đơn.
     *
     * Request body có thể null trong giai đoạn MVP.
     * Nếu sau này muốn lưu lý do reject, cần thêm cột vào bảng booking_worker_assignments.
     */
    @PostMapping("/bookings/{bookingId}/assignments/{assignmentId}/reject")
    public ApiResponse<AssignmentActionResponse> reject(
            @PathVariable UUID bookingId,
            @PathVariable UUID assignmentId,
            @Valid @RequestBody(required = false) RejectAssignmentRequest request
    ) {
        RejectAssignmentRequest safeRequest = request != null
                ? request
                : new RejectAssignmentRequest();

        AssignmentActionResponse response = workerAssignmentService.reject(
                bookingId,
                assignmentId,
                safeRequest
        );

        return ApiResponse.success(response, "Từ chối đơn thành công");
    }

    /**
     * Ghi nhận thợ bỏ lỡ đơn.
     *
     * Giai đoạn MVP:
     * - Android có thể gọi API này khi countdown hết 3 phút.
     *
     * Sau này:
     * - Backend scheduler có thể tự xử lý miss.
     */
    @PostMapping("/bookings/{bookingId}/assignments/{assignmentId}/miss")
    public ApiResponse<AssignmentActionResponse> miss(
            @PathVariable UUID bookingId,
            @PathVariable UUID assignmentId
    ) {
        AssignmentActionResponse response = workerAssignmentService.miss(bookingId, assignmentId);
        return ApiResponse.success(response, "Ghi nhận bỏ lỡ đơn thành công");
    }
}