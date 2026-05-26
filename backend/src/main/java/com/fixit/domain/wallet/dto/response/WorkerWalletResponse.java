package com.fixit.domain.wallet.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerWalletResponse {

    private UUID workerId;

    private BigDecimal availableBalance;

    private BigDecimal heldBalance;

    private BigDecimal debtBalance;

    private BigDecimal totalBalance;

    private Boolean canWithdraw;
}