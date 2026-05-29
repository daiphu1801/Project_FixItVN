package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;
import com.fixit.feature.worker.wallet.domain.repository.WorkerBankRepository;

import java.util.List;

import javax.inject.Inject;

public class GetBankAccountsUseCase {
    private final WorkerBankRepository repository;

    @Inject
    public GetBankAccountsUseCase(WorkerBankRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<List<BankAccount>> callback) {
        repository.getBankAccounts(callback);
    }
}