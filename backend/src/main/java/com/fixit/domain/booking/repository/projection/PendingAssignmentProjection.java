package com.fixit.domain.booking.repository.projection;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface PendingAssignmentProjection {

    UUID getAssignmentId();

    UUID getBookingId();

    String getServiceName();

    String getCustomerName();

    String getAddressPreview();

    String getIssueDescription();

    Instant getScheduledTime();

    Instant getAssignedAt();

    BigDecimal getDestinationLat();

    BigDecimal getDestinationLng();

    BigDecimal getFinalPrice();

    String getPaymentMethod();
}