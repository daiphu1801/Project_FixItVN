package com.fixit.domain.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponse {
    private UUID id;
    private UUID bookingId;
    private String customerReason;
    private String workerResponse;
    private List<String> evidenceImageUrls;
    private List<String> workerEvidenceImageUrls;
    private String status;
    private OffsetDateTime deadlineToRespond;
    private OffsetDateTime createdAt;
}
