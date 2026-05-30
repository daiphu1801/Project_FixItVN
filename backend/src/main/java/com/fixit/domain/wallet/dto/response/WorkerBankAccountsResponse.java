package com.fixit.domain.wallet.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerBankAccountsResponse {

    private UUID workerId;

    private UUID defaultBankAccountId;

    private List<WorkerBankAccountResponse> bankAccounts;
}