package com.fixit.domain.wallet.repository;

import com.fixit.domain.wallet.entity.TransactionHistory;
import com.fixit.domain.wallet.entity.TransactionStatus;
import com.fixit.domain.wallet.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID> {

    boolean existsByTargetBankAccount_Id(UUID bankAccountId);

    Page<TransactionHistory> findByWallet_WorkerId(UUID workerId, Pageable pageable);

    Page<TransactionHistory> findByWallet_WorkerIdAndTransactionType(
            UUID workerId,
            TransactionType transactionType,
            Pageable pageable
    );

    Optional<TransactionHistory> findByIdAndWallet_WorkerIdAndTransactionType(
            UUID id,
            UUID workerId,
            TransactionType transactionType
    );

    boolean existsByTransactionCode(String transactionCode);

    boolean existsByWallet_WorkerIdAndTransactionTypeAndStatus(
            UUID workerId,
            TransactionType transactionType,
            TransactionStatus status
    );

    // Tìm giao dịch Pending theo transactionCode xuất hiện trong nội dung chuyển khoản
    @Query("SELECT t FROM TransactionHistory t WHERE t.status = 'Pending' " +
            "AND t.transactionType = 'Deposit' " +
            "AND :content LIKE CONCAT('%', t.transactionCode, '%')")
    Optional<TransactionHistory> findByTransactionCodeInContent(@Param("content") String content);
}