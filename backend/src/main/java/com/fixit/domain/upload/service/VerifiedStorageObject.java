package com.fixit.domain.upload.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerifiedStorageObject {
    private String publicId;
    private String secureUrl;
    private String resourceType;
    private String format;
    private Long bytes;
}