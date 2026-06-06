package com.fixit.domain.upload.service;

public interface StorageUploadVerifier {

    VerifiedStorageObject verifyImage(String objectKey);
}