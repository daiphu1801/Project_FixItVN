package com.fixit.feature.worker.wallet.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.request.BankAccountCreateRequest;
import com.fixit.feature.worker.wallet.data.remote.dto.response.BankAccountResponse;
import com.fixit.feature.worker.wallet.data.remote.dto.request.BankAccountUpdateRequest;
import com.fixit.feature.worker.wallet.data.remote.dto.response.BankAccountsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WorkerBankAccountApi {

    @GET("api/v1/workers/me/bank-accounts")
    Call<ApiResponse<BankAccountsResponse>> getBankAccounts();

    @POST("api/v1/workers/me/bank-accounts")
    Call<ApiResponse<BankAccountResponse>> createBankAccount(
            @Body BankAccountCreateRequest request
    );

    @PATCH("api/v1/workers/me/bank-accounts/{bankAccountId}")
    Call<ApiResponse<BankAccountResponse>> updateBankAccount(
            @Path("bankAccountId") String bankAccountId,
            @Body BankAccountUpdateRequest request
    );

    @DELETE("api/v1/workers/me/bank-accounts/{bankAccountId}")
    Call<ApiResponse<Void>> deleteBankAccount(
            @Path("bankAccountId") String bankAccountId
    );

    @PATCH("api/v1/workers/me/bank-accounts/{bankAccountId}/default")
    Call<ApiResponse<BankAccountResponse>> setDefaultBankAccount(
            @Path("bankAccountId") String bankAccountId
    );
}