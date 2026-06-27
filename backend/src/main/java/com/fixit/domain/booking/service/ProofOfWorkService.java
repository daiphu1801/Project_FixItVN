package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.ProofOfWorkCreateRequest;
import com.fixit.domain.booking.dto.response.ProofOfWorkResponse;

import java.util.List;
import java.util.UUID;

public interface ProofOfWorkService {

    ProofOfWorkResponse createProofOfWork(UUID bookingId, ProofOfWorkCreateRequest request);

    List<ProofOfWorkResponse> getProofOfWorks(UUID bookingId);

    void deleteProofOfWork(UUID bookingId, UUID proofId);
}