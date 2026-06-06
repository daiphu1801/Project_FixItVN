package com.fixit.domain.upload.service;

import com.fixit.domain.upload.entity.UploadLinkedEntityType;
import com.fixit.domain.upload.entity.UploadPurpose;
import com.fixit.domain.upload.entity.UploadStatus;
import com.fixit.domain.upload.entity.UploadedFile;
import com.fixit.domain.upload.repository.UploadedFileRepository;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadConsumeServiceImpl implements UploadConsumeService {

    private final UploadedFileRepository uploadedFileRepository;

    @Override
    public ConsumedUpload consume(
            UUID uploadId,
            UUID ownerUserId,
            UploadPurpose expectedPurpose,
            UploadLinkedEntityType linkedEntityType,
            UUID linkedEntityId
    ) {
        if (uploadId == null || ownerUserId == null || expectedPurpose == null
                || linkedEntityType == null || linkedEntityId == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST_PARAMETER);
        }

        UploadedFile uploadedFile = uploadedFileRepository.findByIdForUpdate(uploadId)
                .orElseThrow(() -> new AppException(ErrorCode.UPLOAD_NOT_FOUND));

        validateOwner(uploadedFile, ownerUserId);
        validateStatus(uploadedFile);
        validatePurpose(uploadedFile, expectedPurpose);
        if (isAlreadyLinkedToTarget(uploadedFile, linkedEntityType, linkedEntityId)) {
            return toConsumedUpload(uploadedFile);
        }
        validateNotUsed(uploadedFile);

        uploadedFile.setLinkedEntityType(linkedEntityType.name());
        uploadedFile.setLinkedEntityId(linkedEntityId);
        uploadedFile.setUsedAt(OffsetDateTime.now());

        UploadedFile saved = uploadedFileRepository.save(uploadedFile);

        return toConsumedUpload(saved);
    }

    private ConsumedUpload toConsumedUpload(UploadedFile uploadedFile) {
        return ConsumedUpload.builder()
                .uploadId(uploadedFile.getId())
                .purpose(uploadedFile.getPurpose())
                .objectKey(uploadedFile.getObjectKey())
                .fileUrl(uploadedFile.getFileUrl())
                .build();
    }

    @Override
    public List<ConsumedUpload> consumeAll(
            List<UUID> uploadIds,
            UUID ownerUserId,
            UploadPurpose expectedPurpose,
            UploadLinkedEntityType linkedEntityType,
            UUID linkedEntityId
    ) {
        if (uploadIds == null || uploadIds.isEmpty()) {
            return Collections.emptyList();
        }

        return uploadIds.stream()
                .map(uploadId -> consume(
                        uploadId,
                        ownerUserId,
                        expectedPurpose,
                        linkedEntityType,
                        linkedEntityId
                ))
                .toList();
    }

    private void validateOwner(UploadedFile uploadedFile, UUID ownerUserId) {
        if (uploadedFile.getOwner() == null
                || uploadedFile.getOwner().getId() == null
                || !uploadedFile.getOwner().getId().equals(ownerUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateStatus(UploadedFile uploadedFile) {
        if (uploadedFile.getStatus() != UploadStatus.CONFIRMED) {
            throw new AppException(ErrorCode.UPLOAD_NOT_CONFIRMED);
        }
    }

    private void validatePurpose(UploadedFile uploadedFile, UploadPurpose expectedPurpose) {
        if (uploadedFile.getPurpose() != expectedPurpose) {
            throw new AppException(ErrorCode.UPLOAD_PURPOSE_NOT_ALLOWED);
        }
    }

    private void validateNotUsed(UploadedFile uploadedFile) {
        if (uploadedFile.getUsedAt() != null
                || uploadedFile.getLinkedEntityType() != null
                || uploadedFile.getLinkedEntityId() != null) {
            throw new AppException(ErrorCode.UPLOAD_ALREADY_USED);
        }
    }

    private boolean isAlreadyLinkedToTarget(
            UploadedFile uploadedFile,
            UploadLinkedEntityType linkedEntityType,
            UUID linkedEntityId
    ) {
        return uploadedFile.getUsedAt() != null
                && linkedEntityType.name().equals(uploadedFile.getLinkedEntityType())
                && linkedEntityId.equals(uploadedFile.getLinkedEntityId());
    }
}
