package com.fixit.domain.booking.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface PendingAssignmentProjection {

    UUID getAssignmentId();

    UUID getBookingId();

    String getServiceName();

    String getCustomerName();

    String getAddressPreview();

    String getIssueDescription();

    String getScheduledTime();

    String getAssignedAt();

    BigDecimal getDestinationLat();

    BigDecimal getDestinationLng();

    BigDecimal getFinalPrice();

    String getPaymentMethod();
}