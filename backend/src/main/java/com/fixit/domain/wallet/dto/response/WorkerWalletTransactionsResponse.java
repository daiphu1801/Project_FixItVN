package com.fixit.domain.wallet.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerWalletTransactionsResponse {

    private UUID workerId;

    private String type;

    private int page;

    private int size;

    private long totalElements;

    private int totalPages;

    private boolean hasNext;

    private List<WalletTransactionResponse> transactions;
}