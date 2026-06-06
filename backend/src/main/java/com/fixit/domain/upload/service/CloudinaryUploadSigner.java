package com.fixit.domain.upload.service;

import com.fixit.domain.upload.config.CloudinaryProperties;
import com.fixit.domain.upload.entity.UploadPurpose;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class CloudinaryUploadSigner implements StorageUploadSigner {

    private final CloudinaryProperties properties;

    @Override
    public String getUploadUrl() {
        ensureConfigured();
        return "https://api.cloudinary.com/v1_1/" + properties.getCloudName() + "/image/upload";
    }

    @Override
    public String buildFileUrl(String objectKey) {
        ensureConfigured();
        return "https://res.cloudinary.com/" + properties.getCloudName() + "/image/upload/" + objectKey;
    }

    @Override
    public Map<String, String> createSignedFormData(String objectKey, UploadPurpose purpose) {
        ensureConfigured();

        long timestamp = Instant.now().getEpochSecond();

        Map<String, String> paramsToSign = new HashMap<>();
        paramsToSign.put("public_id", objectKey);
        paramsToSign.put("timestamp", String.valueOf(timestamp));
        paramsToSign.put("overwrite", "false");
        paramsToSign.put("context", "purpose=" + purpose.name());

        String signature = sign(paramsToSign, properties.getApiSecret());

        Map<String, String> formData = new HashMap<>(paramsToSign);
        formData.put("api_key", properties.getApiKey());
        formData.put("signature", signature);

        return formData;
    }

    private String sign(Map<String, String> params, String apiSecret) {
        try {
            TreeMap<String, String> sorted = new TreeMap<>(params);
            StringBuilder raw = new StringBuilder();

            for (Map.Entry<String, String> entry : sorted.entrySet()) {
                if (entry.getValue() == null || entry.getValue().isBlank()) {
                    continue;
                }

                if (raw.length() > 0) {
                    raw.append("&");
                }

                raw.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
            }

            raw.append(apiSecret);

            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(raw.toString().getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();
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