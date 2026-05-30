package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;
import com.fixit.feature.worker.wallet.domain.repository.WorkerBankRepository;

import javax.inject.Inject;

public class AddBankAccountUseCase {
    private final WorkerBankRepository repository;

    @Inject
    public AddBankAccountUseCase(WorkerBankRepository repository) {
        this.repository = repository;
    }

    public void execute(BankAccount account, ResultCallback<BankAccount> callback) {
        repository.addBankAccount(account, callback);
    }
}