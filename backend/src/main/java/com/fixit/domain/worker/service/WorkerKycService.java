package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.request.WorkerKycSubmitRequest;
import com.fixit.domain.worker.dto.response.WorkerKycResponse;

public interface WorkerKycService {

    WorkerKycResponse submitKyc(WorkerKycSubmitRequest request);

    WorkerKycResponse getMyKycStatus();
}