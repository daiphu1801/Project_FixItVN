package com.fixit.domain.wallet.repository;

import com.fixit.domain.wallet.entity.WorkerBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkerBankAccountRepository extends JpaRepository<WorkerBankAccount, UUID> {

    @Query("""
            SELECT account
            FROM WorkerBankAccount account
            WHERE account.worker.workerId = :workerId
            ORDER BY CASE WHEN account.defaultAccount = true THEN 0 ELSE 1 END,
                     account.bankName ASC,
                     account.accountNumber ASC
            """)
    List<WorkerBankAccount> findAllByWorkerId(@Param("workerId") UUID workerId);

    @Query("""
            SELECT account
            FROM WorkerBankAccount account
            WHERE account.id = :bankAccountId
              AND account.worker.workerId = :workerId
            """)
    Optional<WorkerBankAccount> findByIdAndWorkerId(
            @Param("bankAccountId") UUID bankAccountId,
            @Param("workerId") UUID workerId
    );

    @Query("""
            SELECT COUNT(account)
            FROM WorkerBankAccount account
            WHERE account.worker.workerId = :workerId
            """)
    long countByWorkerId(@Param("workerId") UUID workerId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE WorkerBankAccount account
            SET account.defaultAccount = false
            WHERE account.worker.workerId = :workerId
            """)
    int clearDefaultByWorkerId(@Param("workerId") UUID workerId);
}