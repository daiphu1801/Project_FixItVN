package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.response.WorkerHistoryResponse;

public interface WorkerHistoryService {



    WorkerHistoryResponse getMyHistory(String status, Integer page, Integer size);
}