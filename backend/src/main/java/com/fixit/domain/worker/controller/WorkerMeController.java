package com.fixit.domain.worker.controller;


import com.fixit.domain.worker.dto.request.WorkerLocationUpdateRequest;
import com.fixit.domain.worker.dto.request.WorkerProfileUpdateRequest;
import com.fixit.domain.worker.dto.request.WorkerSkillsUpdateRequest;
import com.fixit.domain.worker.dto.request.WorkerStatusUpdateRequest;
import com.fixit.domain.worker.dto.response.WorkerHomeResponse;
import com.fixit.domain.worker.dto.response.WorkerProfileResponse;
import com.fixit.domain.worker.dto.response.WorkerScheduleResponse;
import com.fixit.domain.worker.dto.response.WorkerSkillsResponse;
import com.fixit.domain.worker.service.WorkerHomeService;
import com.fixit.domain.worker.service.WorkerProfileService;
import com.fixit.domain.worker.service.WorkerScheduleService;
import com.fixit.domain.worker.dto.response.WorkerHistoryResponse;
import com.fixit.domain.worker.dto.response.WorkerStatsResponse;
import com.fixit.domain.worker.service.WorkerHistoryService;
import com.fixit.domain.worker.service.WorkerStatsService;

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
    private final WorkerProfileService workerProfileService;
    private final WorkerHistoryService workerHistoryService;
    private final WorkerStatsService workerStatsService;

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

    @GetMapping("/profile")
    public ApiResponse<WorkerProfileResponse> getProfile() {
        return ApiResponse.success(workerProfileService.getMyProfile());
    }

    @PatchMapping("/profile")
    public ApiResponse<WorkerProfileResponse> updateProfile(
            @Valid @RequestBody WorkerProfileUpdateRequest request
    ) {
        WorkerProfileResponse response = workerProfileService.updateMyProfile(request);
        return ApiResponse.success(response, "Cập nhật hồ sơ thợ thành công");
    }

    @GetMapping("/skills")
    public ApiResponse<WorkerSkillsResponse> getSkills() {
        return ApiResponse.success(workerProfileService.getMySkills());
    }

    @PutMapping("/skills")
    public ApiResponse<WorkerSkillsResponse> updateSkills(
            @Valid @RequestBody WorkerSkillsUpdateRequest request
    ) {
        WorkerSkillsResponse response = workerProfileService.updateMySkills(request);
        return ApiResponse.success(response, "Cập nhật kỹ năng thợ thành công");
    }

    @GetMapping("/history")
    public ApiResponse<WorkerHistoryResponse> getHistory(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        WorkerHistoryResponse response = workerHistoryService.getMyHistory(status, page, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/stats")
    public ApiResponse<WorkerStatsResponse> getStats() {
        WorkerStatsResponse response = workerStatsService.getMyStats();
        return ApiResponse.success(response);
    }

}