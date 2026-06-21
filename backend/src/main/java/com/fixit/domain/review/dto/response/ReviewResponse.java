package com.fixit.domain.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private UUID id;
    private UUID bookingId;
    private UUID customerId;
    private String customerName;
    private String customerAvatar;
    private Integer rating;
    private String comment;
    private OffsetDateTime createdAt;
}
