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
public class CloudinaryStorageUploadCleaner implements StorageUploadCleaner {

    private final Cloudinary cloudinary;
    private final CloudinaryProperties properties;

    @Override
    public boolean deleteImage(String objectKey) {
        ensureConfigured();

        try {
            Map result = cloudinary.uploader().destroy(
                    objectKey,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "invalidate", true
                    )
            );

            Object rawResult = result != null ? result.get("result") : null;
            String deleteResult = rawResult != null ? String.valueOf(rawResult) : null;

            // Cloudinary thường trả:
            // result = "ok" nếu xóa thành công
            // result = "not found" nếu file không tồn tại
            // Với cleanup, "not found" vẫn coi là thành công vì mục tiêu là file không còn trên storage.
            return "ok".equalsIgnoreCase(deleteResult)
                    || "not found".equalsIgnoreCase(deleteResult);

        } catch (Exception ex) {
            throw new AppException(ErrorCode.UPLOAD_STORAGE_ERROR);
        }
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