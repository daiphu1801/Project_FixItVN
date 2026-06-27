// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/domain/repository/WorkerWalletRepository.java

package com.fixit.feature.worker.wallet.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.wallet.data.remote.dto.response.DepositQrResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.response.DepositResponse;
import com.fixit.feature.worker.wallet.domain.model.WalletBalance;
import com.fixit.feature.worker.wallet.domain.model.WalletTransaction;

import java.util.List;

public interface WorkerWalletRepository {

    // Lấy số dư 3 ví từ server
    void getWalletBalance(ResultCallback<WalletBalance> callback);

    // Lấy lịch sử giao dịch, lọc theo walletType:
    //   "available" → Deposit, Withdraw, Release
    //   "held"      → Holding
    //   "debt"      → Fee_Deduction, Deposit (pending)
    //   null/"all"  → tất cả
    void getTransactions(String walletType, ResultCallback<List<WalletTransaction>> callback);

    // Tạo yêu cầu nạp tiền → trả về DepositResponse (có kèm QR nếu status=Pending)
    void createDeposit(long amount, ResultCallback<DepositResponse> callback);

    // Lấy QR thanh toán theo transactionId
    void getDepositQr(String transactionId, ResultCallback<DepositQrResponse> callback);

    // Rút tiền về ngân hàng (tạm thời giữ callback stub nếu backend chưa có)
    void createWithdrawal(long amount, String bankAccountId, ResultCallback<Void> callback);

    // Hủy yêu cầu rút tiền
    void cancelWithdrawal(String transactionId, ResultCallback<Void> callback);

    // Lấy chi tiết giao dịch nạp tiền để polling check trạng thái
    void getDepositDetail(String transactionId, ResultCallback<DepositResponse> callback);

    // Hủy yêu cầu nạp tiền
    void cancelDeposit(String transactionId, ResultCallback<Void> callback);
}
