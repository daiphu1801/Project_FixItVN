package com.fixit.domain.worker.entity;

import com.fixit.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "workers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Worker {

    @Id
    @Column(name = "worker_id")
    private UUID workerId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "worker_id")
    private User user;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "identity_card", length = 20, unique = true)
    private String identityCard;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", length = 50)
    private WorkerVerificationStatus verificationStatus = WorkerVerificationStatus.Unverified;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Builder.Default
    @Column(name = "is_available")
    private Boolean available = false;

    @Builder.Default
    @Column(name = "reputation_score", precision = 3, scale = 1)
    private BigDecimal reputationScore = BigDecimal.valueOf(5.0);

    @Builder.Default
    @Column(name = "missed_count")
    private Integer missedCount = 0;

    @Builder.Default
    @Column(name = "rejection_count")
    private Integer rejectionCount = 0;

    @Column(name = "rejected_priority_until")
    private OffsetDateTime rejectedPriorityUntil;

    @Column(name = "experience_description", columnDefinition = "text")
    private String experienceDescription;

    @Column(name = "service_area")
    private String serviceArea;
}
