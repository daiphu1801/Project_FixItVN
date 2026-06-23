package com.fixit.feature.worker.profile.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.auth.domain.usecase.ChangePasswordUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChangePasswordViewModel extends BaseViewModel {
    private final ChangePasswordUseCase changePasswordUseCase;

    private final MutableLiveData<Boolean> _changePasswordSuccess = new MutableLiveData<>();
    public final LiveData<Boolean> changePasswordSuccess = _changePasswordSuccess;

    @Inject
    public ChangePasswordViewModel(ChangePasswordUseCase changePasswordUseCase) {
        this.changePasswordUseCase = changePasswordUseCase;
    }

    public void changePassword(String oldPassword, String newPassword) {
        setLoading(true);
        changePasswordUseCase.execute(oldPassword, newPassword, result -> {
            setLoading(false);
            if (result.isSuccess()) {
                _changePasswordSuccess.postValue(true);
            } else if (result.getError() != null) {
                setError(result.getError().getMessage());
            }
        });
    }
}
