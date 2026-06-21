// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/domain/usecase/GetWalletBalanceUseCase.java

package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.domain.model.WalletBalance;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Inject;

public class GetWalletBalanceUseCase {
    private final WorkerWalletRepository repository;

    @Inject
    public GetWalletBalanceUseCase(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<WalletBalance> callback) {
        repository.getWalletBalance(callback);
    }
}
