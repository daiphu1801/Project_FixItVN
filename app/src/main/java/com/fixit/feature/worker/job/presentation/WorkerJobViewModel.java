package com.fixit.feature.worker.job.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.job.domain.model.WorkerJobSummary;
import com.fixit.feature.worker.job.domain.usecase.GetWorkerJobSummaryUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerJobViewModel extends BaseViewModel {
    private final GetWorkerJobSummaryUseCase getWorkerJobSummaryUseCase;

    private final MutableLiveData<String> _workerName = new MutableLiveData<>();
    private final MutableLiveData<String> _serviceArea = new MutableLiveData<>();
    private final MutableLiveData<Integer> _todayOrders = new MutableLiveData<>();
    private final MutableLiveData<Float> _rating = new MutableLiveData<>();
    private final MutableLiveData<Double> _debtBalance = new MutableLiveData<>();

    public final LiveData<String> workerName = _workerName;
    public final LiveData<String> serviceArea = _serviceArea;
    public final LiveData<Integer> todayOrders = _todayOrders;
    public final LiveData<Float> rating = _rating;
    public final LiveData<Double> debtBalance = _debtBalance;

    @Inject
    public WorkerJobViewModel(GetWorkerJobSummaryUseCase getWorkerJobSummaryUseCase) {
        this.getWorkerJobSummaryUseCase = getWorkerJobSummaryUseCase;
        loadSummary();
    }

    public boolean hasDebt() {
        Double debt = _debtBalance.getValue();
        return debt != null && debt > 0;
    }

    private void loadSummary() {
        WorkerJobSummary summary = getWorkerJobSummaryUseCase.execute();
        _workerName.setValue(summary.getWorkerName());
        _serviceArea.setValue(summary.getServiceArea());
        _todayOrders.setValue(summary.getTodayOrders());
        _rating.setValue(summary.getRating());
        _debtBalance.setValue(summary.getDebtBalance());
    }
}
