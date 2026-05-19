package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.request.WorkerLocationUpdateRequest;
import com.fixit.domain.worker.dto.request.WorkerStatusUpdateRequest;
import com.fixit.domain.worker.dto.response.WorkerHomeResponse;

public interface WorkerHomeService {

    WorkerHomeResponse getHome();

    WorkerHomeResponse updateStatus(WorkerStatusUpdateRequest request);

    WorkerHomeResponse updateLocation(WorkerLocationUpdateRequest request);
}