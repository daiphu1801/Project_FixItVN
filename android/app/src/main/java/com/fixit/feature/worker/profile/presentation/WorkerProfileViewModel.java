package com.fixit.feature.worker.profile.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.auth.domain.usecase.LogoutUseCase;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;
import com.fixit.feature.worker.profile.domain.model.WorkerProfileUpdateInput;
import com.fixit.feature.worker.profile.domain.model.WorkerSkill;
import com.fixit.feature.worker.profile.domain.model.ServiceCategory;
import com.fixit.feature.worker.profile.domain.usecase.GetWorkerProfileUseCase;
import com.fixit.feature.worker.profile.domain.usecase.GetWorkerSkillsUseCase;
import com.fixit.feature.worker.profile.domain.usecase.UpdateWorkerProfileUseCase;
import com.fixit.feature.worker.profile.domain.usecase.UpdateWorkerSkillsUseCase;
import com.fixit.feature.worker.profile.domain.usecase.GetServiceCategoriesUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.GetWalletBalanceUseCase;

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
    private final GetServiceCategoriesUseCase getServiceCategoriesUseCase;
    private final GetWalletBalanceUseCase getWalletBalanceUseCase;

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

    private final MutableLiveData<List<ServiceCategory>> _categories = new MutableLiveData<>();
    public LiveData<List<ServiceCategory>> categories = _categories;

    private final MutableLiveData<String> _walletBalance = new MutableLiveData<>("0 đ");
    public LiveData<String> walletBalance = _walletBalance;

    @Inject
    public WorkerProfileViewModel(
            LogoutUseCase logoutUseCase,
            GetWorkerProfileUseCase getWorkerProfileUseCase,
            UpdateWorkerProfileUseCase updateWorkerProfileUseCase,
            GetWorkerSkillsUseCase getWorkerSkillsUseCase,
            UpdateWorkerSkillsUseCase updateWorkerSkillsUseCase,
            GetServiceCategoriesUseCase getServiceCategoriesUseCase,
            GetWalletBalanceUseCase getWalletBalanceUseCase
    ) {
        this.logoutUseCase = logoutUseCase;
        this.getWorkerProfileUseCase = getWorkerProfileUseCase;
        this.updateWorkerProfileUseCase = updateWorkerProfileUseCase;
        this.getWorkerSkillsUseCase = getWorkerSkillsUseCase;
        this.updateWorkerSkillsUseCase = updateWorkerSkillsUseCase;
        this.getServiceCategoriesUseCase = getServiceCategoriesUseCase;
        this.getWalletBalanceUseCase = getWalletBalanceUseCase;
    }

    public void loadWalletBalance() {
        getWalletBalanceUseCase.execute(result -> {
            if (result.isSuccess() && result.getData() != null) {
                _walletBalance.postValue(result.getData().getAvailableBalance());
            }
        });
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

    public void loadServiceCategories() {
        setLoading(true);

        getServiceCategoriesUseCase.execute(result -> {
            setLoading(false);

            if (result.isSuccess()) {
                _categories.postValue(result.getData());
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