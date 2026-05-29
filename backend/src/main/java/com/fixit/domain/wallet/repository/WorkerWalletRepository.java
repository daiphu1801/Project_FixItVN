package com.fixit.domain.wallet.repository;

import com.fixit.domain.wallet.entity.WorkerWallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface WorkerWalletRepository extends JpaRepository<WorkerWallet, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from WorkerWallet w where w.workerId = :workerId")
    Optional<WorkerWallet> findByWorkerIdForUpdate(@Param("workerId") UUID workerId);
}