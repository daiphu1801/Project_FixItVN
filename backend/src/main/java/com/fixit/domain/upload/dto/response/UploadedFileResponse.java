package com.fixit.domain.upload.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class UploadedFileResponse {
    private UUID uploadId;
    private String purpose;
    private String storageProvider;
    private String objectKey;
    private String fileUrl;
    private String contentType;
    private Long fileSize;
    private String status;
    private OffsetDateTime expiresAt;
    private OffsetDateTime confirmedAt;
}