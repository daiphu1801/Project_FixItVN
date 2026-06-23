package com.fixit.feature.worker.home.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.home.domain.model.Appointment;
import com.fixit.feature.worker.home.domain.model.WorkerHome;
import com.fixit.feature.worker.home.domain.usecase.GetWorkerHomeUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerHomeViewModel extends BaseViewModel {

    private final GetWorkerHomeUseCase getWorkerHomeUseCase;

    private final MutableLiveData<WorkerHome> _workerHome = new MutableLiveData<>();
    public LiveData<WorkerHome> workerHome = _workerHome;

    private final MutableLiveData<List<Appointment>> _todayAppointments =
            new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Appointment>> todayAppointments = _todayAppointments;

    @Inject
    public WorkerHomeViewModel(GetWorkerHomeUseCase getWorkerHomeUseCase) {
        this.getWorkerHomeUseCase = getWorkerHomeUseCase;
        loadWorkerHome();
    }

    public void loadWorkerHome() {
        loadWorkerHome(true);
    }

    public void loadWorkerHome(boolean showLoading) {
        if (showLoading) {
            setLoading(true);
        }

        getWorkerHomeUseCase.execute(result -> {
            if (showLoading) {
                setLoading(false);
            }

            if (result.isSuccess()) {
                WorkerHome home = result.getData();
                _workerHome.postValue(home);

                if (home.getTodayAppointments() != null) {
                    _todayAppointments.postValue(home.getTodayAppointments());
                } else {
                    _todayAppointments.postValue(new ArrayList<>());
                }
            } else {
                setError(result.getError().getMessage());
            }
        });
    }
}