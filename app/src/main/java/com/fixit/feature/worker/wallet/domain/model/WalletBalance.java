package com.fixit.feature.worker.wallet.domain.model;

public class WalletBalance {
    private final String availableBalance;
    private final String heldBalance;
    private final String debtBalance;

    public WalletBalance(String availableBalance, String heldBalance, String debtBalance) {
        this.availableBalance = availableBalance;
        this.heldBalance = heldBalance;
        this.debtBalance = debtBalance;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public String getHeldBalance() {
        return heldBalance;
    }

    public String getDebtBalance() {
        return debtBalance;
    }
}
