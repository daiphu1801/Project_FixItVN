package com.fixit.domain.upload.service;

import com.fixit.domain.upload.entity.UploadPurpose;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

@Component
public class UploadValidationPolicy {

    private static final int MAX_OBJECT_KEY_LENGTH = 500;
    private static final int MAX_FILE_URL_LENGTH = 1000;
    private static final int MAX_ORIGINAL_FILE_NAME_LENGTH = 255;

    private static final Pattern CONTROL_CHARACTER_PATTERN = Pattern.compile("[\\p{Cntrl}]");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

    private static final Set<String> IMAGE_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
            "image/heic",
            "image/heif"
    );

    public UploadPurpose validatePurpose(String rawPurpose) {
        if (rawPurpose == null || rawPurpose.isBlank()) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_PURPOSE);
        }

        try {
            return UploadPurpose.valueOf(rawPurpose.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_PURPOSE);
        }
    }

    public String validateContentType(String rawContentType, UploadPurpose purpose) {
        if (rawContentType == null || rawContentType.isBlank()) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_CONTENT_TYPE);
        }

        String contentType = rawContentType.trim().toLowerCase();

        if (!isAllowedContentTypeForPurpose(contentType, purpose)) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_CONTENT_TYPE);
        }

        return contentType;
    }

    public long validateFileSize(Long fileSize, long maxFileSizeBytes) {
        if (fileSize == null || fileSize <= 0) {
            throw new AppException(ErrorCode.INVALID_REQUEST_PARAMETER);
        }

        if (fileSize > maxFileSizeBytes) {
            throw new AppException(ErrorCode.UPLOAD_FILE_TOO_LARGE);
        }

        return fileSize;
    }

    public String validateOriginalFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return null;
        }

        String normalized = originalFileName.trim();

        if (normalized.length() > MAX_ORIGINAL_FILE_NAME_LENGTH) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_FILE_NAME);
        }

        if (CONTROL_CHARACTER_PATTERN.matcher(normalized).find()) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_FILE_NAME);
        }

        if (normalized.contains("/")
                || normalized.contains("\\")
                || normalized.contains("..")
                || normalized.contains("\0")) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_FILE_NAME);
        }

        return normalized;
    }

    public String validateObjectKey(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_OBJECT_KEY);
        }

        String normalized = objectKey.trim();

        if (normalized.length() > MAX_OBJECT_KEY_LENGTH) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_OBJECT_KEY);
        }

        if (!normalized.startsWith("fixit/")) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_OBJECT_KEY);
        }

        if (normalized.contains("..")
                || normalized.contains("\\")
                || normalized.contains("\0")
                || WHITESPACE_PATTERN.matcher(normalized).find()) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_OBJECT_KEY);
        }

        return normalized;
    }

    public String validateFileUrl(String fileUrl, String objectKey, String cloudName) {
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_FILE_URL);
        }

        String normalized = fileUrl.trim();

        if (normalized.length() > MAX_FILE_URL_LENGTH) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_FILE_URL);
        }

        String expectedPrefix = "https://res.cloudinary.com/" + cloudName + "/";

        if (!normalized.startsWith(expectedPrefix)) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_FILE_URL);
        }

        if (!normalized.contains("/" + objectKey)) {
            throw new AppException(ErrorCode.UPLOAD_PUBLIC_ID_MISMATCH);
        }

        if (normalized.contains("\0") || CONTROL_CHARACTER_PATTERN.matcher(normalized).find()) {
            throw new AppException(ErrorCode.UPLOAD_INVALID_FILE_URL);
        }

        return normalized;
    }

    private boolean isAllowedContentTypeForPurpose(String contentType, UploadPurpose purpose) {
        if (purpose == null) {
            return false;
        }

        // MVP hiện tại chỉ nhận ảnh. Nếu sau này WORKER_CERTIFICATE cần PDF,
        // hãy tách resource_type hoặc upload endpoint riêng ở đợt sau.
        return IMAGE_CONTENT_TYPES.contains(contentType);
    }
}
