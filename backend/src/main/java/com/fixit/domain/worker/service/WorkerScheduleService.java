package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.response.WorkerScheduleResponse;

import java.time.LocalDate;

public interface WorkerScheduleService {

    WorkerScheduleResponse getMySchedule(LocalDate date);
}