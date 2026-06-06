package com.fixit.feature.worker.kyc.domain.model;

import java.util.List;

public class WorkerKyc {
    private final String kycId;
    private final String status; // PENDING, APPROVED, REJECTED, UNVERIFIED
    private final List<String> certificateUrls;

    public WorkerKyc(String kycId, String status, List<String> certificateUrls) {
        this.kycId = kycId;
        this.status = status;
        this.certificateUrls = certificateUrls;
    }

    public String getKycId() {
        return kycId;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getCertificateUrls() {
        return certificateUrls;
    }
}
