// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/domain/usecase/CreateDepositUseCase.java

package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.data.remote.dto.response.DepositResponse;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Inject;

public class CreateDepositUseCase {
    private final WorkerWalletRepository repository;

    @Inject
    public CreateDepositUseCase(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public void execute(long amount, ResultCallback<DepositResponse> callback) {
        repository.createDeposit(amount, callback);
    }
}
