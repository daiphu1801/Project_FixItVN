package com.fixit.feature.customer.order.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.fixit.core.ui.BaseViewModel;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CustomerOrderViewModel extends BaseViewModel {

    // 0: Trống, 1: Đang tìm thợ (Radar), 2: Đã có thợ (Chi tiết)
    private final MutableLiveData<Integer> _orderStatus = new MutableLiveData<>(0);
    public final LiveData<Integer> orderStatus = _orderStatus;

    @Inject
    public CustomerOrderViewModel() {
    }

    public void setStatus(int status) {
        _orderStatus.setValue(status);
    }

    public void cancelOrder() {
        _orderStatus.setValue(0);
    }

    public void startFinding() {
        _orderStatus.setValue(1);
    }

    public void onWorkerAccepted() {
        _orderStatus.setValue(2);
    }
}
