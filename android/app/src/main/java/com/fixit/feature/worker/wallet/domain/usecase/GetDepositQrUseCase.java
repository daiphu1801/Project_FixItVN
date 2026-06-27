package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.data.remote.dto.response.DepositQrResponse;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Inject;

public class GetDepositQrUseCase {
    private final WorkerWalletRepository repository;

    @Inject
    public GetDepositQrUseCase(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public void execute(String transactionId, ResultCallback<DepositQrResponse> callback) {
        repository.getDepositQr(transactionId, callback);
    }
}

