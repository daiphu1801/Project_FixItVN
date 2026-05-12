package com.fixit.ui.worker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * WorkerStatusViewModel — Activity-scoped Shared ViewModel
 *
 * Dùng chung giữa WorkerHomeFragment và WorkerJobFragment.
 * Vì được scope vào Activity, mọi Fragment trong cùng Activity
 * đều nhận cùng 1 instance → trạng thái isOnline luôn đồng bộ.
 *
 * Cách lấy từ Fragment:
 *   WorkerStatusViewModel statusVm =
 *       new ViewModelProvider(requireActivity()).get(WorkerStatusViewModel.class);
 */
@HiltViewModel
public class WorkerStatusViewModel extends ViewModel {

    private final MutableLiveData<Boolean> _isOnline = new MutableLiveData<>(false);
    public final LiveData<Boolean> isOnline = _isOnline;

    @Inject
    public WorkerStatusViewModel() { /* Hilt inject */ }

    /** Bật/tắt trạng thái nhận việc */
    public void toggleOnlineStatus() {
        Boolean current = _isOnline.getValue();
        _isOnline.setValue(current == null || !current);
    }

    /** Kiểm tra đang Online không */
    public boolean isCurrentlyOnline() {
        return Boolean.TRUE.equals(_isOnline.getValue());
    }
}
