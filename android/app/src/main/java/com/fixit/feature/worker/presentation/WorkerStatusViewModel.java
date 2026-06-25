package com.fixit.feature.worker.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.feature.worker.availability.domain.usecase.GetWorkerAvailabilityUseCase;
import com.fixit.feature.worker.availability.domain.usecase.ToggleWorkerAvailabilityUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerStatusViewModel extends ViewModel {
    private final GetWorkerAvailabilityUseCase getWorkerAvailabilityUseCase;
    private final ToggleWorkerAvailabilityUseCase toggleWorkerAvailabilityUseCase;

    private final MutableLiveData<Boolean> _isOnline = new MutableLiveData<>(false);
    public final LiveData<Boolean> isOnline = _isOnline;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;

    @Inject
    public WorkerStatusViewModel(GetWorkerAvailabilityUseCase getWorkerAvailabilityUseCase,
                                 ToggleWorkerAvailabilityUseCase toggleWorkerAvailabilityUseCase) {
        this.getWorkerAvailabilityUseCase = getWorkerAvailabilityUseCase;
        this.toggleWorkerAvailabilityUseCase = toggleWorkerAvailabilityUseCase;
        loadOnlineStatus();
    }

    public void loadOnlineStatus() {
        getWorkerAvailabilityUseCase.execute(result -> {
            if (result.isSuccess() && result.getData() != null) {
                _isOnline.postValue(result.getData());
            } else if (!result.isSuccess()) {
                _errorMessage.postValue(result.getError().getMessage());
            }
        });
    }

    public void toggleOnlineStatus() {
        Boolean current = _isOnline.getValue();
        boolean target = current == null || !current;
        toggleWorkerAvailabilityUseCase.execute(target, result -> {
            if (result.isSuccess() && result.getData() != null) {
                _isOnline.postValue(result.getData());
            } else if (!result.isSuccess()) {
                _errorMessage.postValue(result.getError().getMessage());
            }
        });
    }

    public void clearError() {
        _errorMessage.postValue(null);
    }

    public boolean isCurrentlyOnline() {
        Boolean current = _isOnline.getValue();
        return current != null && current;
    }
}
