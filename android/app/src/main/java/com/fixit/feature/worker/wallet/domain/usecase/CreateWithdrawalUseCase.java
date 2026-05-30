package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Inject;

public class CreateWithdrawalUseCase {
    private final WorkerWalletRepository repository;

    @Inject
    public CreateWithdrawalUseCase(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public void execute(long amount, String bankAccountId) {
        repository.createWithdrawal(amount, bankAccountId);
    }
}
