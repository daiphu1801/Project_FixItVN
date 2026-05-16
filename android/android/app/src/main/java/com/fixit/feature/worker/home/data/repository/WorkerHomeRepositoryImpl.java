package com.fixit.feature.worker.home.data.repository;

import com.fixit.feature.worker.home.domain.model.Appointment;
import com.fixit.feature.worker.home.domain.repository.WorkerHomeRepository;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkerHomeRepositoryImpl implements WorkerHomeRepository {
    @Inject
    public WorkerHomeRepositoryImpl() {
    }

    @Override
    public List<Appointment> getTodayAppointments() {
        return Arrays.asList(
                new Appointment("08:00 Hom nay", "Sua may lanh khong mat", "123 Nguyen Trai, Quan 1, TP.HCM"),
                new Appointment("10:30 Hom nay", "Thay o khoa cua chinh", "78 Dinh Tien Hoang, Binh Thanh"),
                new Appointment("14:00 Hom nay", "Thong tac bon rua bat", "456 Le Van Sy, Quan 3, TP.HCM")
        );
    }
}
