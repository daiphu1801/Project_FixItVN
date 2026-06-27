package com.fixit.domain.worker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "worker_identity_cards")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerIdentityCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", unique = true)
    private Worker worker;

    @Column(name = "front_image_url", columnDefinition = "text")
    private String frontImageUrl;

    @Column(name = "back_image_url", columnDefinition = "text")
    private String backImageUrl;

    @Column(name = "selfie_image_url", columnDefinition = "text")
    private String selfieImageUrl;

    @Column(name = "ocr_full_name", length = 100)
    private String ocrFullName;

    @Column(name = "ocr_identity_card", length = 20)
    private String ocrIdentityCard;

    @Column(name = "similarity_score", precision = 5, scale = 2)
    private java.math.BigDecimal similarityScore;

    @Column(name = "vnpt_ekyc_hash")
    private String vnptEkycHash;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private WorkerIdentityStatus status = WorkerIdentityStatus.Pending;
}
