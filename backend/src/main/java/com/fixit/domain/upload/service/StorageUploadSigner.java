package com.fixit.domain.upload.service;

import com.fixit.domain.upload.entity.UploadPurpose;

import java.util.Map;

public interface StorageUploadSigner {

    String getUploadUrl();

    String buildFileUrl(String objectKey);

    Map<String, String> createSignedFormData(String objectKey, UploadPurpose purpose);
}