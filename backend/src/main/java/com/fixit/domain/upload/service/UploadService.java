package com.fixit.domain.upload.service;

import com.fixit.domain.upload.dto.request.PresignedUrlRequest;
import com.fixit.domain.upload.dto.request.UploadConfirmRequest;
import com.fixit.domain.upload.dto.response.PresignedUrlResponse;
import com.fixit.domain.upload.dto.response.UploadedFileResponse;

public interface UploadService {

    PresignedUrlResponse createPresignedUrl(PresignedUrlRequest request);

    UploadedFileResponse confirmUpload(UploadConfirmRequest request);
}