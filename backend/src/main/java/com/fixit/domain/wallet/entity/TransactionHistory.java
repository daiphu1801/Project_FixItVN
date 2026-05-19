package com.fixit.domain.wallet.entity;

import com.fixit.domain.booking.entity.Booking;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_histories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WorkerWallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 50)
    private TransactionType transactionType;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_code", unique = true, length = 50)
    private String transactionCode;

    @Column(name = "gateway_reference_code", length = 100)
    private String gatewayReferenceCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_bank_account_id")
    private WorkerBankAccount targetBankAccount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private TransactionStatus status = TransactionStatus.Pending;

    @Column(name = "admin_note", columnDefinition = "text")
    private String adminNote;

    @Column(name = "held_release_at")
    private OffsetDateTime heldReleaseAt;

    @Builder.Default
    @Column(name = "transaction_time")
    private OffsetDateTime transactionTime = OffsetDateTime.now();
}
