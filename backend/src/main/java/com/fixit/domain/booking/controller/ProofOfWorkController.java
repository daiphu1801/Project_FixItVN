package com.fixit.domain.booking.controller;

import com.fixit.domain.booking.dto.request.ProofOfWorkCreateRequest;
import com.fixit.domain.booking.dto.response.ProofOfWorkResponse;
import com.fixit.domain.booking.service.ProofOfWorkService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings/{bookingId}/proof-of-work")
@RequiredArgsConstructor
public class ProofOfWorkController {

    private final ProofOfWorkService proofOfWorkService;

    @PostMapping
    public ApiResponse<ProofOfWorkResponse> createProofOfWork(
            @PathVariable UUID bookingId,
            @Valid @RequestBody ProofOfWorkCreateRequest request
    ) {
        ProofOfWorkResponse response = proofOfWorkService.createProofOfWork(bookingId, request);
        return ApiResponse.success(response, "Lưu ảnh bằng chứng thành công");
    }

    @GetMapping
    public ApiResponse<List<ProofOfWorkResponse>> getProofOfWorks(
            @PathVariable UUID bookingId
    ) {
        return ApiResponse.success(proofOfWorkService.getProofOfWorks(bookingId));
    }

    @DeleteMapping("/{proofId}")
    public ApiResponse<Void> deleteProofOfWork(
            @PathVariable UUID bookingId,
            @PathVariable UUID proofId
    ) {
        proofOfWorkService.deleteProofOfWork(bookingId, proofId);
        return ApiResponse.success(null, "Xóa ảnh bằng chứng thành công");
    }
}