package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.response.VnptFaceMatchResult;
import com.fixit.domain.worker.dto.response.VnptOcrResult;
import com.fixit.global.config.VnptKycProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class VnptKycClient {

    private final VnptKycProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    public VnptOcrResult performOcr(byte[] imageBytes, String side) {
        if (isMockEnabled()) {
            log.info("[MOCK] VNPT OCR side {} completed.", side);
            return VnptOcrResult.builder()
                    .success(true)
                    .idNumber("123456789012")
                    .fullName("NGUYEN VAN THO")
                    .build();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("client-id", properties.getClientId());
            headers.set("client-secret", properties.getClientSecret());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return side + ".jpg";
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            String url = properties.getApiUrl() + "/ocr/id-card";

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map bodyMap = response.getBody();
                String status = String.valueOf(bodyMap.getOrDefault("status", ""));
                if ("0".equals(status) || "200".equals(status) || bodyMap.containsKey("data")) {
                    Map data = (Map) bodyMap.get("data");
                    if (data != null) {
                        String id = String.valueOf(data.getOrDefault("id", ""));
                        String name = String.valueOf(data.getOrDefault("name", ""));
                        return VnptOcrResult.builder()
                                .success(true)
                                .idNumber(id)
                                .fullName(name)
                                .build();
                    }
                }
                return VnptOcrResult.builder()
                        .success(false)
                        .errorCode(status)
                        .errorMessage("API VNPT trả về mã lỗi: " + status)
                        .build();
            }
            return VnptOcrResult.builder()
                    .success(false)
                    .errorMessage("Không nhận được phản hồi từ VNPT")
                    .build();
        } catch (Exception e) {
            log.error("Error during VNPT OCR", e);
            return VnptOcrResult.builder()
                    .success(false)
                    .errorMessage("Lỗi kết nối VNPT: " + e.getMessage())
                    .build();
        }
    }

    public VnptFaceMatchResult matchFaces(byte[] faceImage1, byte[] faceImage2) {
        if (isMockEnabled()) {
            log.info("[MOCK] VNPT Face Match completed.");
            return VnptFaceMatchResult.builder()
                    .success(true)
                    .similarityScore(92.5)
                    .build();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("client-id", properties.getClientId());
            headers.set("client-secret", properties.getClientSecret());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image1", new ByteArrayResource(faceImage1) {
                @Override
                public String getFilename() {
                    return "face1.jpg";
                }
            });
            body.add("image2", new ByteArrayResource(faceImage2) {
                @Override
                public String getFilename() {
                    return "face2.jpg";
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            String url = properties.getApiUrl() + "/face-matching";

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map bodyMap = response.getBody();
                String status = String.valueOf(bodyMap.getOrDefault("status", ""));
                if ("0".equals(status) || "200".equals(status) || bodyMap.containsKey("data")) {
                    Map data = (Map) bodyMap.get("data");
                    if (data != null) {
                        Object similarityObj = data.get("similarity");
                        double score = 0.0;
                        if (similarityObj instanceof Number) {
                            score = ((Number) similarityObj).doubleValue();
                            if (score <= 1.0) {
                                score = score * 100;
                            }
                        }
                        return VnptFaceMatchResult.builder()
                                .success(true)
                                .similarityScore(score)
                                .build();
                    }
                }
                return VnptFaceMatchResult.builder()
                        .success(false)
                        .errorCode(status)
                        .errorMessage("API VNPT trả về mã lỗi: " + status)
                        .build();
            }
            return VnptFaceMatchResult.builder()
                    .success(false)
                    .errorMessage("Không nhận được phản hồi từ VNPT")
                    .build();
        } catch (Exception e) {
            log.error("Error during VNPT Face Matching", e);
            return VnptFaceMatchResult.builder()
                    .success(false)
                    .errorMessage("Lỗi kết nối VNPT: " + e.getMessage())
                    .build();
        }
    }

    private boolean isMockEnabled() {
        return properties.getClientId() == null
                || properties.getClientId().isBlank()
                || properties.getClientId().startsWith("YOUR_");
    }
}
