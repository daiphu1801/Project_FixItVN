package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Inject;

public class GetDepositQrUseCase {
    private final WorkerWalletRepository repository;

    @Inject
    public GetDepositQrUseCase(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public String execute(String transactionId) {
        return repository.getDepositQr(transactionId);
    }
}
