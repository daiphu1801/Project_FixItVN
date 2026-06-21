// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/data/remote/dto/response/WorkerWalletResponse.java

package com.fixit.feature.worker.wallet.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class WorkerWalletResponse {

    @SerializedName("workerId")
    private String workerId;

    @SerializedName("availableBalance")
    private BigDecimal availableBalance;

    @SerializedName("heldBalance")
    private BigDecimal heldBalance;

    @SerializedName("debtBalance")
    private BigDecimal debtBalance;

    @SerializedName("totalBalance")
    private BigDecimal totalBalance;

    @SerializedName("canWithdraw")
    private Boolean canWithdraw;

    @SerializedName("incomeThisWeek")
    private BigDecimal incomeThisWeek;

    @SerializedName("incomeThisMonth")
    private BigDecimal incomeThisMonth;

    public String getWorkerId() {
        return workerId;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getHeldBalance() {
        return heldBalance;
    }

    public BigDecimal getDebtBalance() {
        return debtBalance;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public Boolean getCanWithdraw() {
        return canWithdraw;
    }

    public BigDecimal getIncomeThisWeek() {
        return incomeThisWeek;
    }

    public BigDecimal getIncomeThisMonth() {
        return incomeThisMonth;
    }
}
