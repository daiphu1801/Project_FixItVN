package com.fixit.domain.upload.repository;

import com.fixit.domain.upload.entity.UploadStatus;
import com.fixit.domain.upload.entity.UploadedFile;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, UUID> {

    Optional<UploadedFile> findByIdAndOwner_Id(UUID id, UUID ownerId);

    Optional<UploadedFile> findByObjectKey(String objectKey);

    boolean existsByObjectKey(String objectKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT uploadedFile
            FROM UploadedFile uploadedFile
            WHERE uploadedFile.id = :uploadId
            """)
    Optional<UploadedFile> findByIdForUpdate(@Param("uploadId") UUID uploadId);

    List<UploadedFile> findByStatusAndExpiresAtBefore(
            UploadStatus status,
            OffsetDateTime now
    );

    List<UploadedFile> findByStatusAndExpiresAtBefore(
            UploadStatus status,
            OffsetDateTime now,
            Pageable pageable
    );

    List<UploadedFile> findByStatusAndUsedAtIsNullAndConfirmedAtBefore(
            UploadStatus status,
            OffsetDateTime confirmedBefore,
            Pageable pageable
    );

    List<UploadedFile> findByLinkedEntityTypeAndLinkedEntityId(
            String linkedEntityType,
            UUID linkedEntityId
    );
}