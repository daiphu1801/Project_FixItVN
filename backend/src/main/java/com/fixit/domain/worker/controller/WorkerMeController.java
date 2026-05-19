package com.fixit.domain.worker.controller;

import com.fixit.domain.worker.dto.request.WorkerLocationUpdateRequest;
import com.fixit.domain.worker.dto.request.WorkerStatusUpdateRequest;
import com.fixit.domain.worker.dto.response.WorkerHomeResponse;
import com.fixit.domain.worker.service.WorkerAvailabilityService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workers/me")
@RequiredArgsConstructor
public class WorkerMeController {

    private final WorkerAvailabilityService workerAvailabilityService;

    @GetMapping("/home")
    public ApiResponse<WorkerHomeResponse> getHome() {
        WorkerHomeResponse response = workerAvailabilityService.getHome();
        return ApiResponse.success(response);
    }

    @PatchMapping("/status")
    public ApiResponse<WorkerHomeResponse> updateStatus(
            @Valid @RequestBody WorkerStatusUpdateRequest request
    ) {
        WorkerHomeResponse response = workerAvailabilityService.updateStatus(request);

        return ApiResponse.success(
                response,
                "Cập nhật trạng thái thành công"
        );
    }

    @PatchMapping("/location")
    public ApiResponse<WorkerHomeResponse> updateLocation(
            @Valid @RequestBody WorkerLocationUpdateRequest request
    ) {
        WorkerHomeResponse response = workerAvailabilityService.updateLocation(request);

        return ApiResponse.success(
                response,
                "Cập nhật vị trí thành công"
        );
    }
}