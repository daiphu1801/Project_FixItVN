package com.fixit.feature.worker.wallet.data.repository;

import com.fixit.feature.worker.wallet.domain.model.WalletBalance;
import com.fixit.feature.worker.wallet.domain.model.WalletTransaction;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkerWalletRepositoryImpl implements WorkerWalletRepository {
    private long availableAmount = 1250000;
    private long heldAmount = 320000;
    private long debtAmount = 75000;

    private final List<WalletTransaction> transactions = new ArrayList<>();

    @Inject
    public WorkerWalletRepositoryImpl() {
        // Khởi tạo các giao dịch mẫu ban đầu
        transactions.add(new WalletTransaction("TX001", "Nhận tiền đơn ORD003 (chuyển khoản)",
                "08/05/2026 - 14:30", "350.000 đ", true, "available", "SUCCESS"));
        transactions.add(new WalletTransaction("TX002", "Rút tiền về Vietcombank",
                "07/05/2026 - 09:00", "500.000 đ", false, "available", "SUCCESS"));
        transactions.add(new WalletTransaction("TX003", "Nhận tiền đơn ORD001",
                "06/05/2026 - 17:00", "150.000 đ", true, "available", "SUCCESS"));
        transactions.add(new WalletTransaction("TX004", "Giữ bảo hành đơn ORD003",
                "08/05/2026 - 14:30", "100.000 đ", false, "held", "SUCCESS"));
        transactions.add(new WalletTransaction("TX005", "Giải phóng bảo hành ORD002",
                "05/05/2026 - 11:00", "80.000 đ", true, "held", "SUCCESS"));
        transactions.add(new WalletTransaction("TX006", "Giữ bảo hành đơn ORD001",
                "06/05/2026 - 17:00", "50.000 đ", false, "held", "SUCCESS"));
        transactions.add(new WalletTransaction("TX007", "Chiết khấu đơn tiền mặt ORD004",
                "07/05/2026 - 16:00", "30.000 đ", false, "debt", "SUCCESS"));
        transactions.add(new WalletTransaction("TX008", "Nạp tiền trả nợ",
                "06/05/2026 - 08:00", "100.000 đ", true, "debt", "SUCCESS"));
        transactions.add(new WalletTransaction("TX009", "Chiết khấu đơn tiền mặt ORD002",
                "05/05/2026 - 15:30", "45.000 đ", false, "debt", "SUCCESS"));
    }

    private String formatVND(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat format = new DecimalFormat("#,###", symbols);
        return format.format(amount) + " đ";
    }

    @Override
    public WalletBalance getWalletBalance() {
        return new WalletBalance(formatVND(availableAmount), formatVND(heldAmount), formatVND(debtAmount));
    }

    @Override
    public List<WalletTransaction> getTransactions(String walletType) {
        if ("all".equals(walletType)) {
            return transactions;
        }
        return transactions.stream()
                .filter(transaction -> walletType.equals(transaction.getWalletType()))
                .collect(Collectors.toList());
    }

    @Override
    public String createDeposit(long amount, String note) {
        String txId = "DEP" + (System.currentTimeMillis() % 100000);
        String dateStr = new SimpleDateFormat("dd/05/2026 - HH:mm", Locale.getDefault()).format(new Date());

        transactions.add(0, new WalletTransaction(txId, "Nạp tiền ví (" + note + ")",
                dateStr, formatVND(amount), true, "debt", "PENDING"));

        return txId;
    }

    @Override
    public String getDepositQr(String transactionId) {
        WalletTransaction target = null;
        for (WalletTransaction tx : transactions) {
            if (tx.getId().equals(transactionId)) {
                target = tx;
                break;
            }
        }
        long amount = 75000;
        if (target != null) {
            try {
                String amtStr = target.getAmount().replace(" đ", "").replace(".", "").trim();
                amount = Long.parseLong(amtStr);
            } catch (Exception ignored) {}
        }

        return "https://img.vietqr.io/image/MB-9704229999999-qr_only.png?amount=" + amount + "&addInfo=FIXIT_" + transactionId;
    }

    @Override
    public void createWithdrawal(long amount, String bankAccountId) {
        if (amount > availableAmount) return;

        availableAmount -= amount;
        String txId = "WDR" + (System.currentTimeMillis() % 100000);
        String dateStr = new SimpleDateFormat("dd/05/2026 - HH:mm", Locale.getDefault()).format(new Date());

        transactions.add(0, new WalletTransaction(txId, "Rút tiền về ngân hàng",
                dateStr, formatVND(amount), false, "available", "PENDING"));
    }

    @Override
    public void cancelWithdrawal(String transactionId) {
        for (WalletTransaction tx : transactions) {
            if (tx.getId().equals(transactionId) && "PENDING".equals(tx.getStatus())) {
                tx.setStatus("CANCELLED");
                try {
                    String amtStr = tx.getAmount().replace(" đ", "").replace(".", "").trim();
                    long amount = Long.parseLong(amtStr);
                    availableAmount += amount;
                } catch (Exception ignored) {}
                break;
            }
        }
    }

    @Override
    public void simulateDepositSuccess(String transactionId) {
        for (WalletTransaction tx : transactions) {
            if (tx.getId().equals(transactionId) && "PENDING".equals(tx.getStatus())) {
                tx.setStatus("SUCCESS");
                try {
                    String amtStr = tx.getAmount().replace(" đ", "").replace(".", "").trim();
                    long amount = Long.parseLong(amtStr);
                    debtAmount = Math.max(0, debtAmount - amount);
                } catch (Exception ignored) {}
                break;
            }
        }
    }
}
