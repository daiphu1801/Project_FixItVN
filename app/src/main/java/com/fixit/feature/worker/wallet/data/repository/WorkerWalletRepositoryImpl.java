package com.fixit.feature.worker.wallet.data.repository;

import com.fixit.feature.worker.wallet.domain.model.WalletBalance;
import com.fixit.feature.worker.wallet.domain.model.WalletTransaction;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkerWalletRepositoryImpl implements WorkerWalletRepository {
    private static final WalletBalance MOCK_BALANCE =
            new WalletBalance("1.250.000 d", "320.000 d", "75.000 d");

    private static final List<WalletTransaction> ALL_TRANSACTIONS = Arrays.asList(
            new WalletTransaction("Nhan tien don ORD003 (chuyen khoan)",
                    "08/05/2026 - 14:30", "350.000 d", true, "available"),
            new WalletTransaction("Rut tien ve Vietcombank",
                    "07/05/2026 - 09:00", "500.000 d", false, "available"),
            new WalletTransaction("Nhan tien don ORD001",
                    "06/05/2026 - 17:00", "150.000 d", true, "available"),
            new WalletTransaction("Giu bao hanh don ORD003",
                    "08/05/2026 - 14:30", "100.000 d", false, "held"),
            new WalletTransaction("Giai phong bao hanh ORD002",
                    "05/05/2026 - 11:00", "80.000 d", true, "held"),
            new WalletTransaction("Giu bao hanh don ORD001",
                    "06/05/2026 - 17:00", "50.000 d", false, "held"),
            new WalletTransaction("Chiet khau don tien mat ORD004",
                    "07/05/2026 - 16:00", "30.000 d", false, "debt"),
            new WalletTransaction("Nap tien tra no",
                    "06/05/2026 - 08:00", "100.000 d", true, "debt"),
            new WalletTransaction("Chiet khau don tien mat ORD002",
                    "05/05/2026 - 15:30", "45.000 d", false, "debt")
    );

    @Inject
    public WorkerWalletRepositoryImpl() {
    }

    @Override
    public WalletBalance getWalletBalance() {
        return MOCK_BALANCE;
    }

    @Override
    public List<WalletTransaction> getTransactions(String walletType) {
        if ("all".equals(walletType)) {
            return ALL_TRANSACTIONS;
        }
        return ALL_TRANSACTIONS.stream()
                .filter(transaction -> walletType.equals(transaction.getWalletType()))
                .collect(Collectors.toList());
    }
}
