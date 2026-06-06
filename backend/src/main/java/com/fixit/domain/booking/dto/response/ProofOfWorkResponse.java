package com.fixit.domain.booking.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class ProofOfWorkResponse {
    private UUID proofId;
    private UUID bookingId;
    private String imageUrl;
    private String proofType;
    private OffsetDateTime capturedAt;
}