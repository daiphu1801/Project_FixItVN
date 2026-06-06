package com.fixit.domain.upload.entity;

import com.fixit.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "uploaded_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 50)
    private UploadPurpose purpose;

    @Column(name = "original_file_name", length = 255)
    private String originalFileName;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "storage_provider", nullable = false, length = 30)
    private StorageProvider storageProvider = StorageProvider.CLOUDINARY;

    @Column(name = "object_key", nullable = false, unique = true, columnDefinition = "text")
    private String objectKey;

    @Column(name = "file_url", nullable = false, columnDefinition = "text")
    private String fileUrl;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private UploadStatus status = UploadStatus.PENDING;

    @Column(name = "linked_entity_type", length = 50)
    private String linkedEntityType;

    @Column(name = "linked_entity_id")
    private UUID linkedEntityId;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "confirmed_at")
    private OffsetDateTime confirmedAt;

    @Column(name = "used_at")
    private OffsetDateTime usedAt;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}