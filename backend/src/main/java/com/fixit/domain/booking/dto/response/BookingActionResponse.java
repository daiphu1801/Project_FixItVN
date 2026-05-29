package com.fixit.domain.booking.dto.response;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingActionResponse {

    private UUID bookingId;

    private String bookingStatus;

    private String action;

    private String nextAction;

    private String message;

    private OffsetDateTime updatedAt;
}