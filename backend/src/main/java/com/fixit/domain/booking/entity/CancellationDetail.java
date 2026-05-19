package com.fixit.domain.booking.entity;

import com.fixit.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cancellation_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancellationDetail {

    @Id
    @Column(name = "booking_id")
    private UUID bookingId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelled_by_id")
    private User cancelledBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancelled_by_role", length = 20)
    private CancelledByRole cancelledByRole;

    @Column(name = "reason_category", length = 50)
    private String reasonCategory;

    @Column(name = "cancellation_reason", columnDefinition = "text")
    private String cancellationReason;

    @Builder.Default
    @Column(name = "reputation_penalty_applied", precision = 3, scale = 1)
    private BigDecimal reputationPenaltyApplied = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "cancelled_at", updatable = false)
    private OffsetDateTime cancelledAt = OffsetDateTime.now();
}
