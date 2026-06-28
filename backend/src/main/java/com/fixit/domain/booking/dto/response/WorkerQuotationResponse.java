package com.fixit.domain.booking.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class WorkerQuotationResponse {
    private UUID id;
    private UUID bookingId;
    private UUID workerId;
    private BigDecimal laborCost;
    private BigDecimal materialCost;
    private BigDecimal totalProposedPrice;
    private String status;
    private OffsetDateTime createdAt;
}
