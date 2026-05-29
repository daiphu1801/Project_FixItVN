package com.fixit.feature.worker.wallet.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;
import com.fixit.feature.worker.wallet.domain.model.WalletBalance;
import com.fixit.feature.worker.wallet.domain.usecase.CreateWithdrawalUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.GetBankAccountsUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.GetWalletBalanceUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerWithdrawViewModel extends BaseViewModel {

    private final GetWalletBalanceUseCase getWalletBalanceUseCase;
    private final GetBankAccountsUseCase getBankAccountsUseCase;
    private final CreateWithdrawalUseCase createWithdrawalUseCase;

    private final MutableLiveData<String> _availableBalanceStr = new MutableLiveData<>();
    public LiveData<String> availableBalanceStr = _availableBalanceStr;

    private final MutableLiveData<Long> _availableAmount = new MutableLiveData<>(0L);
    public LiveData<Long> availableAmount = _availableAmount;

    private final MutableLiveData<BankAccount> _defaultBankAccount = new MutableLiveData<>();
    public LiveData<BankAccount> defaultBankAccount = _defaultBankAccount;

    private final MutableLiveData<String> _message = new MutableLiveData<>();
    public LiveData<String> message = _message;

    @Inject
    public WorkerWithdrawViewModel(
            GetWalletBalanceUseCase getWalletBalanceUseCase,
            GetBankAccountsUseCase getBankAccountsUseCase,
            CreateWithdrawalUseCase createWithdrawalUseCase
    ) {
        this.getWalletBalanceUseCase = getWalletBalanceUseCase;
        this.getBankAccountsUseCase = getBankAccountsUseCase;
        this.createWithdrawalUseCase = createWithdrawalUseCase;
        loadData();
    }

    public void loadData() {
        WalletBalance balance = getWalletBalanceUseCase.execute();
        _availableBalanceStr.setValue(balance.getAvailableBalance());

        try {
            String cleanBal = balance.getAvailableBalance().replace(" đ", "").replace(".", "").trim();
            _availableAmount.setValue(Long.parseLong(cleanBal));
        } catch (Exception ignored) {
            _availableAmount.setValue(0L);
        }

        getBankAccountsUseCase.execute(result -> {
            if (result.isSuccess()) {
                _defaultBankAccount.setValue(findDefaultBankAccount(result.getData()));
            } else {
                _defaultBankAccount.setValue(null);
                if (result.getError() != null) {
                    _message.setValue(result.getError().getMessage());
                }
            }
        });
    }

    public boolean submitWithdrawal(long amount) {
        BankAccount acc = _defaultBankAccount.getValue();
        Long available = _availableAmount.getValue();

        if (acc == null || available == null) {
            return false;
        }
        if (amount > available) {
            return false;
        }

        createWithdrawalUseCase.execute(amount, acc.getId());
        return true;
    }

    private BankAccount findDefaultBankAccount(List<BankAccount> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return null;
        }

        for (BankAccount acc : accounts) {
            if (acc.isDefault()) {
                return acc;
            }
        }
        return accounts.get(0);
    }
}