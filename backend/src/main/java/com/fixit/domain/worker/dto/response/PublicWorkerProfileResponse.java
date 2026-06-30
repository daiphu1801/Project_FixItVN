package com.fixit.domain.worker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicWorkerProfileResponse {
    private UUID workerId;
    private String fullName;
    private String avatarUrl;
    private BigDecimal reputationScore;
    private Integer totalReviews; // Mock for now
    private String experienceDescription;
    private String serviceArea;
    private Boolean available;
}
