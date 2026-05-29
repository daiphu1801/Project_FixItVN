package com.fixit.domain.booking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PendingAssignmentItemResponse {

    private UUID assignmentId;
    private UUID bookingId;

    private String serviceName;
    private String customerName;

    /**
     * Giai đoạn MVP đang trả địa chỉ đầy đủ để dễ test.
     * Production nên mask số nhà trước khi thợ accept.
     */
    private String addressPreview;

    private String issueDescription;

    private OffsetDateTime scheduledTime;
    private OffsetDateTime assignedAt;
    private OffsetDateTime expiresAt;

    /**
     * Số giây còn lại để accept/reject.
     */
    private Integer remainingSeconds;

    private BigDecimal destinationLat;
    private BigDecimal destinationLng;
    private BigDecimal finalPrice;
    private String paymentMethod;
}