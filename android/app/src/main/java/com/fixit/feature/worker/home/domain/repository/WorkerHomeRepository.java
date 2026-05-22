package com.fixit.feature.worker.home.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.home.domain.model.WorkerHome;

public interface WorkerHomeRepository {

    void getWorkerHome(ResultCallback<WorkerHome> callback);
}