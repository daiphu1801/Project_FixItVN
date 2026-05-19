package com.fixit.domain.worker.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface WorkerHomeProjection {

    UUID getWorkerId();

    String getFullName();

    Boolean getAvailable();

    String getVerificationStatus();

    BigDecimal getReputationScore();

    BigDecimal getLatitude();

    BigDecimal getLongitude();

    Integer getTodayAppointmentCount();

    Integer getPendingAssignmentCount();

    BigDecimal getAvailableBalance();

    BigDecimal getHeldBalance();

    BigDecimal getDebtBalance();
}