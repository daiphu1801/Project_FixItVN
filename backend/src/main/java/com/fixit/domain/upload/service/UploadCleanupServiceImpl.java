package com.fixit.domain.upload.service;

import com.fixit.domain.upload.config.UploadCleanupProperties;
import com.fixit.domain.upload.entity.UploadStatus;
import com.fixit.domain.upload.entity.UploadedFile;
import com.fixit.domain.upload.repository.UploadedFileRepository;
import com.fixit.global.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadCleanupServiceImpl implements UploadCleanupService {

    private final UploadedFileRepository uploadedFileRepository;
    private final StorageUploadCleaner storageUploadCleaner;
    private final UploadCleanupProperties cleanupProperties;

    @Override
    public void cleanupExpiredPendingUploads() {
        int batchSize = getBatchSize();
        OffsetDateTime now = OffsetDateTime.now();

        List<UploadedFile> expiredPendingUploads = uploadedFileRepository
                .findByStatusAndExpiresAtBefore(
                        UploadStatus.PENDING,
                        now,
                        PageRequest.of(0, batchSize)
                );

        if (expiredPendingUploads.isEmpty()) {
            log.debug("[UploadCleanup] No expired PENDING uploads found");
            return;
        }

        int successCount = 0;
        int failedCount = 0;

        for (UploadedFile uploadedFile : expiredPendingUploads) {
            try {
                cleanupOneExpiredPendingUpload(uploadedFile);
                successCount++;
            } catch (Exception ex) {
                failedCount++;
                log.warn(
                        "[UploadCleanup] Failed to cleanup expired PENDING upload. uploadId={}, objectKey={}, error={}",
                        uploadedFile.getId(),
                        uploadedFile.getObjectKey(),
                        ex.getMessage()
                );
            }
        }

        log.info(
                "[UploadCleanup] Expired PENDING cleanup finished. total={}, success={}, failed={}",
                expiredPendingUploads.size(),
                successCount,
                failedCount
        );
    }

    @Override
    public void cleanupUnusedConfirmedUploads() {
        if (!cleanupProperties.isCleanupUnusedConfirmed()) {
            log.debug("[UploadCleanup] Unused CONFIRMED cleanup is disabled");
            return;
        }

        int batchSize = getBatchSize();
        OffsetDateTime threshold = OffsetDateTime.now()
                .minusHours(cleanupProperties.getUnusedConfirmedExpireHours());

        List<UploadedFile> unusedConfirmedUploads = uploadedFileRepository
                .findByStatusAndUsedAtIsNullAndConfirmedAtBefore(
                        UploadStatus.CONFIRMED,
                        threshold,
                        PageRequest.of(0, batchSize)
                );

        if (unusedConfirmedUploads.isEmpty()) {
            log.debug("[UploadCleanup] No unused CONFIRMED uploads found");
            return;
        }

        int successCount = 0;
        int failedCount = 0;

        for (UploadedFile uploadedFile : unusedConfirmedUploads) {
            try {
                cleanupOneUnusedConfirmedUpload(uploadedFile);
                successCount++;
            } catch (Exception ex) {
                failedCount++;
                log.warn(
                        "[UploadCleanup] Failed to cleanup unused CONFIRMED upload. uploadId={}, objectKey={}, error={}",
                        uploadedFile.getId(),
                        uploadedFile.getObjectKey(),
                        ex.getMessage()
                );
            }
        }

        log.info(
                "[UploadCleanup] Unused CONFIRMED cleanup finished. total={}, success={}, failed={}",
                unusedConfirmedUploads.size(),
                successCount,
                failedCount
        );
    }

    private void cleanupOneExpiredPendingUpload(UploadedFile uploadedFile) {
        if (cleanupProperties.isDeleteExpiredPendingFromStorage()) {
            deleteFromStorageIfPossible(uploadedFile);
        }

        uploadedFile.setStatus(UploadStatus.EXPIRED);
        uploadedFileRepository.save(uploadedFile);

        log.debug(
                "[UploadCleanup] Marked expired PENDING upload as EXPIRED. uploadId={}, objectKey={}",
                uploadedFile.getId(),
                uploadedFile.getObjectKey()
        );
    }

    private void cleanupOneUnusedConfirmedUpload(UploadedFile uploadedFile) {
        // Mặc định chức năng này đang tắt.
        // Chỉ bật khi bạn đã có đợt 5: API nghiệp vụ set used_at/link entity rõ ràng.
        deleteFromStorageIfPossible(uploadedFile);

        uploadedFile.setStatus(UploadStatus.EXPIRED);
        uploadedFileRepository.save(uploadedFile);

        log.debug(
                "[UploadCleanup] Marked unused CONFIRMED upload as EXPIRED. uploadId={}, objectKey={}",
                uploadedFile.getId(),
                uploadedFile.getObjectKey()
        );
    }

    private void deleteFromStorageIfPossible(UploadedFile uploadedFile) {
        try {
            boolean deleted = storageUploadCleaner.deleteImage(uploadedFile.getObjectKey());

            if (!deleted) {
                throw new IllegalStateException("Storage delete returned false");
            }

            log.debug(
                    "[UploadCleanup] Deleted object from storage. uploadId={}, objectKey={}",
                    uploadedFile.getId(),
                    uploadedFile.getObjectKey()
            );

        } catch (AppException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot delete object from storage", ex);
        }
    }

    private int getBatchSize() {
        int batchSize = cleanupProperties.getBatchSize();

        if (batchSize <= 0) {
            return 100;
        }

        return batchSize;
    }
}