package com.fixit.domain.worker.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class WorkerKycResponse {
    private UUID kycId;
    private UUID workerId;
    private String frontImageUrl;
    private String backImageUrl;
    private String selfieImageUrl;
    private String ocrFullName;
    private String ocrIdentityCard;
    private java.math.BigDecimal similarityScore;
    private List<String> certificateUrls;
    private String status;
}