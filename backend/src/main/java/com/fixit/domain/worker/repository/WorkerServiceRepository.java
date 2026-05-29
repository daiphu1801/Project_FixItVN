package com.fixit.domain.worker.repository;

import com.fixit.domain.worker.entity.WorkerService;
import com.fixit.domain.worker.entity.WorkerServiceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkerServiceRepository extends JpaRepository<WorkerService, WorkerServiceId> {

    List<WorkerService> findByWorker_WorkerId(UUID workerId);

    void deleteByWorker_WorkerId(UUID workerId);
}