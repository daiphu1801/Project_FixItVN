package com.fixit.feature.worker.wallet.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class WithdrawCreateRequest {
    @SerializedName("amount")
    private final BigDecimal amount;

    @SerializedName("targetBankAccountId")
    private final String targetBankAccountId;

    public WithdrawCreateRequest(BigDecimal amount, String targetBankAccountId) {
        this.amount = amount;
        this.targetBankAccountId = targetBankAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTargetBankAccountId() {
        return targetBankAccountId;
    }
}
