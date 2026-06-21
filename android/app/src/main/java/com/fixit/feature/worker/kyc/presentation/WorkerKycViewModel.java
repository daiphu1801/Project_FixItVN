package com.fixit.feature.worker.kyc.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.common.ResultCallback;
import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.kyc.data.remote.dto.response.VnptKycConfigResponse;
import com.fixit.feature.worker.kyc.domain.model.WorkerKyc;
import com.fixit.feature.worker.kyc.domain.repository.WorkerKycRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerKycViewModel extends BaseViewModel {

    private final WorkerKycRepository workerKycRepository;

    private final MutableLiveData<VnptKycConfigResponse> _kycConfig = new MutableLiveData<>();
    public LiveData<VnptKycConfigResponse> kycConfig = _kycConfig;

    private final MutableLiveData<WorkerKyc> _kycStatus = new MutableLiveData<>();
    public LiveData<WorkerKyc> kycStatus = _kycStatus;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    @Inject
    public WorkerKycViewModel(WorkerKycRepository workerKycRepository) {
        this.workerKycRepository = workerKycRepository;
    }

    public void loadKycConfig() {
        _isLoading.postValue(true);
        _error.postValue(null);
        workerKycRepository.getKycConfig(result -> {
            _isLoading.postValue(false);
            if (result.isSuccess()) {
                _kycConfig.postValue(result.getData());
            } else {
                _error.postValue(result.getError() != null ? result.getError().getMessage() : "Không thể lấy cấu hình eKYC");
            }
        });
    }

    public void loadKycStatus() {
        _isLoading.postValue(true);
        _error.postValue(null);
        workerKycRepository.getKycStatus(result -> {
            _isLoading.postValue(false);
            if (result.isSuccess()) {
                _kycStatus.postValue(result.getData());
            } else {
                _error.postValue(result.getError() != null ? result.getError().getMessage() : "Không thể lấy trạng thái KYC");
            }
        });
    }
}
