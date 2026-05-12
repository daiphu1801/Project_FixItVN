package com.fixit.ui.worker.job;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.base.BaseViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerJobViewModel extends BaseViewModel {

    // ── Mock worker info ──────────────────────────────────────────────────────
    private final MutableLiveData<String> _workerName    = new MutableLiveData<>("Nguyễn Văn Phú");
    private final MutableLiveData<String> _serviceArea   = new MutableLiveData<>("Quận Cầu Giấy, Hà Nội");
    private final MutableLiveData<Integer> _todayOrders  = new MutableLiveData<>(5);
    private final MutableLiveData<Float>  _rating        = new MutableLiveData<>(4.8f);

    // ── Mock debt: > 0 để test cảnh báo, đặt 0 để test trạng thái bình thường
    // Mock debt = 0 để test trạng thái không nợ (đặt > 0 để test cảnh báo nợ)
    private final MutableLiveData<Double> _debtBalance   = new MutableLiveData<>(0.0);

    // ── Public LiveData (read-only cho Fragment) ──────────────────────────────
    public final LiveData<String>  workerName  = _workerName;
    public final LiveData<String>  serviceArea = _serviceArea;
    public final LiveData<Integer> todayOrders = _todayOrders;
    public final LiveData<Float>   rating      = _rating;
    public final LiveData<Double>  debtBalance = _debtBalance;

    @Inject
    public WorkerJobViewModel() { /* Hilt inject */ }

    /**
     * Kiểm tra xem thợ có đang nợ tiền không.
     */
    public boolean hasDebt() {
        Double debt = _debtBalance.getValue();
        return debt != null && debt > 0;
    }
}
