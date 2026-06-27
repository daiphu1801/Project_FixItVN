package com.fixit.feature.worker.kyc.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.upload.data.remote.dto.request.WorkerKycSubmitRequest;
import com.fixit.feature.worker.kyc.data.remote.dto.response.VnptKycConfigResponse;
import com.fixit.feature.worker.kyc.domain.model.WorkerKyc;

public interface WorkerKycRepository {
    void getKycConfig(ResultCallback<VnptKycConfigResponse> callback);
    void getKycStatus(ResultCallback<WorkerKyc> callback);
    void submitKyc(WorkerKycSubmitRequest request, ResultCallback<WorkerKyc> callback);
}
