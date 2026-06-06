package com.fixit.domain.booking.repository;

import com.fixit.domain.booking.entity.ProofOfWork;
import com.fixit.domain.booking.entity.ProofType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProofOfWorkRepository extends JpaRepository<ProofOfWork, UUID> {

    List<ProofOfWork> findByBooking_IdOrderByCapturedAtAsc(UUID bookingId);

    Optional<ProofOfWork> findByBooking_IdAndProofType(UUID bookingId, ProofType proofType);
}