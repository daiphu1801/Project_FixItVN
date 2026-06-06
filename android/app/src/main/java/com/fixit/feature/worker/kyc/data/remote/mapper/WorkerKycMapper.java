package com.fixit.feature.worker.kyc.data.remote.mapper;

import com.fixit.feature.upload.data.remote.dto.response.WorkerKycResponse;
import com.fixit.feature.worker.kyc.domain.model.WorkerKyc;

import java.util.Locale;

public class WorkerKycMapper {

    public static WorkerKyc toDomain(WorkerKycResponse response) {
        if (response == null) return null;
        return new WorkerKyc(
                response.getKycId(),
                normalizeStatus(response.getStatus()),
                response.getCertificateUrls()
        );
    }

    private static String normalizeStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "UNVERIFIED";
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }
}
