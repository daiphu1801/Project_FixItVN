package com.fixit.feature.worker.wallet.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

public class BankAccountCreateRequest {

    @SerializedName("bankName")
    private final String bankName;

    @SerializedName("accountNumber")
    private final String accountNumber;

    @SerializedName("accountName")
    private final String accountName;

    @SerializedName("defaultAccount")
    private final boolean defaultAccount;

    public BankAccountCreateRequest(
            String bankName,
            String accountNumber,
            String accountName,
            boolean defaultAccount
    ) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.defaultAccount = defaultAccount;
    }
}