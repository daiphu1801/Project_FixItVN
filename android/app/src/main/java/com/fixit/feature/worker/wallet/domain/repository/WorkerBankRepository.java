package com.fixit.feature.worker.wallet.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;

import java.util.List;

public interface WorkerBankRepository {

    void getBankAccounts(ResultCallback<List<BankAccount>> callback);

    void addBankAccount(BankAccount account, ResultCallback<BankAccount> callback);

    void updateBankAccount(BankAccount account, ResultCallback<BankAccount> callback);

    void deleteBankAccount(String id, ResultCallback<Void> callback);

    void setDefaultBankAccount(String id, ResultCallback<BankAccount> callback);
}