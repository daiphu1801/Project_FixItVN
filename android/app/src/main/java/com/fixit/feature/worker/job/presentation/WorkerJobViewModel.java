package com.fixit.feature.worker.job.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.job.domain.model.WorkerJobSummary;
import com.fixit.feature.worker.job.domain.usecase.GetWorkerJobSummaryUseCase;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;
import com.fixit.feature.worker.profile.domain.usecase.GetWorkerProfileUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerJobViewModel extends BaseViewModel {
    private final GetWorkerJobSummaryUseCase getWorkerJobSummaryUseCase;
    private final GetWorkerProfileUseCase getWorkerProfileUseCase;

    private final MutableLiveData<String> _workerName = new MutableLiveData<>();
    private final MutableLiveData<String> _serviceArea = new MutableLiveData<>();
    private final MutableLiveData<Integer> _todayOrders = new MutableLiveData<>();
    private final MutableLiveData<Float> _rating = new MutableLiveData<>();
    private final MutableLiveData<Double> _debtBalance = new MutableLiveData<>();
    private final MutableLiveData<Double> _latitude = new MutableLiveData<>();
    private final MutableLiveData<Double> _longitude = new MutableLiveData<>();

    public final LiveData<String> workerName = _workerName;
    public final LiveData<String> serviceArea = _serviceArea;
    public final LiveData<Integer> todayOrders = _todayOrders;
    public final LiveData<Float> rating = _rating;
    public final LiveData<Double> debtBalance = _debtBalance;
    public final LiveData<Double> latitude = _latitude;
    public final LiveData<Double> longitude = _longitude;

    @Inject
    public WorkerJobViewModel(
            GetWorkerJobSummaryUseCase getWorkerJobSummaryUseCase,
            GetWorkerProfileUseCase getWorkerProfileUseCase
    ) {
        this.getWorkerJobSummaryUseCase = getWorkerJobSummaryUseCase;
        this.getWorkerProfileUseCase = getWorkerProfileUseCase;
        loadSummary();
        loadProfile();
    }

    public boolean hasDebt() {
        Double debt = _debtBalance.getValue();
        return debt != null && debt > 0;
    }

    public void loadSummary() {
        getWorkerJobSummaryUseCase.execute(result -> {
            if (result.isSuccess() && result.getData() != null) {
                WorkerJobSummary summary = result.getData();
                _workerName.postValue(summary.getWorkerName());
                _serviceArea.postValue(summary.getServiceArea());
                _todayOrders.postValue(summary.getTodayOrders());
                _rating.postValue(summary.getRating());
                _debtBalance.postValue(summary.getDebtBalance());
            }
        });
    }

    public void loadProfile() {
        getWorkerProfileUseCase.execute(result -> {
            if (result.isSuccess() && result.getData() != null) {
                WorkerProfile profile = result.getData();
                _workerName.postValue(profile.getFullName());
                _serviceArea.postValue(profile.getServiceArea());
                _latitude.postValue(profile.getLatitude());
                _longitude.postValue(profile.getLongitude());
            }
        });
    }
}
