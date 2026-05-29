package com.fixit.domain.wallet.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositQrResponse {

    private UUID transactionId;

    private BigDecimal amount;

    private String transactionCode;

    private String bankName;

    private String bankCode;

    private String accountNumber;

    private String accountName;

    private String transferContent;

    private String qrUrl;
}