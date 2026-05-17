package com.fixit.feature.worker.home.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.home.domain.model.Appointment;
import com.fixit.feature.worker.home.domain.usecase.GetTodayAppointmentsUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerHomeViewModel extends BaseViewModel {
    private final GetTodayAppointmentsUseCase getTodayAppointmentsUseCase;

    private final MutableLiveData<List<Appointment>> _todayAppointments = new MutableLiveData<>();
    public LiveData<List<Appointment>> todayAppointments = _todayAppointments;

    @Inject
    public WorkerHomeViewModel(GetTodayAppointmentsUseCase getTodayAppointmentsUseCase) {
        this.getTodayAppointmentsUseCase = getTodayAppointmentsUseCase;
        loadTodayAppointments();
    }

    public void loadTodayAppointments() {
        _todayAppointments.setValue(getTodayAppointmentsUseCase.execute());
    }
}
