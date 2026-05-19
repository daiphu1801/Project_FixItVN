package com.fixit.domain.worker.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class WorkerHomeResponse {

    private UUID workerId;

    private String fullName;

    private Boolean available;

    private String verificationStatus;

    private BigDecimal reputationScore;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer todayAppointmentCount;

    private Integer pendingAssignmentCount;

    private BigDecimal availableBalance;

    private BigDecimal heldBalance;

    private BigDecimal debtBalance;
}