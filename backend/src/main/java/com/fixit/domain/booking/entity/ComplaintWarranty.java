package com.fixit.domain.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "complaint_warranties")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintWarranty {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;

    @Column(name = "customer_reason", columnDefinition = "text")
    private String customerReason;

    @Column(name = "worker_response", columnDefinition = "text")
    private String workerResponse;

    @Column(name = "evidence_image_urls", columnDefinition = "text")
    private String evidenceImageUrls;

    @Column(name = "worker_evidence_image_urls", columnDefinition = "text")
    private String workerEvidenceImageUrls;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private ComplaintStatus status = ComplaintStatus.Pending;

    @Column(name = "deadline_to_respond")
    private OffsetDateTime deadlineToRespond;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
