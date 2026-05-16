package com.fixit.feature.worker.wallet.domain.repository;

import com.fixit.feature.worker.wallet.domain.model.WalletBalance;
import com.fixit.feature.worker.wallet.domain.model.WalletTransaction;

import java.util.List;

public interface WorkerWalletRepository {
    WalletBalance getWalletBalance();

    List<WalletTransaction> getTransactions(String walletType);
}
