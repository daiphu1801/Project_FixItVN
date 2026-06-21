package com.fixit.domain.worker.service;

import com.fixit.domain.upload.entity.UploadLinkedEntityType;
import com.fixit.domain.upload.entity.UploadPurpose;
import com.fixit.domain.upload.repository.UploadedFileRepository;
import com.fixit.domain.upload.service.ConsumedUpload;
import com.fixit.domain.upload.service.StorageUploadSigner;
import com.fixit.domain.upload.service.UploadConsumeService;
import com.fixit.domain.worker.dto.request.WorkerKycSubmitRequest;
import com.fixit.domain.worker.dto.response.VnptKycConfigResponse;
import com.fixit.domain.worker.dto.response.VnptOcrResult;
import com.fixit.domain.worker.dto.response.VnptFaceMatchResult;
import com.fixit.domain.worker.dto.response.WorkerKycResponse;
import com.fixit.domain.worker.entity.Worker;
import com.fixit.domain.worker.entity.WorkerIdentityCard;
import com.fixit.domain.worker.entity.WorkerIdentityStatus;
import com.fixit.domain.worker.entity.WorkerVerificationStatus;
import com.fixit.domain.worker.repository.WorkerIdentityCardRepository;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import com.fixit.global.config.VnptKycProperties;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerKycServiceImpl implements WorkerKycService {

    private final CurrentWorkerResolver currentWorkerResolver;
    private final WorkerRepository workerRepository;
    private final WorkerIdentityCardRepository workerIdentityCardRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final UploadConsumeService uploadConsumeService;
    private final StorageUploadSigner storageUploadSigner;
    private final VnptKycProperties vnptKycProperties;
    private final VnptKycClient vnptKycClient;

    @Override
    @Transactional
    public WorkerKycResponse submitKyc(WorkerKycSubmitRequest request) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        Worker worker = getWorker(workerId);

        WorkerIdentityCard identityCard = workerIdentityCardRepository
                .findByWorker_WorkerId(workerId)
                .orElseGet(() -> WorkerIdentityCard.builder()
                        .worker(worker)
                        .status(WorkerIdentityStatus.Pending)
                        .build());

        identityCard.setStatus(WorkerIdentityStatus.Pending);
        identityCard = workerIdentityCardRepository.saveAndFlush(identityCard);

        ConsumedUpload frontImage = uploadConsumeService.consume(
                request.getFrontImageUploadId(),
                workerId,
                UploadPurpose.WORKER_KYC_FRONT,
                UploadLinkedEntityType.WORKER_KYC,
                identityCard.getId()
        );

        ConsumedUpload backImage = uploadConsumeService.consume(
                request.getBackImageUploadId(),
                workerId,
                UploadPurpose.WORKER_KYC_BACK,
                UploadLinkedEntityType.WORKER_KYC,
                identityCard.getId()
        );

        ConsumedUpload selfieImage = uploadConsumeService.consume(
                request.getSelfieImageUploadId(),
                workerId,
                UploadPurpose.WORKER_KYC_SELFIE,
                UploadLinkedEntityType.WORKER_KYC,
                identityCard.getId()
        );

        List<ConsumedUpload> certificates = uploadConsumeService.consumeAll(
                request.getCertificateUploadIds(),
                workerId,
                UploadPurpose.WORKER_CERTIFICATE,
                UploadLinkedEntityType.WORKER_KYC,
                identityCard.getId()
        );

        identityCard.setFrontImageUrl(frontImage.getFileUrl());
        identityCard.setBackImageUrl(backImage.getFileUrl());
        identityCard.setSelfieImageUrl(selfieImage.getFileUrl());

        // Perform automatic VNPT eKYC
        try {
            byte[] frontBytes = downloadImage(storageUploadSigner.buildFileUrl(frontImage.getObjectKey()));
            byte[] backBytes = downloadImage(storageUploadSigner.buildFileUrl(backImage.getObjectKey()));
            byte[] selfieBytes = downloadImage(storageUploadSigner.buildFileUrl(selfieImage.getObjectKey()));

            VnptOcrResult ocrFront = vnptKycClient.performOcr(frontBytes, "front");
            VnptOcrResult ocrBack = vnptKycClient.performOcr(backBytes, "back");
            VnptFaceMatchResult faceMatch = vnptKycClient.matchFaces(frontBytes, selfieBytes);

            if (ocrFront.isSuccess() && faceMatch.isSuccess() 
                    && faceMatch.getSimilarityScore() >= vnptKycProperties.getSimilarityThreshold()) {
                
                identityCard.setStatus(WorkerIdentityStatus.Approved);
                identityCard.setOcrFullName(ocrFront.getFullName());
                identityCard.setOcrIdentityCard(ocrFront.getIdNumber());
                identityCard.setSimilarityScore(BigDecimal.valueOf(faceMatch.getSimilarityScore()));

                worker.setIdentityCard(ocrFront.getIdNumber());
                worker.setFullName(ocrFront.getFullName());
                worker.setVerificationStatus(WorkerVerificationStatus.Approved);
                log.info("eKYC auto-verified successfully for worker {}: Score {}, Name {}", 
                        workerId, faceMatch.getSimilarityScore(), ocrFront.getFullName());
            } else {
                identityCard.setStatus(WorkerIdentityStatus.Rejected);
                if (faceMatch.isSuccess()) {
                    identityCard.setSimilarityScore(BigDecimal.valueOf(faceMatch.getSimilarityScore()));
                }
                if (ocrFront.isSuccess()) {
                    identityCard.setOcrFullName(ocrFront.getFullName());
                    identityCard.setOcrIdentityCard(ocrFront.getIdNumber());
                }
                worker.setVerificationStatus(WorkerVerificationStatus.Unverified);
                log.warn("eKYC auto-verification failed for worker {}. Similarity score: {}, OCR success: {}", 
                        workerId, faceMatch.getSimilarityScore(), ocrFront.isSuccess());
            }
        } catch (Exception e) {
            log.error("Error running VNPT eKYC. Falling back to pending manual review.", e);
            identityCard.setStatus(WorkerIdentityStatus.Pending);
            worker.setVerificationStatus(WorkerVerificationStatus.Pending);
        }

        workerIdentityCardRepository.save(identityCard);
        workerRepository.save(worker);

        return toResponse(identityCard, certificates, frontImage, backImage, selfieImage);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerKycResponse getMyKycStatus() {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        WorkerIdentityCard identityCard = workerIdentityCardRepository
                .findByWorker_WorkerId(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_KYC_NOT_FOUND));

        List<com.fixit.domain.upload.entity.UploadedFile> files = uploadedFileRepository
                .findByLinkedEntityTypeAndLinkedEntityId(
                        UploadLinkedEntityType.WORKER_KYC.name(),
                        identityCard.getId()
                );

        String frontUrl = files.stream()
                .filter(f -> f.getPurpose() == UploadPurpose.WORKER_KYC_FRONT)
                .findFirst()
                .map(f -> storageUploadSigner.buildFileUrl(f.getObjectKey()))
                .orElse(identityCard.getFrontImageUrl());

        String backUrl = files.stream()
                .filter(f -> f.getPurpose() == UploadPurpose.WORKER_KYC_BACK)
                .findFirst()
                .map(f -> storageUploadSigner.buildFileUrl(f.getObjectKey()))
                .orElse(identityCard.getBackImageUrl());

        String selfieUrl = files.stream()
                .filter(f -> f.getPurpose() == UploadPurpose.WORKER_KYC_SELFIE)
                .findFirst()
                .map(f -> storageUploadSigner.buildFileUrl(f.getObjectKey()))
                .orElse(identityCard.getSelfieImageUrl());

        List<String> certificateUrls = files.stream()
                .filter(f -> f.getPurpose() == UploadPurpose.WORKER_CERTIFICATE)
                .map(f -> storageUploadSigner.buildFileUrl(f.getObjectKey()))
                .toList();

        return WorkerKycResponse.builder()
                .kycId(identityCard.getId())
                .workerId(workerId)
                .frontImageUrl(frontUrl)
                .backImageUrl(backUrl)
                .selfieImageUrl(selfieUrl)
                .ocrFullName(identityCard.getOcrFullName())
                .ocrIdentityCard(identityCard.getOcrIdentityCard())
                .similarityScore(identityCard.getSimilarityScore())
                .certificateUrls(certificateUrls)
                .status(toApiStatus(identityCard.getStatus()))
                .build();
    }

    @Override
    public VnptKycConfigResponse getKycConfig() {
        return VnptKycConfigResponse.builder()
                .tokenId(vnptKycProperties.getTokenId())
                .tokenKey(vnptKycProperties.getTokenKey())
                .apiUrl(vnptKycProperties.getApiUrl())
                .build();
    }

    private Worker getWorker(UUID workerId) {
        return workerRepository.findById(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_NOT_FOUND));
    }

    private byte[] downloadImage(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(url, byte[].class);
        } catch (Exception e) {
            log.error("Failed to download image from: " + url, e);
            throw new AppException(ErrorCode.UPLOAD_STORAGE_ERROR, "Không thể tải ảnh từ storage để xác thực eKYC: " + e.getMessage());
        }
    }

    private WorkerKycResponse toResponse(
            WorkerIdentityCard identityCard,
            List<ConsumedUpload> certificates,
            ConsumedUpload front,
            ConsumedUpload back,
            ConsumedUpload selfie
    ) {
        String frontUrl = front != null ? storageUploadSigner.buildFileUrl(front.getObjectKey()) : identityCard.getFrontImageUrl();
        String backUrl = back != null ? storageUploadSigner.buildFileUrl(back.getObjectKey()) : identityCard.getBackImageUrl();
        String selfieUrl = selfie != null ? storageUploadSigner.buildFileUrl(selfie.getObjectKey()) : identityCard.getSelfieImageUrl();

        List<String> certificateUrls = certificates == null
                ? Collections.emptyList()
                : certificates.stream()
                .map(c -> storageUploadSigner.buildFileUrl(c.getObjectKey()))
                .toList();

        return WorkerKycResponse.builder()
                .kycId(identityCard.getId())
                .workerId(identityCard.getWorker() != null ? identityCard.getWorker().getWorkerId() : null)
                .frontImageUrl(frontUrl)
                .backImageUrl(backUrl)
                .selfieImageUrl(selfieUrl)
                .ocrFullName(identityCard.getOcrFullName())
                .ocrIdentityCard(identityCard.getOcrIdentityCard())
                .similarityScore(identityCard.getSimilarityScore())
                .certificateUrls(certificateUrls)
                .status(toApiStatus(identityCard.getStatus()))
                .build();
    }

    private String toApiStatus(WorkerIdentityStatus status) {
        return status != null ? status.name().toUpperCase(Locale.ROOT) : null;
    }
}
