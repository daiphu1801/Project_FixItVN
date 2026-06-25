package com.fixit.domain.upload.service;

import com.fixit.domain.auth.entity.User;
import com.fixit.domain.auth.repository.UserRepository;
import com.fixit.domain.upload.config.CloudinaryProperties;
import com.fixit.domain.upload.dto.request.PresignedUrlRequest;
import com.fixit.domain.upload.dto.request.UploadConfirmRequest;
import com.fixit.domain.upload.dto.response.PresignedUrlResponse;
import com.fixit.domain.upload.dto.response.UploadedFileResponse;
import com.fixit.domain.upload.entity.StorageProvider;
import com.fixit.domain.upload.entity.UploadPurpose;
import com.fixit.domain.upload.entity.UploadStatus;
import com.fixit.domain.upload.entity.UploadedFile;
import com.fixit.domain.upload.repository.UploadedFileRepository;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import com.fixit.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final UploadedFileRepository uploadedFileRepository;
    private final UserRepository userRepository;
    private final StorageUploadSigner storageUploadSigner;
    private final StorageUploadVerifier storageUploadVerifier;
    private final CloudinaryProperties cloudinaryProperties;
    private final UploadValidationPolicy uploadValidationPolicy;

    @Override
    @Transactional
    public PresignedUrlResponse createPresignedUrl(PresignedUrlRequest request) {
        User currentUser = getCurrentUser();

        UploadPurpose purpose = uploadValidationPolicy.validatePurpose(request.getPurpose());
        String originalFileName = uploadValidationPolicy.validateOriginalFileName(
                request.getOriginalFileName()
        );

        String contentType = uploadValidationPolicy.validateContentType(
                request.getContentType(),
                purpose
        );

        long fileSize = uploadValidationPolicy.validateFileSize(
                request.getFileSize(),
                getMaxFileSizeBytes()
        );

        String objectKey = generateUniqueObjectKey(currentUser.getId(), purpose);
        OffsetDateTime expiresAt = OffsetDateTime.now().plusSeconds(getExpireSeconds());
        String fileUrl = storageUploadSigner.buildFileUrl(objectKey);

        UploadedFile uploadedFile = UploadedFile.builder()
                .owner(currentUser)
                .purpose(purpose)
                .originalFileName(originalFileName)
                .contentType(contentType)
                .fileSize(fileSize)
                .storageProvider(StorageProvider.CLOUDINARY)
                .objectKey(objectKey)
                .fileUrl(fileUrl)
                .status(UploadStatus.PENDING)
                .expiresAt(expiresAt)
                .createdAt(OffsetDateTime.now())
                .build();

        UploadedFile saved = uploadedFileRepository.save(uploadedFile);

        Map<String, String> formData = storageUploadSigner.createSignedFormData(
                objectKey,
                purpose
        );

        return PresignedUrlResponse.builder()
                .uploadId(saved.getId())
                .storageProvider(saved.getStorageProvider().name())
                .uploadUrl(storageUploadSigner.getUploadUrl())
                .method("POST")
                .objectKey(saved.getObjectKey())
                .fileUrl(saved.getFileUrl())
                .expiresAt(saved.getExpiresAt())
                .expiresInSeconds(getExpireSeconds())
                .formData(formData)
                .build();
    }

    @Override
    @Transactional
    public UploadedFileResponse confirmUpload(UploadConfirmRequest request) {
        User currentUser = getCurrentUser();

        UploadedFile uploadedFile = uploadedFileRepository.findById(request.getUploadId())
                .orElseThrow(() -> new AppException(ErrorCode.UPLOAD_NOT_FOUND));

        validateOwner(uploadedFile, currentUser);

        if (uploadedFile.getStatus() == UploadStatus.CONFIRMED) {
            validateConfirmMatches(uploadedFile, request);
            verifyProviderObject(uploadedFile);
            return toResponse(uploadedFile);
        }

        if (uploadedFile.getStatus() == UploadStatus.EXPIRED) {
            throw new AppException(ErrorCode.UPLOAD_EXPIRED);
        }

        if (uploadedFile.getStatus() != UploadStatus.PENDING) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_STATUS);
        }

        if (isExpired(uploadedFile)) {
            uploadedFile.setStatus(UploadStatus.EXPIRED);
            uploadedFileRepository.save(uploadedFile);
            throw new AppException(ErrorCode.UPLOAD_EXPIRED);
        }

        validateConfirmMatches(uploadedFile, request);

        VerifiedStorageObject verifiedObject = verifyProviderObject(uploadedFile);

        String providerSecureUrl = uploadValidationPolicy.validateFileUrl(
                verifiedObject.getSecureUrl(),
                uploadedFile.getObjectKey(),
                cloudinaryProperties.getCloudName()
        );

        uploadedFile.setFileUrl(providerSecureUrl);
        uploadedFile.setStatus(UploadStatus.CONFIRMED);
        uploadedFile.setConfirmedAt(OffsetDateTime.now());

        UploadedFile saved = uploadedFileRepository.save(uploadedFile);
        return toResponse(saved);
    }

    private VerifiedStorageObject verifyProviderObject(UploadedFile uploadedFile) {
        VerifiedStorageObject verifiedObject = storageUploadVerifier.verifyImage(
                uploadedFile.getObjectKey()
        );

        if (!verifiedObject.getPublicId().equals(uploadedFile.getObjectKey())) {
            uploadedFile.setStatus(UploadStatus.FAILED);
            uploadedFileRepository.save(uploadedFile);
            throw new AppException(ErrorCode.UPLOAD_PUBLIC_ID_MISMATCH);
        }

        if (!"image".equals(verifiedObject.getResourceType())) {
            uploadedFile.setStatus(UploadStatus.FAILED);
            uploadedFileRepository.save(uploadedFile);
            throw new AppException(ErrorCode.UPLOAD_PROVIDER_RESOURCE_TYPE_INVALID);
        }

        // Không kiểm tra strict bytes vì Cloudinary nén ảnh sau khi upload
        // nên bytes trả về khác với fileSize gốc từ Android là bình thường.

        return verifiedObject;
    }

    private void validateOwner(UploadedFile uploadedFile, User currentUser) {
        if (uploadedFile.getOwner() == null
                || uploadedFile.getOwner().getId() == null
                || !uploadedFile.getOwner().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    private void validateConfirmMatches(UploadedFile uploadedFile, UploadConfirmRequest request) {
        String requestObjectKey = uploadValidationPolicy.validateObjectKey(request.getObjectKey());

        if (!uploadedFile.getObjectKey().equals(requestObjectKey)) {
            throw new AppException(ErrorCode.UPLOAD_PUBLIC_ID_MISMATCH);
        }

        String requestContentType = uploadValidationPolicy.validateContentType(
                request.getContentType(),
                uploadedFile.getPurpose()
        );

        if (!uploadedFile.getContentType().equals(requestContentType)) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_CONTENT_TYPE);
        }

        long requestFileSize = uploadValidationPolicy.validateFileSize(
                request.getFileSize(),
                getMaxFileSizeBytes()
        );

        // Lưu ý: không so sánh strict file size ở đây vì Cloudinary có thể
        // trả về bytes khác với size gốc do nén ảnh. Chỉ kiểm tra size gửi lên
        // không quá giới hạn tối đa (đã check ở trên).

        uploadValidationPolicy.validateFileUrl(
                request.getFileUrl(),
                uploadedFile.getObjectKey(),
                cloudinaryProperties.getCloudName()
        );
    }

    private boolean isExpired(UploadedFile uploadedFile) {
        return uploadedFile.getExpiresAt() != null
                && uploadedFile.getExpiresAt().isBefore(OffsetDateTime.now());
    }

    private User getCurrentUser() {
        String phoneNumber = SecurityUtil.getCurrentUserPhone();

        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private String generateUniqueObjectKey(UUID userId, UploadPurpose purpose) {
        String objectKey = generateObjectKey(userId, purpose);

        while (uploadedFileRepository.existsByObjectKey(objectKey)) {
            objectKey = generateObjectKey(userId, purpose);
        }

        return objectKey;
    }

    private String generateObjectKey(UUID userId, UploadPurpose purpose) {
        return "fixit/"
                + purpose.name()
                + "/"
                + userId
                + "/"
                + UUID.randomUUID();
    }

    private int getExpireSeconds() {
        Integer value = cloudinaryProperties.getUploadExpireSeconds();

        if (value == null || value <= 0) {
            return 300;
        }

        return value;
    }

    private long getMaxFileSizeBytes() {
        Long value = cloudinaryProperties.getMaxFileSizeBytes();

        if (value == null || value <= 0) {
            return 10_485_760L;
        }

        return value;
    }

    private UploadedFileResponse toResponse(UploadedFile uploadedFile) {
        return UploadedFileResponse.builder()
                .uploadId(uploadedFile.getId())
                .purpose(uploadedFile.getPurpose() != null ? uploadedFile.getPurpose().name() : null)
                .storageProvider(uploadedFile.getStorageProvider() != null ? uploadedFile.getStorageProvider().name() : null)
                .objectKey(uploadedFile.getObjectKey())
                .fileUrl(uploadedFile.getFileUrl())
                .contentType(uploadedFile.getContentType())
                .fileSize(uploadedFile.getFileSize())
                .status(uploadedFile.getStatus() != null ? uploadedFile.getStatus().name() : null)
                .expiresAt(uploadedFile.getExpiresAt())
                .confirmedAt(uploadedFile.getConfirmedAt())
                .build();
    }
}