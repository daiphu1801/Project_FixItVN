package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.feature.worker.wallet.domain.model.WalletBalance;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Inject;

public class GetWalletBalanceUseCase {
    private final WorkerWalletRepository repository;

    @Inject
    public GetWalletBalanceUseCase(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public WalletBalance execute() {
        return repository.getWalletBalance();
    }
}
