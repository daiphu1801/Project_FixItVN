package com.fixit.feature.worker.availability.domain.repository;

public interface WorkerAvailabilityRepository {
    boolean isOnline();

    boolean toggleOnline();
}
