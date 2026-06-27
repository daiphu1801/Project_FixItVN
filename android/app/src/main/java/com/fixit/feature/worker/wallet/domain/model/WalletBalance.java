package com.fixit.feature.worker.wallet.domain.model;

public class WalletBalance {
    private final String availableBalance;
    private final String heldBalance;
    private final String debtBalance;
    private final String incomeThisWeek;
    private final String incomeThisMonth;

    public WalletBalance(String availableBalance, String heldBalance, String debtBalance, String incomeThisWeek, String incomeThisMonth) {
        this.availableBalance = availableBalance;
        this.heldBalance = heldBalance;
        this.debtBalance = debtBalance;
        this.incomeThisWeek = incomeThisWeek;
        this.incomeThisMonth = incomeThisMonth;
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

    public String getIncomeThisWeek() {
        return incomeThisWeek;
    }

    public String getIncomeThisMonth() {
        return incomeThisMonth;
    }
}
