package com.fixit.domain.upload.controller;

import com.fixit.domain.upload.dto.request.PresignedUrlRequest;
import com.fixit.domain.upload.dto.request.UploadConfirmRequest;
import com.fixit.domain.upload.dto.response.PresignedUrlResponse;
import com.fixit.domain.upload.dto.response.UploadedFileResponse;
import com.fixit.domain.upload.service.UploadService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/presigned-url")
    public ApiResponse<PresignedUrlResponse> createPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        PresignedUrlResponse response = uploadService.createPresignedUrl(request);
        return ApiResponse.success(response, "Tạo quyền upload thành công");
    }

    @PostMapping("/confirm")
    public ApiResponse<UploadedFileResponse> confirmUpload(
            @Valid @RequestBody UploadConfirmRequest request
    ) {
        UploadedFileResponse response = uploadService.confirmUpload(request);
        return ApiResponse.success(response, "Xác nhận upload thành công");
    }
}