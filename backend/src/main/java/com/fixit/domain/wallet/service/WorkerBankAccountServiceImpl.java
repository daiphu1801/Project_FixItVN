package com.fixit.domain.wallet.service;

import com.fixit.domain.wallet.dto.request.WorkerBankAccountCreateRequest;
import com.fixit.domain.wallet.dto.request.WorkerBankAccountUpdateRequest;
import com.fixit.domain.wallet.dto.response.WorkerBankAccountResponse;
import com.fixit.domain.wallet.dto.response.WorkerBankAccountsResponse;
import com.fixit.domain.wallet.entity.WorkerBankAccount;
import com.fixit.domain.wallet.repository.TransactionHistoryRepository;
import com.fixit.domain.wallet.repository.WorkerBankAccountRepository;
import com.fixit.domain.worker.entity.Worker;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerBankAccountServiceImpl implements WorkerBankAccountService {

    private final CurrentWorkerResolver currentWorkerResolver;
    private final WorkerRepository workerRepository;
    private final WorkerBankAccountRepository workerBankAccountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    @Override
    @Transactional(readOnly = true)
    public WorkerBankAccountsResponse getMyBankAccounts() {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        List<WorkerBankAccountResponse> accounts = workerBankAccountRepository.findAllByWorkerId(workerId)
                .stream()
                .map(this::toResponse)
                .toList();

        UUID defaultBankAccountId = accounts.stream()
                .filter(account -> Boolean.TRUE.equals(account.getDefaultAccount()))
                .map(WorkerBankAccountResponse::getBankAccountId)
                .findFirst()
                .orElse(null);

        return WorkerBankAccountsResponse.builder()
                .workerId(workerId)
                .defaultBankAccountId(defaultBankAccountId)
                .bankAccounts(accounts)
                .build();
    }

    @Override
    @Transactional
    public WorkerBankAccountResponse createMyBankAccount(WorkerBankAccountCreateRequest request) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_NOT_FOUND));

        long currentAccountCount = workerBankAccountRepository.countByWorkerId(workerId);

        boolean shouldSetDefault = currentAccountCount == 0
                || Boolean.TRUE.equals(request.getDefaultAccount());

        if (shouldSetDefault) {
            workerBankAccountRepository.clearDefaultByWorkerId(workerId);
        }

        WorkerBankAccount account = WorkerBankAccount.builder()
                .worker(worker)
                .bankName(normalizeRequired(request.getBankName()))
                .accountNumber(normalizeRequired(request.getAccountNumber()))
                .accountName(normalizeRequired(request.getAccountName()))
                .defaultAccount(shouldSetDefault)
                .build();

        return toResponse(workerBankAccountRepository.save(account));
    }

    @Override
    @Transactional
    public WorkerBankAccountResponse updateMyBankAccount(
            UUID bankAccountId,
            WorkerBankAccountUpdateRequest request
    ) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        WorkerBankAccount account = getOwnedBankAccount(workerId, bankAccountId);

        if (request.getBankName() != null) {
            account.setBankName(normalizePatchValue(request.getBankName()));
        }

        if (request.getAccountNumber() != null) {
            account.setAccountNumber(normalizePatchValue(request.getAccountNumber()));
        }

        if (request.getAccountName() != null) {
            account.setAccountName(normalizePatchValue(request.getAccountName()));
        }

        return toResponse(workerBankAccountRepository.save(account));
    }

    @Override
    @Transactional
    public void deleteMyBankAccount(UUID bankAccountId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        WorkerBankAccount account = getOwnedBankAccount(workerId, bankAccountId);

        if (transactionHistoryRepository.existsByTargetBankAccount_Id(bankAccountId)) {
            throw new AppException(ErrorCode.WORKER_BANK_ACCOUNT_IN_USE);
        }

        boolean wasDefault = Boolean.TRUE.equals(account.getDefaultAccount());

        List<WorkerBankAccount> remainingAccounts = workerBankAccountRepository.findAllByWorkerId(workerId)
                .stream()
                .filter(item -> !item.getId().equals(bankAccountId))
                .toList();

        workerBankAccountRepository.delete(account);
        workerBankAccountRepository.flush();

        if (wasDefault && !remainingAccounts.isEmpty()) {
            WorkerBankAccount nextDefault = remainingAccounts.get(0);
            nextDefault.setDefaultAccount(true);
            workerBankAccountRepository.save(nextDefault);
        }
    }

    @Override
    @Transactional
    public WorkerBankAccountResponse setMyDefaultBankAccount(UUID bankAccountId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        WorkerBankAccount account = getOwnedBankAccount(workerId, bankAccountId);

        workerBankAccountRepository.clearDefaultByWorkerId(workerId);
        account.setDefaultAccount(true);

        return toResponse(workerBankAccountRepository.save(account));
    }

    private WorkerBankAccount getOwnedBankAccount(UUID workerId, UUID bankAccountId) {
        return workerBankAccountRepository.findByIdAndWorkerId(bankAccountId, workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_BANK_ACCOUNT_NOT_FOUND));
    }

    private WorkerBankAccountResponse toResponse(WorkerBankAccount account) {
        return WorkerBankAccountResponse.builder()
                .bankAccountId(account.getId())
                .bankName(account.getBankName())
                .accountName(account.getAccountName())
                .accountNumberMasked(maskAccountNumber(account.getAccountNumber()))
                .defaultAccount(Boolean.TRUE.equals(account.getDefaultAccount()))
                .build();
    }

    private String normalizeRequired(String value) {
        if (value == null || value.isBlank()) {
            throw new AppException(ErrorCode.INVALID_REQUEST_PARAMETER);
        }

        return value.trim();
    }

    private String normalizePatchValue(String value) {
        if (value == null || value.isBlank()) {
            throw new AppException(ErrorCode.INVALID_REQUEST_PARAMETER);
        }

        return value.trim();
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return null;
        }

        String trimmed = accountNumber.trim();
        int length = trimmed.length();

        if (length <= 4) {
            return "****";
        }

        return "****" + trimmed.substring(length - 4);
    }
}