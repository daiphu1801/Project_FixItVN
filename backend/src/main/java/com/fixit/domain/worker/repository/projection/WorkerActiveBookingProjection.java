package com.fixit.domain.worker.repository.projection;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface WorkerActiveBookingProjection {

    UUID getBookingId();

    String getServiceName();

    String getCustomerName();

    String getAddress();

    String getStatus();

    OffsetDateTime getScheduledTime();

    BigDecimal getFinalPrice();
}