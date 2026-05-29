package com.fixit.feature.worker.wallet.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.wallet.domain.model.WalletTransaction;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerTransactionDetailViewModel extends BaseViewModel {

    private final WorkerWalletRepository repository;

    private final MutableLiveData<WalletTransaction> _transaction = new MutableLiveData<>();
    public LiveData<WalletTransaction> transaction = _transaction;

    @Inject
    public WorkerTransactionDetailViewModel(WorkerWalletRepository repository) {
        this.repository = repository;
    }

    public void loadTransaction(String txId) {
        // Tìm kiếm trong cả 3 loại ví
        WalletTransaction found = findInList(repository.getTransactions("available"), txId);
        if (found == null) {
            found = findInList(repository.getTransactions("held"), txId);
        }
        if (found == null) {
            found = findInList(repository.getTransactions("debt"), txId);
        }
        _transaction.setValue(found);
    }

    private WalletTransaction findInList(List<WalletTransaction> list, String txId) {
        if (list == null) return null;
        for (WalletTransaction t : list) {
            if (t.getId().equals(txId)) {
                return t;
            }
        }
        return null;
    }

    public void cancelWithdrawal(String txId) {
        repository.cancelWithdrawal(txId);
        // Reload details
        loadTransaction(txId);
    }
}
