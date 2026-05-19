package com.fixit.domain.booking.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AssignmentActionResponse {

    private UUID bookingId;
    private UUID assignmentId;

    private String assignmentStatus;
    private String bookingStatus;

    /**
     * Gợi ý cho Android biết bước tiếp theo.
     *
     * Ví dụ:
     * - START_MOVING
     * - WAIT_NEXT_ASSIGNMENT
     */
    private String nextAction;

    private String message;
}