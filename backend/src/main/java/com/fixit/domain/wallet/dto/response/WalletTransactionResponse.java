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
public class WalletTransactionResponse {

    private UUID transactionId;

    private UUID bookingId;

    private String transactionType;

    private BigDecimal amount;

    private String transactionCode;

    private String gatewayReferenceCode;

    private UUID targetBankAccountId;

    private String targetBankName;

    private String targetAccountNumberMasked;

    private String status;

    private String adminNote;

    private OffsetDateTime heldReleaseAt;

    private OffsetDateTime transactionTime;
}