package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Inject;

public class CreateDepositUseCase {
    private final WorkerWalletRepository repository;

    @Inject
    public CreateDepositUseCase(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public String execute(long amount, String note) {
        return repository.createDeposit(amount, note);
    }
}
