package com.fixit.feature.worker.wallet.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

public class BankAccountResponse {

    @SerializedName("bankAccountId")
    private String bankAccountId;

    @SerializedName("bankName")
    private String bankName;

    @SerializedName("accountName")
    private String accountName;

    @SerializedName("accountNumberMasked")
    private String accountNumberMasked;

    @SerializedName("defaultAccount")
    private Boolean defaultAccount;

    public String getBankAccountId() {
        return bankAccountId;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumberMasked() {
        return accountNumberMasked;
    }

    public Boolean getDefaultAccount() {
        return defaultAccount;
    }
}