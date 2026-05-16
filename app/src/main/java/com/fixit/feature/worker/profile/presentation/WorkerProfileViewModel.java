package com.fixit.feature.worker.profile.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.auth.domain.usecase.LogoutUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerProfileViewModel extends BaseViewModel {
    private final LogoutUseCase logoutUseCase;

    private final MutableLiveData<Boolean> _logoutSuccess = new MutableLiveData<>();
    public LiveData<Boolean> logoutSuccess = _logoutSuccess;

    @Inject
    public WorkerProfileViewModel(LogoutUseCase logoutUseCase) {
        this.logoutUseCase = logoutUseCase;
    }

    public void logout() {
        isLoading.setValue(true);
        logoutUseCase.execute(result -> {
            isLoading.setValue(false);
            if (result.isSuccess()) {
                _logoutSuccess.setValue(true);
            } else if (result.getError() != null) {
                errorMessage.setValue(result.getError().getMessage());
            }
        });
    }
}
