package com.fixit.domain.wallet.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponse {

    private UUID transactionId;

    private UUID workerId;

    private BigDecimal amount;

    private String transactionCode;

    private String status;

    private String transactionType;

    private String targetBankName;

    private String targetAccountNumber;

    private String targetAccountName;

    private OffsetDateTime transactionTime;

    private String adminNote;
}
