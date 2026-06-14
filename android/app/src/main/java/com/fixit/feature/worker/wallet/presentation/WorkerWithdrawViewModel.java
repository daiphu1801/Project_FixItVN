// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/presentation/WorkerWithdrawViewModel.java

package com.fixit.feature.worker.wallet.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;
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

    private final MutableLiveData<String> _availableBalanceStr = new MutableLiveData<>("...");
    public LiveData<String> availableBalanceStr = _availableBalanceStr;

    private final MutableLiveData<Long> _availableAmount = new MutableLiveData<>(0L);
    public LiveData<Long> availableAmount = _availableAmount;

    private final MutableLiveData<BankAccount> _defaultBankAccount = new MutableLiveData<>();
    public LiveData<BankAccount> defaultBankAccount = _defaultBankAccount;

    private final MutableLiveData<String> _message = new MutableLiveData<>();
    public LiveData<String> message = _message;

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>(false);
    public LiveData<Boolean> loading = _loading;

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
        _loading.setValue(true);

        // Load số dư bất đồng bộ
        getWalletBalanceUseCase.execute(result -> {
            if (result.isSuccess() && result.getData() != null) {
                String balStr = result.getData().getAvailableBalance();
                _availableBalanceStr.postValue(balStr);
                try {
                    String clean = balStr.replace(" ₫", "").replace(".", "").trim();
                    _availableAmount.postValue(Long.parseLong(clean));
                } catch (Exception ignored) {
                    _availableAmount.postValue(0L);
                }
            }
        });

        // Load danh sách tài khoản ngân hàng
        getBankAccountsUseCase.execute(result -> {
            _loading.postValue(false);
            if (result.isSuccess()) {
                _defaultBankAccount.postValue(findDefaultBankAccount(result.getData()));
            } else {
                _defaultBankAccount.postValue(null);
                if (result.getError() != null) {
                    _message.postValue(result.getError().getMessage());
                }
            }
        });
    }

    public void submitWithdrawal(long amount) {
        BankAccount acc = _defaultBankAccount.getValue();
        Long available = _availableAmount.getValue();

        if (acc == null || available == null || amount > available) {
            _message.setValue("Số tiền rút vượt quá số dư khả dụng");
            return;
        }

        createWithdrawalUseCase.execute(amount, acc.getId(), result -> {
            if (result.isSuccess()) {
                _message.postValue("Yêu cầu rút tiền đã được gửi");
            } else if (result.getError() != null) {
                _message.postValue(result.getError().getMessage());
            }
        });
    }

    private BankAccount findDefaultBankAccount(List<BankAccount> accounts) {
        if (accounts == null || accounts.isEmpty()) return null;
        for (BankAccount acc : accounts) {
            if (acc.isDefault()) return acc;
        }
        return accounts.get(0);
    }
}
