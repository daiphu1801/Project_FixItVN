// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/data/remote/dto/response/WalletTransactionItemResponse.java

package com.fixit.feature.worker.wallet.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class WalletTransactionItemResponse {

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("bookingId")
    private String bookingId;

    @SerializedName("transactionType")
    private String transactionType; // Deposit | Withdraw | Holding | Release | Fee_Deduction

    @SerializedName("amount")
    private BigDecimal amount;

    @SerializedName("transactionCode")
    private String transactionCode;

    @SerializedName("gatewayReferenceCode")
    private String gatewayReferenceCode;

    @SerializedName("targetBankAccountId")
    private String targetBankAccountId;

    @SerializedName("targetBankName")
    private String targetBankName;

    @SerializedName("targetAccountNumberMasked")
    private String targetAccountNumberMasked;

    @SerializedName("status")
    private String status; // Pending | Success | Failed | Cancelled

    @SerializedName("adminNote")
    private String adminNote;

    @SerializedName("heldReleaseAt")
    private String heldReleaseAt;

    @SerializedName("transactionTime")
    private String transactionTime;

    public String getTransactionId() {
        return transactionId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public String getGatewayReferenceCode() {
        return gatewayReferenceCode;
    }

    public String getTargetBankAccountId() {
        return targetBankAccountId;
    }

    public String getTargetBankName() {
        return targetBankName;
    }

    public String getTargetAccountNumberMasked() {
        return targetAccountNumberMasked;
    }

    public String getStatus() {
        return status;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public String getHeldReleaseAt() {
        return heldReleaseAt;
    }

    public String getTransactionTime() {
        return transactionTime;
    }
}
