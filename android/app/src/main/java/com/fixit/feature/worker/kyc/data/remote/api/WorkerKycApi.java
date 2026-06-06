package com.fixit.feature.worker.kyc.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.upload.data.remote.dto.request.WorkerKycSubmitRequest;
import com.fixit.feature.upload.data.remote.dto.response.WorkerKycResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface WorkerKycApi {

    @POST("api/v1/workers/me/kyc")
    Call<ApiResponse<WorkerKycResponse>> submitKyc(
            @Body WorkerKycSubmitRequest request
    );

    @GET("api/v1/workers/me/kyc/status")
    Call<ApiResponse<WorkerKycResponse>> getKycStatus();
}
