package com.fixit.feature.worker.kyc.domain.model;

import java.util.List;

public class WorkerKyc {
    private final String kycId;
    private final String status; // PENDING, APPROVED, REJECTED, UNVERIFIED
    private final String frontImageUrl;
    private final String backImageUrl;
    private final String selfieImageUrl;
    private final String ocrFullName;
    private final String ocrIdentityCard;
    private final Double similarityScore;
    private final List<String> certificateUrls;

    public WorkerKyc(
            String kycId, 
            String status, 
            String frontImageUrl,
            String backImageUrl,
            String selfieImageUrl,
            String ocrFullName,
            String ocrIdentityCard,
            Double similarityScore,
            List<String> certificateUrls
    ) {
        this.kycId = kycId;
        this.status = status;
        this.frontImageUrl = frontImageUrl;
        this.backImageUrl = backImageUrl;
        this.selfieImageUrl = selfieImageUrl;
        this.ocrFullName = ocrFullName;
        this.ocrIdentityCard = ocrIdentityCard;
        this.similarityScore = similarityScore;
        this.certificateUrls = certificateUrls;
    }

    public String getKycId() {
        return kycId;
    }

    public String getStatus() {
        return status;
    }

    public String getFrontImageUrl() {
        return frontImageUrl;
    }

    public String getBackImageUrl() {
        return backImageUrl;
    }

    public String getSelfieImageUrl() {
        return selfieImageUrl;
    }

    public String getOcrFullName() {
        return ocrFullName;
    }

    public String getOcrIdentityCard() {
        return ocrIdentityCard;
    }

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public List<String> getCertificateUrls() {
        return certificateUrls;
    }
}
