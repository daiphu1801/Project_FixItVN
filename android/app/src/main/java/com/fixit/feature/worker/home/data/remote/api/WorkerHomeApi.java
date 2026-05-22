package com.fixit.feature.worker.home.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.home.data.remote.dto.WorkerHomeResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WorkerHomeApi {

    @GET("api/v1/workers/me/home")
    Call<ApiResponse<WorkerHomeResponse>> getWorkerHome();
}