// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/data/remote/dto/response/DepositResponse.java

package com.fixit.feature.worker.wallet.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class DepositResponse {

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("workerId")
    private String workerId;

    @SerializedName("amount")
    private BigDecimal amount;

    @SerializedName("transactionCode")
    private String transactionCode;

    @SerializedName("status")
    private String status; // Pending | Success | Failed | Cancelled

    @SerializedName("transactionType")
    private String transactionType;

    @SerializedName("debtBefore")
    private BigDecimal debtBefore;

    @SerializedName("debtPaidAmount")
    private BigDecimal debtPaidAmount;

    @SerializedName("surplusToAvailable")
    private BigDecimal surplusToAvailable;

    @SerializedName("transactionTime")
    private String transactionTime;

    @SerializedName("nextAction")
    private String nextAction; // "SHOW_QR" hoặc "VIEW_RESULT"

    @SerializedName("qr")
    private DepositQrResponse qr;

    public String getTransactionId() {
        return transactionId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public String getStatus() {
        return status;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getDebtBefore() {
        return debtBefore;
    }

    public BigDecimal getDebtPaidAmount() {
        return debtPaidAmount;
    }

    public BigDecimal getSurplusToAvailable() {
        return surplusToAvailable;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public String getNextAction() {
        return nextAction;
    }

    public DepositQrResponse getQr() {
        return qr;
    }
}
