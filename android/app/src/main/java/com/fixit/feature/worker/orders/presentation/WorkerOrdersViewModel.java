package com.fixit.feature.worker.orders.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
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

    private final MutableLiveData<String> _statusUpdateSuccess = new MutableLiveData<>();
    public LiveData<String> statusUpdateSuccess = _statusUpdateSuccess;

    private final MutableLiveData<List<ExtraCostItem>> _extraItems = new MutableLiveData<>();
    public LiveData<List<ExtraCostItem>> extraItems = _extraItems;

    private final MutableLiveData<List<WorkerOrder>> _filteredOrders = new MutableLiveData<>();
    public LiveData<List<WorkerOrder>> filteredOrders = _filteredOrders;

    private final MutableLiveData<WorkerOrder> _orderDetails = new MutableLiveData<>();
    public LiveData<WorkerOrder> orderDetails = _orderDetails;

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

    public void loadOrderDetails(String orderId) {
        loadOrderDetails(orderId, true);
    }

    public void loadOrderDetails(String orderId, boolean showLoading) {
        _orderDetails.setValue(null);
        _currentStatus.setValue(null);
        if (showLoading) {
            setLoading(true);
        }
        getWorkerOrderByIdUseCase.execute(orderId, new ResultCallback<WorkerOrder>() {
            @Override
            public void onResult(Result<WorkerOrder> result) {
                if (showLoading) {
                    setLoading(false);
                }
                if (result.isSuccess()) {
                    WorkerOrder order = result.getData();
                    _orderDetails.postValue(order);
                    if (order != null && order.getJobStatus() != null) {
                        _currentStatus.postValue(order.getJobStatus());
                    } else if (order != null) {
                        initializeStatus(order.getStatus());
                    }
                } else {
                    setError(result.getError() != null ? result.getError().getMessage() : "Không tìm thấy đơn hàng");
                    _orderDetails.postValue(null);
                }
            }
        });
    }

    public String getCurrentFilterStatus() {
        return currentFilterStatus;
    }

    public void filterByStatus(String status) {
        filterByStatus(status, true);
    }

    public void filterByStatus(String status, boolean showLoading) {
        currentFilterStatus = status;
        if (showLoading) {
            setLoading(true);
        }
        filterWorkerOrdersUseCase.execute(status, new ResultCallback<List<WorkerOrder>>() {
            @Override
            public void onResult(Result<List<WorkerOrder>> result) {
                if (showLoading) {
                    setLoading(false);
                }
                if (result.isSuccess()) {
                    _filteredOrders.postValue(result.getData());
                } else {
                    setError(result.getError() != null ? result.getError().getMessage() : "Lỗi khi lọc đơn hàng");
                    _filteredOrders.postValue(null);
                }
            }
        });
    }

    public void clearStatusUpdateSuccess() {
        _statusUpdateSuccess.setValue(null);
    }

    public void advanceStatus(String orderId) {
        setLoading(true);
        advanceJobStatusUseCase.execute(orderId, _currentStatus.getValue(), new ResultCallback<JobStatus>() {
            @Override
            public void onResult(Result<JobStatus> result) {
                setLoading(false);
                if (result.isSuccess()) {
                    _currentStatus.postValue(result.getData());
                    _statusUpdateSuccess.postValue("Cập nhật trạng thái thành công!");
                } else {
                    setError(result.getError() != null ? result.getError().getMessage() : "Không thể cập nhật tiến trình");
                }
            }
        });
    }
}
