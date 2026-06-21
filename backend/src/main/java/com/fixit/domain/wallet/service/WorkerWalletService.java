// PATH: backend/src/main/java/com/fixit/domain/wallet/service/WorkerWalletService.java

package com.fixit.domain.wallet.service;

import com.fixit.domain.wallet.dto.request.DepositCreateRequest;
import com.fixit.domain.wallet.dto.request.SepayWebhookRequest;
import com.fixit.domain.wallet.dto.response.DepositQrResponse;
import com.fixit.domain.wallet.dto.response.DepositResponse;
import com.fixit.domain.wallet.dto.response.WorkerWalletResponse;
import com.fixit.domain.wallet.dto.response.WorkerWalletTransactionsResponse;

public interface WorkerWalletService {

    WorkerWalletResponse getMyWallet();

    WorkerWalletTransactionsResponse getMyTransactions(int page, int size, String type);

    DepositResponse createMyDeposit(DepositCreateRequest request);

    DepositResponse getMyDepositDetail(java.util.UUID transactionId);

    DepositQrResponse getMyDepositQr(java.util.UUID transactionId);

    // ← THÊM MỚI: Webhook gọi hàm này khi ngân hàng xác nhận tiền vào
    void processDepositWebhook(SepayWebhookRequest request);

    void cancelMyDeposit(java.util.UUID transactionId);
}
