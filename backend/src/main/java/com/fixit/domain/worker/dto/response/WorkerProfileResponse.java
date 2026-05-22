package com.fixit.domain.worker.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerProfileResponse {

    private UUID workerId;

    private String fullName;

    private String phoneNumber;

    private String email;

    private String avatarUrl;

    private String identityCard;

    private String verificationStatus;

    private Boolean available;

    private BigDecimal reputationScore;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String experienceDescription;

    private String serviceArea;
}