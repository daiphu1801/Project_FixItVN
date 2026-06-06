package com.fixit.domain.worker.repository;

import com.fixit.domain.worker.entity.WorkerIdentityCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkerIdentityCardRepository extends JpaRepository<WorkerIdentityCard, UUID> {

    Optional<WorkerIdentityCard> findByWorker_WorkerId(UUID workerId);
}