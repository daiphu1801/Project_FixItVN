package com.fixit.domain.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "proof_of_works")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProofOfWork {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "proof_type", length = 50)
    private ProofType proofType;

    @Builder.Default
    @Column(name = "captured_at", updatable = false)
    private OffsetDateTime capturedAt = OffsetDateTime.now();
}
