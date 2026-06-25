package com.fixit.feature.worker.stats.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.stats.data.remote.dto.WorkerStatsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WorkerStatsApi {

    @GET("api/v1/workers/me/stats")
    Call<ApiResponse<WorkerStatsResponse>> getStats(@Query("period") String period);
}
