package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.data.remote.dto.response.DepositResponse;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Inject;

public class GetDepositDetailUseCase {
    private final WorkerWalletRepository repository;

    @Inject
    public GetDepositDetailUseCase(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public void execute(String transactionId, ResultCallback<DepositResponse> callback) {
        repository.getDepositDetail(transactionId, callback);
    }
}
