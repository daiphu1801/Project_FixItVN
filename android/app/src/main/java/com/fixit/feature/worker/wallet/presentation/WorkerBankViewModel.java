package com.fixit.feature.worker.wallet.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.common.Result;
import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;
import com.fixit.feature.worker.wallet.domain.usecase.AddBankAccountUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.DeleteBankAccountUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.GetBankAccountsUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.SetDefaultBankAccountUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.UpdateBankAccountUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerBankViewModel extends BaseViewModel {

    private final GetBankAccountsUseCase getBankAccountsUseCase;
    private final AddBankAccountUseCase addBankAccountUseCase;
    private final UpdateBankAccountUseCase updateBankAccountUseCase;
    private final DeleteBankAccountUseCase deleteBankAccountUseCase;
    private final SetDefaultBankAccountUseCase setDefaultBankAccountUseCase;

    private final MutableLiveData<List<BankAccount>> _bankAccounts = new MutableLiveData<>();
    public LiveData<List<BankAccount>> bankAccounts = _bankAccounts;

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>(false);
    public LiveData<Boolean> loading = _loading;

    private final MutableLiveData<String> _message = new MutableLiveData<>();
    public LiveData<String> message = _message;

    private final MutableLiveData<Boolean> _saveSuccess = new MutableLiveData<>(false);
    public LiveData<Boolean> saveSuccess = _saveSuccess;

    @Inject
    public WorkerBankViewModel(
            GetBankAccountsUseCase getBankAccountsUseCase,
            AddBankAccountUseCase addBankAccountUseCase,
            UpdateBankAccountUseCase updateBankAccountUseCase,
            DeleteBankAccountUseCase deleteBankAccountUseCase,
            SetDefaultBankAccountUseCase setDefaultBankAccountUseCase
    ) {
        this.getBankAccountsUseCase = getBankAccountsUseCase;
        this.addBankAccountUseCase = addBankAccountUseCase;
        this.updateBankAccountUseCase = updateBankAccountUseCase;
        this.deleteBankAccountUseCase = deleteBankAccountUseCase;
        this.setDefaultBankAccountUseCase = setDefaultBankAccountUseCase;
    }

    public void loadBankAccounts() {
        _loading.setValue(true);
        getBankAccountsUseCase.execute(result -> {
            _loading.setValue(false);
            if (result.isSuccess()) {
                _bankAccounts.setValue(result.getData());
            } else {
                _message.setValue(errorMessage(result, "Không tải được danh sách tài khoản ngân hàng"));
            }
        });
    }

    public void addBank(String bankName, String accNo, String holder, boolean makeDefault) {
        _loading.setValue(true);
        _saveSuccess.setValue(false);

        BankAccount account = new BankAccount(null, bankName, accNo, holder.toUpperCase(), makeDefault);
        addBankAccountUseCase.execute(account, result -> {
            _loading.setValue(false);
            if (result.isSuccess()) {
                _message.setValue("Đã liên kết tài khoản thành công");
                _saveSuccess.setValue(true);
                loadBankAccounts();
            } else {
                _message.setValue(errorMessage(result, "Thêm tài khoản ngân hàng thất bại"));
            }
        });
    }

    public void updateBank(String id, String bankName, String accNo, String holder) {
        _loading.setValue(true);
        _saveSuccess.setValue(false);

        BankAccount account = new BankAccount(id, bankName, accNo, holder.toUpperCase(), false);
        updateBankAccountUseCase.execute(account, result -> {
            _loading.setValue(false);
            if (result.isSuccess()) {
                _message.setValue("Đã cập nhật tài khoản ngân hàng");
                _saveSuccess.setValue(true);
                loadBankAccounts();
            } else {
                _message.setValue(errorMessage(result, "Cập nhật tài khoản ngân hàng thất bại"));
            }
        });
    }

    public void deleteBank(String id) {
        _loading.setValue(true);
        deleteBankAccountUseCase.execute(id, result -> {
            _loading.setValue(false);
            if (result.isSuccess()) {
                _message.setValue("Đã hủy liên kết tài khoản ngân hàng");
                loadBankAccounts();
            } else {
                _message.setValue(errorMessage(result, "Xóa tài khoản ngân hàng thất bại"));
            }
        });
    }

    public void setDefaultBank(String id) {
        _loading.setValue(true);
        setDefaultBankAccountUseCase.execute(id, result -> {
            _loading.setValue(false);
            if (result.isSuccess()) {
                _message.setValue("Đã đặt tài khoản mặc định");
                loadBankAccounts();
            } else {
                _message.setValue(errorMessage(result, "Đặt tài khoản mặc định thất bại"));
            }
        });
    }

    public void clearSaveSuccess() {
        _saveSuccess.setValue(false);
    }

    private <T> String errorMessage(Result<T> result, String fallback) {
        if (result != null && result.getError() != null && result.getError().getMessage() != null) {
            return result.getError().getMessage();
        }
        return fallback;
    }
}