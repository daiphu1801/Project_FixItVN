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

    @Inject
    public WorkerStatusViewModel(GetWorkerAvailabilityUseCase getWorkerAvailabilityUseCase,
                                 ToggleWorkerAvailabilityUseCase toggleWorkerAvailabilityUseCase) {
        this.getWorkerAvailabilityUseCase = getWorkerAvailabilityUseCase;
        this.toggleWorkerAvailabilityUseCase = toggleWorkerAvailabilityUseCase;
        _isOnline.setValue(getWorkerAvailabilityUseCase.execute());
    }

    public void toggleOnlineStatus() {
        _isOnline.setValue(toggleWorkerAvailabilityUseCase.execute());
    }

    public boolean isCurrentlyOnline() {
        Boolean current = _isOnline.getValue();
        return current != null ? current : getWorkerAvailabilityUseCase.execute();
    }
}
