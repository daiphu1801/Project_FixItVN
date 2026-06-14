// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/data/repository/WorkerWalletRepositoryImpl.java

package com.fixit.feature.worker.wallet.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.core.ui.ViewUtils;
import com.fixit.feature.worker.wallet.data.remote.api.WorkerWalletApi;
import com.fixit.feature.worker.wallet.data.remote.dto.request.DepositCreateRequest;
import com.fixit.feature.worker.wallet.data.remote.dto.response.DepositQrResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.response.DepositResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.response.WalletTransactionItemResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.response.WalletTransactionsResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.response.WorkerWalletResponse;
import com.fixit.feature.worker.wallet.domain.model.WalletBalance;
import com.fixit.feature.worker.wallet.domain.model.WalletTransaction;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkerWalletRepositoryImpl implements WorkerWalletRepository {

    private final WorkerWalletApi api;

    @Inject
    public WorkerWalletRepositoryImpl(WorkerWalletApi api) {
        this.api = api;
    }

    // ─────────────────────────────────────────────
    // Số dư ví
    // ─────────────────────────────────────────────

    @Override
    public void getWalletBalance(ResultCallback<WalletBalance> callback) {
        api.getWallet().enqueue(new Callback<ApiResponse<WorkerWalletResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<WorkerWalletResponse>> call,
                    Response<ApiResponse<WorkerWalletResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    ApiResponse<?> errorResponse = ApiResponse.parseError(response);
                    String errorMsg = (errorResponse != null && errorResponse.getMessage() != null)
                            ? errorResponse.getMessage()
                            : "Không tải được số dư ví. HTTP " + response.code();
                    callback.onResult(Result.error(new AppError(errorMsg)));
                    return;
                }

                ApiResponse<WorkerWalletResponse> body = response.body();
                if (body == null || !body.isSuccess() || body.getData() == null) {
                    callback.onResult(Result.error(new AppError(
                            body != null && body.getMessage() != null
                                    ? body.getMessage()
                                    : "Dữ liệu ví trống"
                    )));
                    return;
                }

                WorkerWalletResponse data = body.getData();
                WalletBalance balance = new WalletBalance(
                        formatBalance(data.getAvailableBalance()),
                        formatBalance(data.getHeldBalance()),
                        formatBalance(data.getDebtBalance()),
                        formatBalance(data.getIncomeThisWeek()),
                        formatBalance(data.getIncomeThisMonth())
                );
                callback.onResult(Result.success(balance));
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerWalletResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(
                        "Lỗi kết nối khi tải ví: " + t.getMessage(), t
                )));
            }
        });
    }

    // ─────────────────────────────────────────────
    // Lịch sử giao dịch
    // ─────────────────────────────────────────────

    @Override
    public void getTransactions(String walletType, ResultCallback<List<WalletTransaction>> callback) {
        // Ánh xạ walletType của UI sang transactionType của API
        String apiType = mapWalletTypeToApiType(walletType);

        api.getTransactions(0, 50, apiType).enqueue(new Callback<ApiResponse<WalletTransactionsResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<WalletTransactionsResponse>> call,
                    Response<ApiResponse<WalletTransactionsResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    ApiResponse<?> errorResponse = ApiResponse.parseError(response);
                    String errorMsg = (errorResponse != null && errorResponse.getMessage() != null)
                            ? errorResponse.getMessage()
                            : "Không tải được lịch sử giao dịch. HTTP " + response.code();
                    callback.onResult(Result.error(new AppError(errorMsg)));
                    return;
                }

                ApiResponse<WalletTransactionsResponse> body = response.body();
                if (body == null || !body.isSuccess()) {
                    callback.onResult(Result.error(new AppError(
                            body != null && body.getMessage() != null
                                    ? body.getMessage()
                                    : "Danh sách giao dịch trống"
                    )));
                    return;
                }

                WalletTransactionsResponse data = body.getData();
                List<WalletTransaction> result = mapTransactions(
                        data != null ? data.getTransactions() : null,
                        walletType
                );
                callback.onResult(Result.success(result));
            }

            @Override
            public void onFailure(Call<ApiResponse<WalletTransactionsResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(
                        "Lỗi kết nối khi tải giao dịch: " + t.getMessage(), t
                )));
            }
        });
    }

    // ─────────────────────────────────────────────
    // Nạp tiền
    // ─────────────────────────────────────────────

    @Override
    public void createDeposit(long amount, ResultCallback<DepositResponse> callback) {
        DepositCreateRequest request = new DepositCreateRequest(BigDecimal.valueOf(amount));

        api.createDeposit(request).enqueue(new Callback<ApiResponse<DepositResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<DepositResponse>> call,
                    Response<ApiResponse<DepositResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    ApiResponse<?> errorResponse = ApiResponse.parseError(response);
                    String errorMsg = (errorResponse != null && errorResponse.getMessage() != null)
                            ? errorResponse.getMessage()
                            : "Tạo lệnh nạp tiền thất bại. HTTP " + response.code();
                    callback.onResult(Result.error(new AppError(errorMsg)));
                    return;
                }

                ApiResponse<DepositResponse> body = response.body();
                if (body == null || !body.isSuccess() || body.getData() == null) {
                    callback.onResult(Result.error(new AppError(
                            body != null && body.getMessage() != null
                                    ? body.getMessage()
                                    : "Tạo lệnh nạp tiền thất bại"
                    )));
                    return;
                }

                callback.onResult(Result.success(body.getData()));
            }

            @Override
            public void onFailure(Call<ApiResponse<DepositResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(
                        "Lỗi kết nối khi nạp tiền: " + t.getMessage(), t
                )));
            }
        });
    }

    @Override
    public void getDepositQr(String transactionId, ResultCallback<DepositQrResponse> callback) {
        api.getDepositQr(transactionId).enqueue(new Callback<ApiResponse<DepositQrResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<DepositQrResponse>> call,
                    Response<ApiResponse<DepositQrResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    ApiResponse<?> errorResponse = ApiResponse.parseError(response);
                    String errorMsg = (errorResponse != null && errorResponse.getMessage() != null)
                            ? errorResponse.getMessage()
                            : "Lấy mã QR thất bại. HTTP " + response.code();
                    callback.onResult(Result.error(new AppError(errorMsg)));
                    return;
                }

                ApiResponse<DepositQrResponse> body = response.body();
                if (body == null || !body.isSuccess() || body.getData() == null) {
                    callback.onResult(Result.error(new AppError(
                            body != null && body.getMessage() != null
                                    ? body.getMessage()
                                    : "Dữ liệu QR trống"
                    )));
                    return;
                }

                callback.onResult(Result.success(body.getData()));
            }

            @Override
            public void onFailure(Call<ApiResponse<DepositQrResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(
                        "Lỗi kết nối khi lấy QR: " + t.getMessage(), t
                )));
            }
        });
    }

    // ─────────────────────────────────────────────
    // Rút tiền (stub — chờ backend bổ sung endpoint)
    // ─────────────────────────────────────────────

    @Override
    public void createWithdrawal(long amount, String bankAccountId, ResultCallback<Void> callback) {
        // TODO: Gọi API khi backend có endpoint rút tiền
        callback.onResult(Result.success(null));
    }

    @Override
    public void cancelWithdrawal(String transactionId, ResultCallback<Void> callback) {
        // TODO: Gọi API khi backend có endpoint hủy rút tiền
        callback.onResult(Result.success(null));
    }

    @Override
    public void getDepositDetail(String transactionId, ResultCallback<DepositResponse> callback) {
        api.getDepositDetail(transactionId).enqueue(new Callback<ApiResponse<DepositResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<DepositResponse>> call,
                    Response<ApiResponse<DepositResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    ApiResponse<?> errorResponse = ApiResponse.parseError(response);
                    String errorMsg = (errorResponse != null && errorResponse.getMessage() != null)
                            ? errorResponse.getMessage()
                            : "Không tải được chi tiết nạp tiền. HTTP " + response.code();
                    callback.onResult(Result.error(new AppError(errorMsg)));
                    return;
                }

                ApiResponse<DepositResponse> body = response.body();
                if (body == null || !body.isSuccess() || body.getData() == null) {
                    callback.onResult(Result.error(new AppError(
                            body != null && body.getMessage() != null
                                    ? body.getMessage()
                                    : "Dữ liệu giao dịch nạp tiền trống"
                    )));
                    return;
                }

                callback.onResult(Result.success(body.getData()));
            }

            @Override
            public void onFailure(Call<ApiResponse<DepositResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(
                        "Lỗi kết nối khi tải chi tiết nạp tiền: " + t.getMessage(), t
                )));
            }
        });
    }

    // ─────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────

    private String formatBalance(BigDecimal value) {
        if (value == null) return ViewUtils.formatCurrency(0);
        return ViewUtils.formatCurrency(value.longValue());
    }

    /**
     * UI dùng 3 tab: "available", "held", "debt"
     * Backend nhận: Deposit | Withdraw | Holding | Release | Fee_Deduction | null (all)
     *
     * Mapping:
     *   "available" → null (lấy all rồi filter phía client)
     *   "held"      → "Holding"
     *   "debt"      → "Fee_Deduction"
     *   null/"all"  → null
     */
    private String mapWalletTypeToApiType(String walletType) {
        if (walletType == null) return null;
        switch (walletType) {
            case "held":  return "Holding";
            case "debt":  return "Fee_Deduction";
            default:      return null; // "available" và "all" lấy all rồi filter client-side
        }
    }

    /**
     * Ánh xạ response DTO → domain model WalletTransaction
     * isCredit (cộng tiền): Deposit, Release → true
     * isDebit  (trừ tiền):  Withdraw, Holding, Fee_Deduction → false
     */
    private List<WalletTransaction> mapTransactions(
            List<WalletTransactionItemResponse> items,
            String walletType
    ) {
        List<WalletTransaction> result = new ArrayList<>();
        if (items == null) return result;

        for (WalletTransactionItemResponse item : items) {
            String type = item.getTransactionType();

            // Xác định walletType hiển thị
            String resolvedWalletType = resolveWalletType(type);

            // Nếu đang filter tab "available", bỏ qua các loại không thuộc nhóm này
            if ("available".equals(walletType)) {
                if (!"available".equals(resolvedWalletType)) continue;
            }

            boolean isCredit = "Deposit".equals(type) || "Release".equals(type);

            String label = buildLabel(type, item);
            String date = formatDate(item.getTransactionTime());
            String amountStr = formatBalance(item.getAmount());

            result.add(new WalletTransaction(
                    item.getTransactionId(),
                    label,
                    date,
                    amountStr,
                    isCredit,
                    resolvedWalletType,
                    item.getStatus() != null ? item.getStatus() : "Success",
                    item.getBookingId()
            ));
        }
        return result;
    }

    private String resolveWalletType(String transactionType) {
        if (transactionType == null) return "available";
        switch (transactionType) {
            case "Holding":       return "held";
            case "Fee_Deduction": return "debt";
            default:              return "available"; // Deposit, Withdraw, Release
        }
    }

    private String buildLabel(String type, WalletTransactionItemResponse item) {
        if (type == null) return "Giao dịch";
        switch (type) {
            case "Deposit":       return "Nạp tiền trả nợ phí";
            case "Withdraw":      return item.getTargetBankName() != null
                    ? "Rút tiền về " + item.getTargetBankName()
                    : "Rút tiền về ngân hàng";
            case "Holding":       return item.getBookingId() != null
                    ? "Tạm giữ bảo hành đơn"
                    : "Tạm giữ bảo hành";
            case "Release":       return item.getBookingId() != null
                    ? "Giải phóng bảo hành đơn"
                    : "Giải phóng bảo hành";
            case "Fee_Deduction": return "Khấu trừ phí nền tảng";
            default:              return type;
        }
    }

    private String formatDate(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isEmpty()) return "";
        try {
            // "2026-05-08T14:30:00+07:00" → "08/05/2026 - 14:30"
            String datePart = isoDateTime.substring(0, 10);   // yyyy-MM-dd
            String timePart = isoDateTime.substring(11, 16);  // HH:mm
            String[] parts = datePart.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0] + " - " + timePart;
        } catch (Exception e) {
            return isoDateTime;
        }
    }
}
