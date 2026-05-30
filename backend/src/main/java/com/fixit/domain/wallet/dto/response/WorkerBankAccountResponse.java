package com.fixit.domain.wallet.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerBankAccountResponse {

    private UUID bankAccountId;

    private String bankName;

    private String accountName;

    private String accountNumberMasked;

    private Boolean defaultAccount;
}