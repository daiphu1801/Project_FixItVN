package com.fixit.feature.customer.complaint.data.remote.mapper;

import com.fixit.feature.customer.complaint.data.remote.dto.ComplaintResponseDto;
import com.fixit.feature.customer.complaint.domain.model.Complaint;

import java.util.ArrayList;

public class ComplaintMapper {
    public static Complaint toDomain(ComplaintResponseDto dto) {
        if (dto == null) return null;
        return new Complaint(
                dto.getId(),
                dto.getBookingId(),
                dto.getCustomerReason(),
                dto.getWorkerResponse(),
                dto.getEvidenceImageUrls() != null ? dto.getEvidenceImageUrls() : new ArrayList<>(),
                dto.getWorkerEvidenceImageUrls() != null ? dto.getWorkerEvidenceImageUrls() : new ArrayList<>(),
                dto.getStatus(),
                dto.getDeadlineToRespond(),
                dto.getCreatedAt()
        );
    }
}
