package com.fixit.domain.upload.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fixit.domain.upload.config.CloudinaryProperties;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CloudinaryStorageUploadVerifier implements StorageUploadVerifier {

    private final Cloudinary cloudinary;
    private final CloudinaryProperties properties;

    @Override
    public VerifiedStorageObject verifyImage(String objectKey) {
        ensureConfigured();

        try {
            Map result = cloudinary.api().resource(
                    objectKey,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "type", "upload"
                    )
            );

            if (result == null || result.isEmpty()) {
                throw new AppException(ErrorCode.UPLOAD_PROVIDER_OBJECT_NOT_FOUND);
            }

            String publicId = readString(result, "public_id");
            String secureUrl = readString(result, "secure_url");
            String resourceType = readString(result, "resource_type");
            String format = readString(result, "format");
            Long bytes = readLong(result, "bytes");

            if (!objectKey.equals(publicId)) {
                throw new AppException(ErrorCode.UPLOAD_PUBLIC_ID_MISMATCH);
            }

            if (!"image".equals(resourceType)) {
                throw new AppException(ErrorCode.UPLOAD_PROVIDER_RESOURCE_TYPE_INVALID);
            }

            return VerifiedStorageObject.builder()
                    .publicId(publicId)
                    .secureUrl(secureUrl)
                    .resourceType(resourceType)
                    .format(format)
                    .bytes(bytes)
                    .build();

        } catch (AppException ex) {
            throw ex;
        } catch (Exception ex) {
            if (isNotFoundError(ex)) {
                throw new AppException(ErrorCode.UPLOAD_PROVIDER_OBJECT_NOT_FOUND);
            }

            throw new AppException(ErrorCode.UPLOAD_STORAGE_ERROR);
        }
    }

    private String readString(Map result, String key) {
        Object value = result.get(key);

        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }

    private Long readLong(Map result, String key) {
        Object value = result.get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean isNotFoundError(Exception ex) {
        String message = ex.getMessage();

        if (message == null) {
            return false;
        }

        String lowerMessage = message.toLowerCase();

        return lowerMessage.contains("not found")
                || lowerMessage.contains("404")
                || lowerMessage.contains("resource not found");
    }

    private void ensureConfigured() {
        if (isBlank(properties.getCloudName())
                || isBlank(properties.getApiKey())
                || isBlank(properties.getApiSecret())) {
            throw new AppException(ErrorCode.UPLOAD_STORAGE_ERROR);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank() || value.startsWith("YOUR_");
    }
}