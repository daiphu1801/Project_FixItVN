// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/domain/usecase/CreateWithdrawalUseCase.java

package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Inject;

public class CreateWithdrawalUseCase {
    private final WorkerWalletRepository repository;

    @Inject
    public CreateWithdrawalUseCase(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public void execute(long amount, String bankAccountId, ResultCallback<Void> callback) {
        repository.createWithdrawal(amount, bankAccountId, callback);
    }
}
