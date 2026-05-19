package com.fixit.domain.wallet.entity;

import com.fixit.domain.worker.entity.Worker;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "worker_wallets")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerWallet {

    @Id
    @Column(name = "worker_id")
    private UUID workerId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @Builder.Default
    @Column(name = "available_balance", precision = 12, scale = 2)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "held_balance", precision = 12, scale = 2)
    private BigDecimal heldBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "debt_balance", precision = 12, scale = 2)
    private BigDecimal debtBalance = BigDecimal.ZERO;
}
