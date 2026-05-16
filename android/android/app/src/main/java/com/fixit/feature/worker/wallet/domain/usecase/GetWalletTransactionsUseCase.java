package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.feature.worker.wallet.domain.model.WalletTransaction;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import java.util.List;

import javax.inject.Inject;

public class GetWalletTransactionsUseCase {
    private final WorkerWalletRepository repository;

    @Inject
    public GetWalletTransactionsUseCase(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public List<WalletTransaction> execute(String walletType) {
        return repository.getTransactions(walletType);
    }
}
