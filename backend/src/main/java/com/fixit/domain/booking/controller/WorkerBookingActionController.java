package com.fixit.domain.booking.controller;

import com.fixit.domain.booking.dto.response.BookingActionResponse;
import com.fixit.domain.booking.dto.response.WorkerBookingDetailResponse;
import com.fixit.domain.booking.service.WorkerBookingActionService;
import com.fixit.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class WorkerBookingActionController {

    private final WorkerBookingActionService workerBookingActionService;

    @GetMapping("/{bookingId}")
    public ApiResponse<WorkerBookingDetailResponse> getBookingDetails(
            @PathVariable UUID bookingId
    ) {
        WorkerBookingDetailResponse response = workerBookingActionService.getBookingDetails(bookingId);
        return ApiResponse.success(response);
    }

    @PostMapping("/{bookingId}/start-moving")
    public ApiResponse<BookingActionResponse> startMoving(
            @PathVariable UUID bookingId
    ) {
        BookingActionResponse response = workerBookingActionService.startMoving(bookingId);
        return ApiResponse.success(response, "Bắt đầu di chuyển thành công");
    }

    @PostMapping("/{bookingId}/arrive")
    public ApiResponse<BookingActionResponse> arrive(
            @PathVariable UUID bookingId
    ) {
        BookingActionResponse response = workerBookingActionService.arrive(bookingId);
        return ApiResponse.success(response, "Đã đến nơi");
    }

    @PostMapping("/{bookingId}/start-survey")
    public ApiResponse<BookingActionResponse> startSurvey(
            @PathVariable UUID bookingId
    ) {
        BookingActionResponse response = workerBookingActionService.startSurvey(bookingId);
        return ApiResponse.success(response, "Bắt đầu khảo sát thành công");
    }

    @PostMapping("/{bookingId}/start-repair")
    public ApiResponse<BookingActionResponse> startRepair(
            @PathVariable UUID bookingId
    ) {
        BookingActionResponse response = workerBookingActionService.startRepair(bookingId);
        return ApiResponse.success(response, "Bắt đầu sửa chữa thành công");
    }

    @PostMapping("/{bookingId}/worker-complete")
    public ApiResponse<BookingActionResponse> workerComplete(
            @PathVariable UUID bookingId
    ) {
        BookingActionResponse response = workerBookingActionService.workerComplete(bookingId);
        return ApiResponse.success(response, "Thợ đã báo hoàn thành");
    }
}