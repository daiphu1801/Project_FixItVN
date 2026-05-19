package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.RejectAssignmentRequest;
import com.fixit.domain.booking.dto.response.AssignmentActionResponse;
import com.fixit.domain.booking.dto.response.PendingAssignmentResponse;

import java.util.UUID;

public interface WorkerAssignmentService {

    PendingAssignmentResponse getPendingAssignments();

    AssignmentActionResponse accept(UUID bookingId, UUID assignmentId);

    AssignmentActionResponse reject(
            UUID bookingId,
            UUID assignmentId,
            RejectAssignmentRequest request
    );

    AssignmentActionResponse miss(UUID bookingId, UUID assignmentId);
}