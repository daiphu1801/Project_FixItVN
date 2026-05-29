package com.fixit.feature.worker.wallet.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

public class BankAccountUpdateRequest {

    @SerializedName("bankName")
    private final String bankName;

    @SerializedName("accountNumber")
    private final String accountNumber;

    @SerializedName("accountName")
    private final String accountName;

    public BankAccountUpdateRequest(
            String bankName,
            String accountNumber,
            String accountName
    ) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
    }
}