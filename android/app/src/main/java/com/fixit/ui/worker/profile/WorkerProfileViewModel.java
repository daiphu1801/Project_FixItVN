package com.fixit.ui.worker.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.base.BaseViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerProfileViewModel extends BaseViewModel {

    private final MutableLiveData<Boolean> _logoutSuccess = new MutableLiveData<>();
    public LiveData<Boolean> logoutSuccess = _logoutSuccess;

    @Inject
    public WorkerProfileViewModel() {
        // Initialize repository or use cases here if needed
    }

    public void logout() {
        // Perform logout logic (clear tokens, etc.)
        // This is a mock implementation
        _logoutSuccess.setValue(true);
    }
}
