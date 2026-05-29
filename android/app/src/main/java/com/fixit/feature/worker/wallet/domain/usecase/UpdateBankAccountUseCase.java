package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;
import com.fixit.feature.worker.wallet.domain.repository.WorkerBankRepository;

import javax.inject.Inject;

public class UpdateBankAccountUseCase {
    private final WorkerBankRepository repository;

    @Inject
    public UpdateBankAccountUseCase(WorkerBankRepository repository) {
        this.repository = repository;
    }

    public void execute(BankAccount account, ResultCallback<BankAccount> callback) {
        repository.updateBankAccount(account, callback);
    }
}