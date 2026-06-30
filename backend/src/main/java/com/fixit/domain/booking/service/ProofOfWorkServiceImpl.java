package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.ProofOfWorkCreateRequest;
import com.fixit.domain.booking.dto.response.ProofOfWorkResponse;
import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingStatus;
import com.fixit.domain.booking.entity.ProofOfWork;
import com.fixit.domain.booking.entity.ProofType;
import com.fixit.domain.booking.repository.BookingHistoryRepository;
import com.fixit.domain.booking.repository.BookingRepository;
import com.fixit.domain.booking.repository.ProofOfWorkRepository;
import com.fixit.domain.upload.entity.UploadLinkedEntityType;
import com.fixit.domain.upload.entity.UploadedFile;
import com.fixit.domain.upload.entity.UploadPurpose;
import com.fixit.domain.upload.repository.UploadedFileRepository;
import com.fixit.domain.upload.service.ConsumedUpload;
import com.fixit.domain.upload.service.UploadConsumeService;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProofOfWorkServiceImpl implements ProofOfWorkService {

    private final CurrentWorkerResolver currentWorkerResolver;
    private final BookingRepository bookingRepository;
    private final BookingHistoryRepository bookingHistoryRepository;
    private final ProofOfWorkRepository proofOfWorkRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final UploadConsumeService uploadConsumeService;
    @Override
    @Transactional
    public ProofOfWorkResponse createProofOfWork(
            UUID bookingId,
            ProofOfWorkCreateRequest request
    ) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        Booking booking = bookingRepository.findWorkerBookingForUpdate(bookingId, workerId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() == BookingStatus.Completed || booking.getStatus() == BookingStatus.Cancelled) {
            throw new AppException(ErrorCode.BOOKING_INVALID_STATUS_TRANSITION);
        }

        if (bookingHistoryRepository.existsByBooking_IdAndStatusUpdate(bookingId, "Worker_Completed")) {
            throw new AppException(ErrorCode.BOOKING_INVALID_STATUS_TRANSITION);
        }

        ProofType proofType = parseProofType(request.getProofType());

        Optional<ProofOfWork> existingProof = proofOfWorkRepository.findByBooking_IdAndProofType(bookingId, proofType);
        if (existingProof.isPresent()) {
            boolean sameUpload = uploadedFileRepository
                    .findByLinkedEntityTypeAndLinkedEntityId(
                            UploadLinkedEntityType.PROOF_OF_WORK.name(),
                            bookingId
                    )
                    .stream()
                    .anyMatch(uploadedFile -> request.getUploadId().equals(uploadedFile.getId())
                            && uploadedFile.getFileUrl().equals(existingProof.get().getImageUrl()));

            if (sameUpload) {
                return toResponse(existingProof.get());
            }

            throw new AppException(ErrorCode.PROOF_OF_WORK_ALREADY_EXISTS);
        }

        UploadPurpose expectedPurpose = expectedPurposeOf(proofType);

        ConsumedUpload consumedUpload = uploadConsumeService.consume(
                request.getUploadId(),
                workerId,
                expectedPurpose,
                UploadLinkedEntityType.PROOF_OF_WORK,
                bookingId
        );

        ProofOfWork proofOfWork = ProofOfWork.builder()
                .booking(booking)
                .imageUrl(consumedUpload.getFileUrl())
                .proofType(proofType)
                .build();

        ProofOfWork saved = proofOfWorkRepository.save(proofOfWork);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProofOfWorkResponse> getProofOfWorks(UUID bookingId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        bookingRepository.findWorkerBookingForUpdate(bookingId, workerId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        return proofOfWorkRepository.findByBooking_IdOrderByCapturedAtAsc(bookingId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProofType parseProofType(String rawProofType) {
        if (rawProofType == null || rawProofType.isBlank()) {
            throw new AppException(ErrorCode.INVALID_REQUEST_PARAMETER);
        }

        try {
            return ProofType.valueOf(rawProofType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.INVALID_REQUEST_PARAMETER);
        }
    }

    private UploadPurpose expectedPurposeOf(ProofType proofType) {
        return switch (proofType) {
            case BEFORE_REPAIR -> UploadPurpose.PROOF_BEFORE_REPAIR;
            case AFTER_REPAIR -> UploadPurpose.PROOF_AFTER_REPAIR;
        };
    }

    private ProofOfWorkResponse toResponse(ProofOfWork proofOfWork) {
        return ProofOfWorkResponse.builder()
                .proofId(proofOfWork.getId())
                .bookingId(proofOfWork.getBooking() != null ? proofOfWork.getBooking().getId() : null)
                .imageUrl(proofOfWork.getImageUrl())
                .proofType(proofOfWork.getProofType() != null ? proofOfWork.getProofType().name() : null)
                .capturedAt(proofOfWork.getCapturedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteProofOfWork(UUID bookingId, UUID proofId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        Booking booking = bookingRepository.findWorkerBookingForUpdate(bookingId, workerId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getStatus() == BookingStatus.Completed || booking.getStatus() == BookingStatus.Cancelled) {
            throw new AppException(ErrorCode.BOOKING_INVALID_STATUS_TRANSITION);
        }

        if (bookingHistoryRepository.existsByBooking_IdAndStatusUpdate(bookingId, "Worker_Completed")) {
            throw new AppException(ErrorCode.BOOKING_INVALID_STATUS_TRANSITION);
        }

        ProofOfWork proofOfWork = proofOfWorkRepository.findById(proofId)
                .orElseThrow(() -> new AppException(ErrorCode.PROOF_OF_WORK_NOT_FOUND));

        if (!proofOfWork.getBooking().getId().equals(bookingId)) {
            throw new AppException(ErrorCode.PROOF_OF_WORK_NOT_FOUND);
        }

        // Unlink the file from upload system
        List<UploadedFile> files = uploadedFileRepository.findByLinkedEntityTypeAndLinkedEntityId(
                UploadLinkedEntityType.PROOF_OF_WORK.name(),
                bookingId
        );
        for (UploadedFile file : files) {
            if (file.getFileUrl().equals(proofOfWork.getImageUrl())) {
                file.setLinkedEntityType(null);
                file.setLinkedEntityId(null);
                file.setUsedAt(null);
                uploadedFileRepository.save(file);
                break;
            }
        }

        proofOfWorkRepository.delete(proofOfWork);
    }
}
