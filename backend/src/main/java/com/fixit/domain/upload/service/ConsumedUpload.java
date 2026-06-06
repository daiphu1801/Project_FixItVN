package com.fixit.domain.upload.service;

import com.fixit.domain.upload.entity.UploadPurpose;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ConsumedUpload {
    private UUID uploadId;
    private UploadPurpose purpose;
    private String objectKey;
    private String fileUrl;
}