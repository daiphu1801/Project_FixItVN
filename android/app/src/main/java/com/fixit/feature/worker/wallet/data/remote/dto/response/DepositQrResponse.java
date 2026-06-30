// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/data/remote/dto/response/DepositQrResponse.java

package com.fixit.feature.worker.wallet.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class DepositQrResponse {

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("amount")
    private BigDecimal amount;

    @SerializedName("transactionCode")
    private String transactionCode;

    @SerializedName("bankName")
    private String bankName;

    @SerializedName("bankCode")
    private String bankCode;

    @SerializedName("accountNumber")
    private String accountNumber;

    @SerializedName("accountName")
    private String accountName;

    @SerializedName("transferContent")
    private String transferContent;

    @SerializedName("qrUrl")
    private String qrUrl;

    public String getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getTransferContent() {
        return transferContent;
    }

    public String getQrUrl() {
        return qrUrl;
    }
}
