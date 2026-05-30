package com.fixit.domain.wallet.service;

import com.fixit.domain.wallet.dto.request.DepositCreateRequest;
import com.fixit.domain.wallet.dto.response.DepositQrResponse;
import com.fixit.domain.wallet.dto.response.DepositResponse;
import com.fixit.domain.wallet.dto.response.WorkerWalletResponse;
import com.fixit.domain.wallet.dto.response.WorkerWalletTransactionsResponse;

import java.util.UUID;

public interface WorkerWalletService {

    WorkerWalletResponse getMyWallet();

    WorkerWalletTransactionsResponse getMyTransactions(int page, int size, String type);

    DepositResponse createMyDeposit(DepositCreateRequest request);

    DepositResponse getMyDepositDetail(UUID transactionId);

    DepositQrResponse getMyDepositQr(UUID transactionId);
}