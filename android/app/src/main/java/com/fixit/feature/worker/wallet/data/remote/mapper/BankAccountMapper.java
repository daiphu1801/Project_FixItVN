package com.fixit.feature.worker.wallet.data.remote.mapper;

import com.fixit.feature.worker.wallet.data.remote.dto.request.BankAccountCreateRequest;
import com.fixit.feature.worker.wallet.data.remote.dto.response.BankAccountResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.request.BankAccountUpdateRequest;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;

import java.util.ArrayList;
import java.util.List;

public final class BankAccountMapper {

    private BankAccountMapper() {}

    public static BankAccount toDomain(BankAccountResponse response) {
        if (response == null) {
            return null;
        }

        return new BankAccount(
                response.getBankAccountId(),
                response.getBankName(),
                response.getAccountNumberMasked(),
                response.getAccountName(),
                Boolean.TRUE.equals(response.getDefaultAccount())
        );
    }

    public static List<BankAccount> toDomainList(List<BankAccountResponse> responses) {
        List<BankAccount> result = new ArrayList<>();
        if (responses == null) {
            return result;
        }

        for (BankAccountResponse response : responses) {
            BankAccount account = toDomain(response);
            if (account != null) {
                result.add(account);
            }
        }
        return result;
    }

    public static BankAccountCreateRequest toCreateRequest(BankAccount account) {
        return new BankAccountCreateRequest(
                account.getBankName(),
                account.getAccountNumber(),
                account.getAccountHolderName(),
                account.isDefault()
        );
    }

    public static BankAccountUpdateRequest toUpdateRequest(BankAccount account) {
        String accountNumber = account.getAccountNumber();

        if (accountNumber != null && accountNumber.contains("*")) {
            accountNumber = null;
        }

        if (accountNumber != null && accountNumber.trim().isEmpty()) {
            accountNumber = null;
        }

        return new BankAccountUpdateRequest(
                emptyToNull(account.getBankName()),
                accountNumber,
                emptyToNull(account.getAccountHolderName())
        );
    }

    private static String emptyToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}