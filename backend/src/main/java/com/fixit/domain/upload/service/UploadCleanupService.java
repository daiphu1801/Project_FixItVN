package com.fixit.domain.upload.service;

public interface UploadCleanupService {

    void cleanupExpiredPendingUploads();

    void cleanupUnusedConfirmedUploads();
}