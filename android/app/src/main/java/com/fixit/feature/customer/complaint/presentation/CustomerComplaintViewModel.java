package com.fixit.feature.customer.complaint.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.common.Result;
import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.customer.complaint.domain.model.Complaint;
import com.fixit.feature.customer.complaint.domain.repository.ComplaintRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CustomerComplaintViewModel extends BaseViewModel {

    private final ComplaintRepository complaintRepository;

    private final MutableLiveData<Complaint> _complaint = new MutableLiveData<>();
    public LiveData<Complaint> complaint = _complaint;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<Result<Complaint>> _createResult = new MutableLiveData<>();
    public LiveData<Result<Complaint>> createResult = _createResult;

    private final MutableLiveData<Result<Void>> _cancelResult = new MutableLiveData<>();
    public LiveData<Result<Void>> cancelResult = _cancelResult;

    @Inject
    public CustomerComplaintViewModel(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public void loadBookingComplaint(String bookingId) {
        _isLoading.postValue(true);
        complaintRepository.getBookingComplaint(bookingId, result -> {
            _isLoading.postValue(false);
            if (result.isSuccess()) {
                _complaint.postValue(result.getData());
            } else {
                _complaint.postValue(null);
            }
        });
    }

    public void createComplaint(String bookingId, String reason, List<String> evidenceUrls) {
        _isLoading.postValue(true);
        _createResult.postValue(null); // Reset
        complaintRepository.createComplaint(bookingId, reason, evidenceUrls, result -> {
            _isLoading.postValue(false);
            _createResult.postValue(result);
            if (result.isSuccess()) {
                _complaint.postValue(result.getData());
            }
        });
    }

    public void cancelComplaint(String bookingId, String complaintId) {
        _isLoading.postValue(true);
        _cancelResult.postValue(null); // Reset
        complaintRepository.cancelComplaint(bookingId, complaintId, result -> {
            _isLoading.postValue(false);
            _cancelResult.postValue(result);
            if (result.isSuccess()) {
                _complaint.postValue(null); // Khiếu nại đã bị hủy/xóa
            }
        });
    }
}
