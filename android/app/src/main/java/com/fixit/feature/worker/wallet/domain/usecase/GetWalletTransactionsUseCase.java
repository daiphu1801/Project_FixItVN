// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/domain/usecase/GetWalletTransactionsUseCase.java

package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
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

    public void execute(String walletType, ResultCallback<List<WalletTransaction>> callback) {
        repository.getTransactions(walletType, callback);
    }
}
