package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Inject;

public class CancelWithdrawalUseCase {
    private final WorkerWalletRepository repository;

    @Inject
    public CancelWithdrawalUseCase(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public void execute(String transactionId, ResultCallback<Void> callback) {
        repository.cancelWithdrawal(transactionId, callback);
    }
}

