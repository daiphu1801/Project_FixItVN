package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;
import com.fixit.feature.worker.wallet.domain.repository.WorkerBankRepository;

import javax.inject.Inject;

public class SetDefaultBankAccountUseCase {
    private final WorkerBankRepository repository;

    @Inject
    public SetDefaultBankAccountUseCase(WorkerBankRepository repository) {
        this.repository = repository;
    }

    public void execute(String id, ResultCallback<BankAccount> callback) {
        repository.setDefaultBankAccount(id, callback);
    }
}