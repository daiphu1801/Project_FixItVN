package com.fixit.domain.booking.repository;

import com.fixit.domain.booking.entity.WorkerQuotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkerQuotationRepository extends JpaRepository<WorkerQuotation, UUID> {
}
