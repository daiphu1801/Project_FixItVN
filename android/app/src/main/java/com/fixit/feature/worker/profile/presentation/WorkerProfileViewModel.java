package com.fixit.feature.worker.profile.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.auth.domain.usecase.LogoutUseCase;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;
import com.fixit.feature.worker.profile.domain.model.WorkerProfileUpdateInput;
import com.fixit.feature.worker.profile.domain.model.WorkerSkill;
import com.fixit.feature.worker.profile.domain.usecase.GetWorkerProfileUseCase;
import com.fixit.feature.worker.profile.domain.usecase.GetWorkerSkillsUseCase;
import com.fixit.feature.worker.profile.domain.usecase.UpdateWorkerProfileUseCase;
import com.fixit.feature.worker.profile.domain.usecase.UpdateWorkerSkillsUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerProfileViewModel extends BaseViewModel {

    private final LogoutUseCase logoutUseCase;
    private final GetWorkerProfileUseCase getWorkerProfileUseCase;
    private final UpdateWorkerProfileUseCase updateWorkerProfileUseCase;
    private final GetWorkerSkillsUseCase getWorkerSkillsUseCase;
    private final UpdateWorkerSkillsUseCase updateWorkerSkillsUseCase;

    private final MutableLiveData<Boolean> _logoutSuccess = new MutableLiveData<>();
    public LiveData<Boolean> logoutSuccess = _logoutSuccess;

    private final MutableLiveData<WorkerProfile> _profile = new MutableLiveData<>();
    public LiveData<WorkerProfile> profile = _profile;

    private final MutableLiveData<Boolean> _profileUpdated = new MutableLiveData<>();
    public LiveData<Boolean> profileUpdated = _profileUpdated;

    private final MutableLiveData<List<WorkerSkill>> _skills = new MutableLiveData<>();
    public LiveData<List<WorkerSkill>> skills = _skills;

    private final MutableLiveData<Boolean> _skillsUpdated = new MutableLiveData<>();
    public LiveData<Boolean> skillsUpdated = _skillsUpdated;

    @Inject
    public WorkerProfileViewModel(
            LogoutUseCase logoutUseCase,
            GetWorkerProfileUseCase getWorkerProfileUseCase,
            UpdateWorkerProfileUseCase updateWorkerProfileUseCase,
            GetWorkerSkillsUseCase getWorkerSkillsUseCase,
            UpdateWorkerSkillsUseCase updateWorkerSkillsUseCase
    ) {
        this.logoutUseCase = logoutUseCase;
        this.getWorkerProfileUseCase = getWorkerProfileUseCase;
        this.updateWorkerProfileUseCase = updateWorkerProfileUseCase;
        this.getWorkerSkillsUseCase = getWorkerSkillsUseCase;
        this.updateWorkerSkillsUseCase = updateWorkerSkillsUseCase;
    }

    public void loadProfile() {
        setLoading(true);

        getWorkerProfileUseCase.execute(result -> {
            setLoading(false);

            if (result.isSuccess()) {
                _profile.postValue(result.getData());
            } else if (result.getError() != null) {
                setError(result.getError().getMessage());
            }
        });
    }

    public void updateProfile(WorkerProfileUpdateInput input) {
        setLoading(true);

        updateWorkerProfileUseCase.execute(input, result -> {
            setLoading(false);

            if (result.isSuccess()) {
                _profile.postValue(result.getData());
                _profileUpdated.postValue(true);
            } else if (result.getError() != null) {
                setError(result.getError().getMessage());
            }
        });
    }

    public void loadSkills() {
        setLoading(true);

        getWorkerSkillsUseCase.execute(result -> {
            setLoading(false);

            if (result.isSuccess()) {
                _skills.postValue(result.getData());
            } else if (result.getError() != null) {
                setError(result.getError().getMessage());
            }
        });
    }

    public void updateSkills(List<WorkerSkill> skills) {
        setLoading(true);

        updateWorkerSkillsUseCase.execute(skills, result -> {
            setLoading(false);

            if (result.isSuccess()) {
                _skills.postValue(result.getData());
                _skillsUpdated.postValue(true);
            } else if (result.getError() != null) {
                setError(result.getError().getMessage());
            }
        });
    }

    public void logout() {
        setLoading(true);

        logoutUseCase.execute(result -> {
            setLoading(false);

            if (result.isSuccess()) {
                _logoutSuccess.postValue(true);
            } else if (result.getError() != null) {
                setError(result.getError().getMessage());
            }
        });
    }
}