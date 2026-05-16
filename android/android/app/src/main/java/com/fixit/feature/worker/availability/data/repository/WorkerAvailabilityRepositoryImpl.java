package com.fixit.feature.worker.availability.data.repository;

import com.fixit.feature.worker.availability.domain.repository.WorkerAvailabilityRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkerAvailabilityRepositoryImpl implements WorkerAvailabilityRepository {
    private boolean isOnline = false;

    @Inject
    public WorkerAvailabilityRepositoryImpl() {
    }

    @Override
    public boolean isOnline() {
        return isOnline;
    }

    @Override
    public boolean toggleOnline() {
        isOnline = !isOnline;
        return isOnline;
    }
}
