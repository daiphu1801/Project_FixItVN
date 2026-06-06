package com.fixit.domain.worker.controller;

import com.fixit.domain.worker.dto.request.WorkerKycSubmitRequest;
import com.fixit.domain.worker.dto.response.WorkerKycResponse;
import com.fixit.domain.worker.service.WorkerKycService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workers/me/kyc")
@RequiredArgsConstructor
public class WorkerKycController {

    private final WorkerKycService workerKycService;

    @PostMapping
    public ApiResponse<WorkerKycResponse> submitKyc(
            @Valid @RequestBody WorkerKycSubmitRequest request
    ) {
        WorkerKycResponse response = workerKycService.submitKyc(request);
        return ApiResponse.success(response, "Nộp hồ sơ KYC thành công");
    }

    @GetMapping("/status")
    public ApiResponse<WorkerKycResponse> getMyKycStatus() {
        return ApiResponse.success(workerKycService.getMyKycStatus());
    }
}