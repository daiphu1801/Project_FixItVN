// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/data/remote/dto/request/DepositCreateRequest.java

package com.fixit.feature.worker.wallet.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class DepositCreateRequest {

    @SerializedName("amount")
    private BigDecimal amount;

    public DepositCreateRequest(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() { return amount; }
}
