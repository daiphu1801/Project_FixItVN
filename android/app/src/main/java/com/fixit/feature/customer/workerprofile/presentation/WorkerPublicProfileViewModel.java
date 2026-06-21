package com.fixit.feature.customer.workerprofile.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.workerprofile.domain.model.PublicWorkerProfile;
import com.fixit.feature.customer.workerprofile.domain.model.PublicWorkerSkill;
import com.fixit.feature.customer.workerprofile.domain.repository.PublicWorkerRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerPublicProfileViewModel extends ViewModel {

    private final PublicWorkerRepository repository;

    private final MutableLiveData<PublicWorkerProfile> _profile = new MutableLiveData<>();

    public LiveData<PublicWorkerProfile> getProfile() {
        return _profile;
    }

    private final MutableLiveData<List<PublicWorkerSkill>> _skills = new MutableLiveData<>();

    public LiveData<List<PublicWorkerSkill>> getSkills() {
        return _skills;
    }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);

    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }

    private final MutableLiveData<String> _error = new MutableLiveData<>();

    public LiveData<String> getError() {
        return _error;
    }

    @Inject
    public WorkerPublicProfileViewModel(PublicWorkerRepository repository) {
        this.repository = repository;
    }

    public void loadWorkerProfile(String workerId) {
        _isLoading.setValue(true);

        repository.getWorkerProfile(workerId, new ResultCallback<PublicWorkerProfile>() {
            @Override
            public void onResult(Result<PublicWorkerProfile> result) {
                if (result.isSuccess()) {
                    _profile.setValue(result.getData());
                    loadWorkerSkills(workerId); // Load skills after profile
                } else {
                    _isLoading.setValue(false);
                    _error.setValue(result.getError().getMessage());
                }
            }
        });
    }

    private void loadWorkerSkills(String workerId) {
        repository.getWorkerSkills(workerId, new ResultCallback<List<PublicWorkerSkill>>() {
            @Override
            public void onResult(Result<List<PublicWorkerSkill>> result) {
                _isLoading.setValue(false);
                if (result.isSuccess()) {
                    _skills.setValue(result.getData());
                } else {
                    _error.setValue(result.getError().getMessage());
                }
            }
        });
    }
}
