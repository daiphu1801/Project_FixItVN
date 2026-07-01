// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/data/remote/api/WorkerWalletApi.java

package com.fixit.feature.worker.wallet.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.request.DepositCreateRequest;
import com.fixit.feature.worker.wallet.data.remote.dto.request.WithdrawCreateRequest;
import com.fixit.feature.worker.wallet.data.remote.dto.response.DepositQrResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.response.DepositResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.response.WalletTransactionsResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.response.WorkerWalletResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WorkerWalletApi {

    // Lấy số dư 3 ví
    @GET("api/v1/workers/me/wallet")
    Call<ApiResponse<WorkerWalletResponse>> getWallet();

    // Lấy lịch sử giao dịch (phân trang, lọc theo loại)
    // type: null = tất cả | Deposit | Withdraw | Holding | Release | Fee_Deduction
    @GET("api/v1/workers/me/wallet/transactions")
    Call<ApiResponse<WalletTransactionsResponse>> getTransactions(
            @Query("page") int page,
            @Query("size") int size,
            @Query("type") String type
    );

    // Tạo yêu cầu nạp tiền (trả nợ phí)
    @POST("api/v1/workers/me/wallet/deposits")
    Call<ApiResponse<DepositResponse>> createDeposit(
            @Body DepositCreateRequest request
    );

    // Lấy chi tiết giao dịch nạp tiền
    @GET("api/v1/workers/me/wallet/deposits/{transactionId}")
    Call<ApiResponse<DepositResponse>> getDepositDetail(
            @Path("transactionId") String transactionId
    );

    // Lấy mã QR thanh toán của giao dịch nạp tiền
    @GET("api/v1/workers/me/wallet/deposits/{transactionId}/qr")
    Call<ApiResponse<DepositQrResponse>> getDepositQr(
            @Path("transactionId") String transactionId
    );

    // Hủy yêu cầu nạp tiền
    @POST("api/v1/workers/me/wallet/deposits/{transactionId}/cancel")
    Call<ApiResponse<Void>> cancelDeposit(
            @Path("transactionId") String transactionId
    );

    // Tạo yêu cầu rút tiền
    @POST("api/v1/workers/me/wallet/withdrawals")
    Call<ApiResponse<Void>> createWithdraw(
            @Body WithdrawCreateRequest request
    );

    // Hủy yêu cầu rút tiền
    @POST("api/v1/workers/me/wallet/withdrawals/{transactionId}/cancel")
    Call<ApiResponse<Void>> cancelWithdraw(
            @Path("transactionId") String transactionId
    );
}
