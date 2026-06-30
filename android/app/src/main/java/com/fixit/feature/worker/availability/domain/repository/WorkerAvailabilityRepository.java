package com.fixit.feature.worker.availability.domain.repository;

import com.fixit.core.common.ResultCallback;

public interface WorkerAvailabilityRepository {
    void isOnline(ResultCallback<Boolean> callback);

    void setOnline(boolean online, ResultCallback<Boolean> callback);
}
