package com.fixit.domain.worker.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface WorkerHistoryItemProjection {

    UUID getBookingId();

    String getServiceName();

    String getCustomerName();

    String getAddress();

    String getStatus();

    String getScheduledTime();

    String getFinishedAt();

    BigDecimal getFinalPrice();

    String getPaymentMethod();

    String getIssueDescription();
}