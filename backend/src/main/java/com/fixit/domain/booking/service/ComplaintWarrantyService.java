package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.ComplaintRequest;
import com.fixit.domain.booking.dto.request.WorkerComplaintResponseRequest;
import com.fixit.domain.booking.dto.response.ComplaintResponse;

import java.util.UUID;

public interface ComplaintWarrantyService {
    ComplaintResponse createComplaint(UUID customerId, UUID bookingId, ComplaintRequest request);
    ComplaintResponse getComplaint(UUID bookingId);
    void cancelComplaint(UUID customerId, UUID bookingId, UUID complaintId);
    ComplaintResponse respondToComplaint(UUID workerUserId, UUID bookingId, WorkerComplaintResponseRequest request);
}
