package com.fixit.feature.worker.wallet.domain.model;

/**
 * Model đại diện cho một giao dịch trong Ví của Thợ.
 *
 * walletType:
 *   "available" → Ví khả dụng
 *   "held"      → Ví tạm giữ
 *   "debt"      → Ví ghi nợ
 *
 * amountSign:
 *   true  → Cộng tiền (+)
 *   false → Trừ tiền (-)
 */
public class WalletTransaction {

    private String title;       // VD: "Nhận tiền đơn ORD001"
    private String date;        // VD: "08/05/2026 - 14:30"
    private String amount;      // VD: "500.000 đ"
    private boolean isCredit;   // true = cộng (+), false = trừ (-)
    private String walletType;  // "available" | "held" | "debt"

    public WalletTransaction(String title, String date, String amount,
                              boolean isCredit, String walletType) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.isCredit = isCredit;
        this.walletType = walletType;
    }

    public String getTitle()      { return title; }
    public String getDate()       { return date; }
    public String getAmount()     { return amount; }
    public boolean isCredit()     { return isCredit; }
    public String getWalletType() { return walletType; }
}
