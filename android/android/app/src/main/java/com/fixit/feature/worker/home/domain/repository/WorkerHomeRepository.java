package com.fixit.feature.worker.home.domain.repository;

import com.fixit.feature.worker.home.domain.model.Appointment;

import java.util.List;

public interface WorkerHomeRepository {
    List<Appointment> getTodayAppointments();
}
