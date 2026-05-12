package com.fixit.feature.worker.home.domain.usecase;

import com.fixit.feature.worker.home.domain.model.Appointment;
import com.fixit.feature.worker.home.domain.repository.WorkerHomeRepository;

import java.util.List;

import javax.inject.Inject;

public class GetTodayAppointmentsUseCase {
    private final WorkerHomeRepository repository;

    @Inject
    public GetTodayAppointmentsUseCase(WorkerHomeRepository repository) {
        this.repository = repository;
    }

    public List<Appointment> execute() {
        return repository.getTodayAppointments();
    }
}
