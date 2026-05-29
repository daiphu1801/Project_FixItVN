package com.fixit.feature.worker.wallet.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BankAccountsResponse {

    @SerializedName("workerId")
    private String workerId;

    @SerializedName("defaultBankAccountId")
    private String defaultBankAccountId;

    @SerializedName("bankAccounts")
    private List<BankAccountResponse> bankAccounts;

    public String getWorkerId() {
        return workerId;
    }

    public String getDefaultBankAccountId() {
        return defaultBankAccountId;
    }

    public List<BankAccountResponse> getBankAccounts() {
        return bankAccounts;
    }
}