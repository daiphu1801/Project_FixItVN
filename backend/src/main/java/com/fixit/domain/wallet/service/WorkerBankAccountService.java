package com.fixit.domain.wallet.service;

import com.fixit.domain.wallet.dto.request.WorkerBankAccountCreateRequest;
import com.fixit.domain.wallet.dto.request.WorkerBankAccountUpdateRequest;
import com.fixit.domain.wallet.dto.response.WorkerBankAccountResponse;
import com.fixit.domain.wallet.dto.response.WorkerBankAccountsResponse;

import java.util.UUID;

public interface WorkerBankAccountService {

    WorkerBankAccountsResponse getMyBankAccounts();

    WorkerBankAccountResponse createMyBankAccount(WorkerBankAccountCreateRequest request);

    WorkerBankAccountResponse updateMyBankAccount(UUID bankAccountId, WorkerBankAccountUpdateRequest request);

    void deleteMyBankAccount(UUID bankAccountId);

    WorkerBankAccountResponse setMyDefaultBankAccount(UUID bankAccountId);
}