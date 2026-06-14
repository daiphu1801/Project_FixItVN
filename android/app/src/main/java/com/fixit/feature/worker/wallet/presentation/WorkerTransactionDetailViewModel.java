package com.fixit.feature.worker.wallet.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.wallet.domain.model.WalletTransaction;
import com.fixit.feature.worker.wallet.domain.usecase.CancelWithdrawalUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.GetWalletTransactionsUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerTransactionDetailViewModel extends BaseViewModel {

    private final GetWalletTransactionsUseCase getWalletTransactionsUseCase;
    private final CancelWithdrawalUseCase cancelWithdrawalUseCase;

    private final MutableLiveData<WalletTransaction> _transaction = new MutableLiveData<>();
    public LiveData<WalletTransaction> transaction = _transaction;

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>(false);
    public LiveData<Boolean> loading = _loading;

    private final MutableLiveData<String> _message = new MutableLiveData<>();
    public LiveData<String> message = _message;

    @Inject
    public WorkerTransactionDetailViewModel(
            GetWalletTransactionsUseCase getWalletTransactionsUseCase,
            CancelWithdrawalUseCase cancelWithdrawalUseCase
    ) {
        this.getWalletTransactionsUseCase = getWalletTransactionsUseCase;
        this.cancelWithdrawalUseCase = cancelWithdrawalUseCase;
    }

    public void loadTransaction(String txId) {
        _loading.setValue(true);
        // Lấy tất cả giao dịch (null = all) rồi tìm theo id
        getWalletTransactionsUseCase.execute(null, result -> {
            _loading.postValue(false);
            if (result.isSuccess()) {
                WalletTransaction found = findInList(result.getData(), txId);
                _transaction.postValue(found);
            } else if (result.getError() != null) {
                _message.postValue(result.getError().getMessage());
            }
        });
    }

    public void cancelWithdrawal(String txId) {
        cancelWithdrawalUseCase.execute(txId, result -> {
            if (result.isSuccess()) {
                _message.postValue("Đã hủy yêu cầu rút tiền");
                loadTransaction(txId);
            } else if (result.getError() != null) {
                _message.postValue(result.getError().getMessage());
            }
        });
    }

    private WalletTransaction findInList(List<WalletTransaction> list, String txId) {
        if (list == null || txId == null) return null;
        for (WalletTransaction t : list) {
            if (txId.equals(t.getId())) {
                return t;
            }
        }
        return null;
    }
}
