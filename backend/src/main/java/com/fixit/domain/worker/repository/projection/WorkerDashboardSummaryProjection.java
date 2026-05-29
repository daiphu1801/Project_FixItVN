package com.fixit.domain.worker.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface WorkerDashboardSummaryProjection {

    UUID getWorkerId();

    String getFullName();

    String getAvatarUrl();

    Boolean getAvailable();

    String getVerificationStatus();

    BigDecimal getReputationScore();

    BigDecimal getLatitude();

    BigDecimal getLongitude();

    Integer getTodayAppointmentCount();

    Integer getPendingAssignmentCount();

    Integer getUnreadNotificationCount();

    BigDecimal getAvailableBalance();

    BigDecimal getHeldBalance();

    BigDecimal getDebtBalance();
}