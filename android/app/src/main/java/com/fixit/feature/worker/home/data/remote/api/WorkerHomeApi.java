package com.fixit.feature.worker.home.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.home.data.remote.dto.WorkerHomeResponse;
import com.fixit.feature.worker.home.data.remote.dto.request.WorkerStatusUpdateRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;

public interface WorkerHomeApi {

    @GET("api/v1/workers/me/home")
    Call<ApiResponse<WorkerHomeResponse>> getWorkerHome();

    @PATCH("api/v1/workers/me/status")
    Call<ApiResponse<WorkerHomeResponse>> updateStatus(@Body WorkerStatusUpdateRequest request);
}