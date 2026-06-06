package com.fixit.domain.upload.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class PresignedUrlResponse {
    private UUID uploadId;
    private String storageProvider;
    private String uploadUrl;
    private String method;
    private String objectKey;
    private String fileUrl;
    private OffsetDateTime expiresAt;
    private Integer expiresInSeconds;
    private Map<String, String> formData;
}