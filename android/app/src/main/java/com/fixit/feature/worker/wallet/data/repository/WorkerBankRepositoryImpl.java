package com.fixit.feature.worker.wallet.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.wallet.data.remote.api.WorkerBankAccountApi;
import com.fixit.feature.worker.wallet.data.remote.dto.response.BankAccountResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.response.BankAccountsResponse;
import com.fixit.feature.worker.wallet.data.remote.mapper.BankAccountMapper;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;
import com.fixit.feature.worker.wallet.domain.repository.WorkerBankRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkerBankRepositoryImpl implements WorkerBankRepository {

    private final WorkerBankAccountApi api;

    @Inject
    public WorkerBankRepositoryImpl(WorkerBankAccountApi api) {
        this.api = api;
    }

    @Override
    public void getBankAccounts(ResultCallback<List<BankAccount>> callback) {
        api.getBankAccounts().enqueue(new Callback<ApiResponse<BankAccountsResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<BankAccountsResponse>> call,
                    Response<ApiResponse<BankAccountsResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    callback.onResult(Result.error(new AppError(
                            "Không tải được tài khoản ngân hàng. HTTP " + response.code()
                    )));
                    return;
                }

                ApiResponse<BankAccountsResponse> body = response.body();
                if (body == null || !body.isSuccess()) {
                    callback.onResult(Result.error(new AppError(
                            body != null && body.getMessage() != null
                                    ? body.getMessage()
                                    : "Response tài khoản ngân hàng rỗng"
                    )));
                    return;
                }

                BankAccountsResponse data = body.getData();
                List<BankAccount> accounts = BankAccountMapper.toDomainList(
                        data != null ? data.getBankAccounts() : null
                );
                callback.onResult(Result.success(accounts));
            }

            @Override
            public void onFailure(Call<ApiResponse<BankAccountsResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(
                        "Lỗi kết nối API bank-accounts: " + t.getMessage(),
                        t
                )));
            }
        });
    }

    @Override
    public void addBankAccount(BankAccount account, ResultCallback<BankAccount> callback) {
        api.createBankAccount(BankAccountMapper.toCreateRequest(account))
                .enqueue(new SingleBankAccountCallback(callback, "Thêm tài khoản ngân hàng thất bại"));
    }

    @Override
    public void updateBankAccount(BankAccount account, ResultCallback<BankAccount> callback) {
        api.updateBankAccount(account.getId(), BankAccountMapper.toUpdateRequest(account))
                .enqueue(new SingleBankAccountCallback(callback, "Cập nhật tài khoản ngân hàng thất bại"));
    }

    @Override
    public void deleteBankAccount(String id, ResultCallback<Void> callback) {
        api.deleteBankAccount(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<Void>> call,
                    Response<ApiResponse<Void>> response
            ) {
                if (!response.isSuccessful()) {
                    callback.onResult(Result.error(new AppError(
                            "Xóa tài khoản ngân hàng thất bại. HTTP " + response.code()
                    )));
                    return;
                }

                ApiResponse<Void> body = response.body();
                if (body == null || !body.isSuccess()) {
                    callback.onResult(Result.error(new AppError(
                            body != null && body.getMessage() != null
                                    ? body.getMessage()
                                    : "Xóa tài khoản ngân hàng thất bại"
                    )));
                    return;
                }

                callback.onResult(Result.success(null));
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(
                        "Lỗi kết nối API xóa tài khoản ngân hàng: " + t.getMessage(),
                        t
                )));
            }
        });
    }

    @Override
    public void setDefaultBankAccount(String id, ResultCallback<BankAccount> callback) {
        api.setDefaultBankAccount(id)
                .enqueue(new SingleBankAccountCallback(callback, "Đặt tài khoản mặc định thất bại"));
    }

    private static class SingleBankAccountCallback implements Callback<ApiResponse<BankAccountResponse>> {
        private final ResultCallback<BankAccount> callback;
        private final String defaultErrorMessage;

        SingleBankAccountCallback(
                ResultCallback<BankAccount> callback,
                String defaultErrorMessage
        ) {
            this.callback = callback;
            this.defaultErrorMessage = defaultErrorMessage;
        }

        @Override
        public void onResponse(
                Call<ApiResponse<BankAccountResponse>> call,
                Response<ApiResponse<BankAccountResponse>> response
        ) {
            if (!response.isSuccessful()) {
                callback.onResult(Result.error(new AppError(
                        defaultErrorMessage + ". HTTP " + response.code()
                )));
                return;
            }

            ApiResponse<BankAccountResponse> body = response.body();
            if (body == null || !body.isSuccess()) {
                callback.onResult(Result.error(new AppError(
                        body != null && body.getMessage() != null
                                ? body.getMessage()
                                : defaultErrorMessage
                )));
                return;
            }

            BankAccount account = BankAccountMapper.toDomain(body.getData());
            if (account == null) {
                callback.onResult(Result.error(new AppError("Dữ liệu tài khoản ngân hàng không hợp lệ")));
                return;
            }

            callback.onResult(Result.success(account));
        }

        @Override
        public void onFailure(Call<ApiResponse<BankAccountResponse>> call, Throwable t) {
            callback.onResult(Result.error(new AppError(
                    "Lỗi kết nối API bank-account: " + t.getMessage(),
                    t
            )));
        }
    }
}