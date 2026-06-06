package com.fixit.domain.worker.service;

import com.fixit.domain.upload.entity.UploadLinkedEntityType;
import com.fixit.domain.upload.entity.UploadPurpose;
import com.fixit.domain.upload.repository.UploadedFileRepository;
import com.fixit.domain.upload.service.ConsumedUpload;
import com.fixit.domain.upload.service.UploadConsumeService;
import com.fixit.domain.worker.dto.request.WorkerKycSubmitRequest;
import com.fixit.domain.worker.dto.response.WorkerKycResponse;
import com.fixit.domain.worker.entity.Worker;
import com.fixit.domain.worker.entity.WorkerIdentityCard;
import com.fixit.domain.worker.entity.WorkerIdentityStatus;
import com.fixit.domain.worker.entity.WorkerVerificationStatus;
import com.fixit.domain.worker.repository.WorkerIdentityCardRepository;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerKycServiceImpl implements WorkerKycService {

    private final CurrentWorkerResolver currentWorkerResolver;
    private final WorkerRepository workerRepository;
    private final WorkerIdentityCardRepository workerIdentityCardRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final UploadConsumeService uploadConsumeService;

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

        List<ConsumedUpload> certificates = uploadConsumeService.consumeAll(
                request.getCertificateUploadIds(),
                workerId,
                UploadPurpose.WORKER_CERTIFICATE,
                UploadLinkedEntityType.WORKER_KYC,
                identityCard.getId()
        );

        identityCard.setFrontImageUrl(frontImage.getFileUrl());
        identityCard.setBackImageUrl(backImage.getFileUrl());
        identityCard.setStatus(WorkerIdentityStatus.Pending);

        worker.setVerificationStatus(WorkerVerificationStatus.Pending);

        workerIdentityCardRepository.save(identityCard);
        workerRepository.save(worker);

        return toResponse(identityCard, certificates);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerKycResponse getMyKycStatus() {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        WorkerIdentityCard identityCard = workerIdentityCardRepository
                .findByWorker_WorkerId(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_KYC_NOT_FOUND));

        List<String> certificateUrls = uploadedFileRepository
                .findByLinkedEntityTypeAndLinkedEntityId(
                        UploadLinkedEntityType.WORKER_KYC.name(),
                        identityCard.getId()
                )
                .stream()
                .filter(uploadedFile -> uploadedFile.getPurpose() == UploadPurpose.WORKER_CERTIFICATE)
                .map(uploadedFile -> uploadedFile.getFileUrl())
                .toList();

        return WorkerKycResponse.builder()
                .kycId(identityCard.getId())
                .workerId(workerId)
                .frontImageUrl(identityCard.getFrontImageUrl())
                .backImageUrl(identityCard.getBackImageUrl())
                .certificateUrls(certificateUrls)
                .status(toApiStatus(identityCard.getStatus()))
                .build();
    }

    private Worker getWorker(UUID workerId) {
        return workerRepository.findById(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_NOT_FOUND));
    }

    private WorkerKycResponse toResponse(
            WorkerIdentityCard identityCard,
            List<ConsumedUpload> certificates
    ) {
        List<String> certificateUrls = certificates == null
                ? Collections.emptyList()
                : certificates.stream()
                .map(ConsumedUpload::getFileUrl)
                .toList();

        return WorkerKycResponse.builder()
                .kycId(identityCard.getId())
                .workerId(identityCard.getWorker() != null ? identityCard.getWorker().getWorkerId() : null)
                .frontImageUrl(identityCard.getFrontImageUrl())
                .backImageUrl(identityCard.getBackImageUrl())
                .certificateUrls(certificateUrls)
                .status(toApiStatus(identityCard.getStatus()))
                .build();
    }

    private String toApiStatus(WorkerIdentityStatus status) {
        return status != null ? status.name().toUpperCase(Locale.ROOT) : null;
    }
}
