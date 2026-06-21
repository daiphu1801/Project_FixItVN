// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/presentation/WorkerWalletViewModel.java

package com.fixit.feature.worker.wallet.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.wallet.domain.model.WalletBalance;
import com.fixit.feature.worker.wallet.domain.model.WalletTransaction;
import com.fixit.feature.worker.wallet.domain.usecase.GetWalletBalanceUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.GetWalletTransactionsUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerWalletViewModel extends BaseViewModel {

    private final GetWalletBalanceUseCase getWalletBalanceUseCase;
    private final GetWalletTransactionsUseCase getWalletTransactionsUseCase;

    private final MutableLiveData<String> _availableBalance = new MutableLiveData<>("...");
    private final MutableLiveData<String> _heldBalance = new MutableLiveData<>("...");
    private final MutableLiveData<String> _debtBalance = new MutableLiveData<>("...");
    public LiveData<String> availableBalance = _availableBalance;
    public LiveData<String> heldBalance = _heldBalance;
    public LiveData<String> debtBalance = _debtBalance;

    private final MutableLiveData<String> _incomeThisWeek = new MutableLiveData<>("0 đ");
    private final MutableLiveData<String> _incomeThisMonth = new MutableLiveData<>("0 đ");
    public LiveData<String> incomeThisWeek = _incomeThisWeek;
    public LiveData<String> incomeThisMonth = _incomeThisMonth;

    private final MutableLiveData<String> _heldBookingId = new MutableLiveData<>();
    public LiveData<String> heldBookingId = _heldBookingId;

    private final MutableLiveData<List<WalletTransaction>> _filteredTx = new MutableLiveData<>();
    public LiveData<List<WalletTransaction>> filteredTransactions = _filteredTx;

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>(false);
    public LiveData<Boolean> loading = _loading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    @Inject
    public WorkerWalletViewModel(
            GetWalletBalanceUseCase getWalletBalanceUseCase,
            GetWalletTransactionsUseCase getWalletTransactionsUseCase
    ) {
        this.getWalletBalanceUseCase = getWalletBalanceUseCase;
        this.getWalletTransactionsUseCase = getWalletTransactionsUseCase;
        loadBalance();
        filterByWallet("available");
    }

    public void filterByWallet(String walletType) {
        _loading.setValue(true);
        getWalletTransactionsUseCase.execute(walletType, result -> {
            _loading.postValue(false);
            if (result.isSuccess()) {
                _filteredTx.postValue(result.getData());
            } else if (result.getError() != null) {
                _error.postValue(result.getError().getMessage());
            }
        });
    }

    public void refresh(String walletType) {
        loadBalance();
        filterByWallet(walletType);
    }

    private void loadBalance() {
        getWalletBalanceUseCase.execute(result -> {
            if (result.isSuccess()) {
                WalletBalance balance = result.getData();
                _availableBalance.postValue(balance.getAvailableBalance());
                _heldBalance.postValue(balance.getHeldBalance());
                _debtBalance.postValue(balance.getDebtBalance());
                _incomeThisWeek.postValue(balance.getIncomeThisWeek());
                _incomeThisMonth.postValue(balance.getIncomeThisMonth());
            } else if (result.getError() != null) {
                _error.postValue(result.getError().getMessage());
            }
        });

        getWalletTransactionsUseCase.execute("held", result -> {
            if (result.isSuccess()) {
                List<WalletTransaction> txs = result.getData();
                if (txs != null) {
                    for (WalletTransaction tx : txs) {
                        if (tx.getBookingId() != null && !tx.getBookingId().isEmpty()) {
                            _heldBookingId.postValue(tx.getBookingId());
                            return;
                        }
                    }
                }
                _heldBookingId.postValue(null);
            }
        });
    }
}
