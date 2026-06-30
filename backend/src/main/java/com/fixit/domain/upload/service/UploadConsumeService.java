package com.fixit.domain.upload.service;

import com.fixit.domain.upload.entity.UploadLinkedEntityType;
import com.fixit.domain.upload.entity.UploadPurpose;

import java.util.List;
import java.util.UUID;

public interface UploadConsumeService {

    ConsumedUpload consume(
            UUID uploadId,
            UUID ownerUserId,
            UploadPurpose expectedPurpose,
            UploadLinkedEntityType linkedEntityType,
            UUID linkedEntityId
    );

    List<ConsumedUpload> consumeAll(
            List<UUID> uploadIds,
            UUID ownerUserId,
            UploadPurpose expectedPurpose,
            UploadLinkedEntityType linkedEntityType,
            UUID linkedEntityId
    );
}