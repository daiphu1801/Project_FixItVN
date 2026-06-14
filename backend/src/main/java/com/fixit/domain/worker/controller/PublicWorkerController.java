package com.fixit.domain.worker.controller;

import com.fixit.domain.worker.dto.response.PublicWorkerProfileResponse;
import com.fixit.domain.worker.dto.response.PublicWorkerSkillResponse;
import com.fixit.domain.worker.service.WorkerProfileService;
import com.fixit.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workers")
@RequiredArgsConstructor
public class PublicWorkerController {

    private final WorkerProfileService workerProfileService;

    @GetMapping("/{workerId}/profile")
    public ApiResponse<PublicWorkerProfileResponse> getPublicProfile(@PathVariable String workerId) {
        return ApiResponse.success(workerProfileService.getPublicProfile(workerId));
    }

    @GetMapping("/{workerId}/skills")
    public ApiResponse<List<PublicWorkerSkillResponse>> getPublicSkills(@PathVariable String workerId) {
        return ApiResponse.success(workerProfileService.getPublicSkills(workerId));
    }

    @GetMapping("/{workerId}/reviews")
    public ApiResponse<List<Object>> getPublicReviews(@PathVariable String workerId) {
        // Mock reviews for now since Review entity is not fully implemented
        return ApiResponse.success(Collections.emptyList());
    }
}
