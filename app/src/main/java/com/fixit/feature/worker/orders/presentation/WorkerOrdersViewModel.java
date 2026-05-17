package com.fixit.feature.worker.orders.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.orders.domain.model.ExtraCostItem;
import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.feature.worker.orders.domain.usecase.AdvanceJobStatusUseCase;
import com.fixit.feature.worker.orders.domain.usecase.CalculateTotalExtraUseCase;
import com.fixit.feature.worker.orders.domain.usecase.FilterWorkerOrdersUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GenerateWorkerPaymentQrUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GetExtraCostsUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GetInitialJobStatusUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GetWorkerOrderByIdUseCase;
import com.fixit.feature.worker.orders.domain.usecase.SaveExtraCostsUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerOrdersViewModel extends BaseViewModel {
    public static final double COMMISSION_RATE = 0.15;

    private final FilterWorkerOrdersUseCase filterWorkerOrdersUseCase;
    private final GetWorkerOrderByIdUseCase getWorkerOrderByIdUseCase;
    private final GetInitialJobStatusUseCase getInitialJobStatusUseCase;
    private final AdvanceJobStatusUseCase advanceJobStatusUseCase;
    private final SaveExtraCostsUseCase saveExtraCostsUseCase;
    private final GetExtraCostsUseCase getExtraCostsUseCase;
    private final CalculateTotalExtraUseCase calculateTotalExtraUseCase;
    private final GenerateWorkerPaymentQrUseCase generateWorkerPaymentQrUseCase;

    private final MutableLiveData<JobStatus> _currentStatus = new MutableLiveData<>(JobStatus.ACCEPTED);
    public LiveData<JobStatus> currentStatus = _currentStatus;

    private final MutableLiveData<List<ExtraCostItem>> _extraItems = new MutableLiveData<>();
    public LiveData<List<ExtraCostItem>> extraItems = _extraItems;

    private final MutableLiveData<List<WorkerOrder>> _filteredOrders = new MutableLiveData<>();
    public LiveData<List<WorkerOrder>> filteredOrders = _filteredOrders;

    private String currentFilterStatus = "pending";

    @Inject
    public WorkerOrdersViewModel(
            FilterWorkerOrdersUseCase filterWorkerOrdersUseCase,
            GetWorkerOrderByIdUseCase getWorkerOrderByIdUseCase,
            GetInitialJobStatusUseCase getInitialJobStatusUseCase,
            AdvanceJobStatusUseCase advanceJobStatusUseCase,
            SaveExtraCostsUseCase saveExtraCostsUseCase,
            GetExtraCostsUseCase getExtraCostsUseCase,
            CalculateTotalExtraUseCase calculateTotalExtraUseCase,
            GenerateWorkerPaymentQrUseCase generateWorkerPaymentQrUseCase) {
        this.filterWorkerOrdersUseCase = filterWorkerOrdersUseCase;
        this.getWorkerOrderByIdUseCase = getWorkerOrderByIdUseCase;
        this.getInitialJobStatusUseCase = getInitialJobStatusUseCase;
        this.advanceJobStatusUseCase = advanceJobStatusUseCase;
        this.saveExtraCostsUseCase = saveExtraCostsUseCase;
        this.getExtraCostsUseCase = getExtraCostsUseCase;
        this.calculateTotalExtraUseCase = calculateTotalExtraUseCase;
        this.generateWorkerPaymentQrUseCase = generateWorkerPaymentQrUseCase;

        _extraItems.setValue(getExtraCostsUseCase.execute());
        filterByStatus(currentFilterStatus);
    }

    public void setExtraItems(List<ExtraCostItem> items) {
        saveExtraCostsUseCase.execute(items);
        _extraItems.setValue(getExtraCostsUseCase.execute());
    }

    public long calculateTotalExtra() {
        return calculateTotalExtraUseCase.execute();
    }

    public String generateVietQrUrl(String orderId, long amount) {
        return generateWorkerPaymentQrUseCase.execute(orderId, amount);
    }

    public void initializeStatus(String orderStatus) {
        _currentStatus.setValue(getInitialJobStatusUseCase.execute(orderStatus));
    }

    public WorkerOrder getOrderById(String orderId) {
        return getWorkerOrderByIdUseCase.execute(orderId);
    }

    public String getCurrentFilterStatus() {
        return currentFilterStatus;
    }

    public void filterByStatus(String status) {
        currentFilterStatus = status;
        _filteredOrders.setValue(filterWorkerOrdersUseCase.execute(status));
    }

    public void advanceStatus() {
        JobStatus nextStatus = advanceJobStatusUseCase.execute(_currentStatus.getValue());
        if (nextStatus != null) {
            _currentStatus.setValue(nextStatus);
        }
    }
}
