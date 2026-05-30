package com.fixit.domain.upload.scheduler;

import com.fixit.domain.upload.config.UploadCleanupProperties;
import com.fixit.domain.upload.service.UploadCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadCleanupScheduler {

    private final UploadCleanupService uploadCleanupService;
    private final UploadCleanupProperties cleanupProperties;

    @Scheduled(
            fixedDelayString = "${app.upload.cleanup.fixed-delay-ms:1800000}",
            initialDelayString = "${app.upload.cleanup.initial-delay-ms:60000}"
    )
    public void runUploadCleanup() {
        if (!cleanupProperties.isEnabled()) {
            log.debug("[UploadCleanup] Scheduler is disabled");
            return;
        }

        log.info("[UploadCleanup] Scheduler started");

        uploadCleanupService.cleanupExpiredPendingUploads();
        uploadCleanupService.cleanupUnusedConfirmedUploads();

        log.info("[UploadCleanup] Scheduler finished");
    }
}