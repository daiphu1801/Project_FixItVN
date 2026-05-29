package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.domain.repository.WorkerBankRepository;

import javax.inject.Inject;

public class DeleteBankAccountUseCase {
    private final WorkerBankRepository repository;

    @Inject
    public DeleteBankAccountUseCase(WorkerBankRepository repository) {
        this.repository = repository;
    }

    public void execute(String id, ResultCallback<Void> callback) {
        repository.deleteBankAccount(id, callback);
    }
}