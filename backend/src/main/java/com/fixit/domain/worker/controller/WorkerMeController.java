package com.fixit.domain.worker.controller;

import com.fixit.domain.worker.dto.request.WorkerLocationUpdateRequest;
import com.fixit.domain.worker.dto.request.WorkerStatusUpdateRequest;
import com.fixit.domain.worker.dto.response.WorkerHomeResponse;
import com.fixit.domain.worker.dto.response.WorkerScheduleResponse;
import com.fixit.domain.worker.service.WorkerHomeService;
import com.fixit.domain.worker.service.WorkerScheduleService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/workers/me")
@RequiredArgsConstructor
public class WorkerMeController {

    private final WorkerHomeService workerHomeService;
    private final WorkerScheduleService workerScheduleService;

    @GetMapping("/home")
    public ApiResponse<WorkerHomeResponse> getHome() {
        return ApiResponse.success(workerHomeService.getHome());
    }

    @PatchMapping("/status")
    public ApiResponse<WorkerHomeResponse> updateStatus(
            @Valid @RequestBody WorkerStatusUpdateRequest request
    ) {
        WorkerHomeResponse response = workerHomeService.updateStatus(request);
        return ApiResponse.success(response, "Cập nhật trạng thái thành công");
    }

    @PatchMapping("/location")
    public ApiResponse<Void> updateLocation(
            @Valid @RequestBody WorkerLocationUpdateRequest request
    ) {
        // Chỉ cập nhật vị trí vào Redis, không lấy lại thông tin màn hình Home
        workerHomeService.updateLocation(request);

        // Trả về kết quả thành công gọn nhẹ
        return ApiResponse.success(null, "Cập nhật vị trí thành công");
    }

    @GetMapping("/schedule")
    public ApiResponse<WorkerScheduleResponse> getSchedule(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        WorkerScheduleResponse response = workerScheduleService.getMySchedule(date);
        return ApiResponse.success(response);
    }
}