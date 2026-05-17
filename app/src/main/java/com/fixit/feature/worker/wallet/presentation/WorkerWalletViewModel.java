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

    private final MutableLiveData<String> _availableBalance = new MutableLiveData<>();
    private final MutableLiveData<String> _heldBalance = new MutableLiveData<>();
    private final MutableLiveData<String> _debtBalance = new MutableLiveData<>();

    public LiveData<String> availableBalance = _availableBalance;
    public LiveData<String> heldBalance = _heldBalance;
    public LiveData<String> debtBalance = _debtBalance;

    private final MutableLiveData<List<WalletTransaction>> _filteredTx = new MutableLiveData<>();
    public LiveData<List<WalletTransaction>> filteredTransactions = _filteredTx;

    @Inject
    public WorkerWalletViewModel(GetWalletBalanceUseCase getWalletBalanceUseCase,
                                 GetWalletTransactionsUseCase getWalletTransactionsUseCase) {
        this.getWalletBalanceUseCase = getWalletBalanceUseCase;
        this.getWalletTransactionsUseCase = getWalletTransactionsUseCase;
        loadBalance();
        filterByWallet("available");
    }

    public void filterByWallet(String walletType) {
        _filteredTx.setValue(getWalletTransactionsUseCase.execute(walletType));
    }

    private void loadBalance() {
        WalletBalance balance = getWalletBalanceUseCase.execute();
        _availableBalance.setValue(balance.getAvailableBalance());
        _heldBalance.setValue(balance.getHeldBalance());
        _debtBalance.setValue(balance.getDebtBalance());
    }
}
