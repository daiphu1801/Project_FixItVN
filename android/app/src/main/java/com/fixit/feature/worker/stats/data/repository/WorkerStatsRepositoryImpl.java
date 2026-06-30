package com.fixit.feature.worker.stats.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.stats.data.remote.api.WorkerStatsApi;
import com.fixit.feature.worker.stats.data.remote.dto.WorkerStatsResponse;
import com.fixit.feature.worker.stats.data.remote.mapper.WorkerStatsMapper;
import com.fixit.feature.worker.stats.domain.model.WorkerStats;
import com.fixit.feature.worker.stats.domain.repository.WorkerStatsRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkerStatsRepositoryImpl implements WorkerStatsRepository {

    private final WorkerStatsApi workerStatsApi;

    @Inject
    public WorkerStatsRepositoryImpl(WorkerStatsApi workerStatsApi) {
        this.workerStatsApi = workerStatsApi;
    }

    @Override
    public void getStats(String period, ResultCallback<WorkerStats> callback) {
        workerStatsApi.getStats(period).enqueue(new Callback<ApiResponse<WorkerStatsResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<WorkerStatsResponse>> call,
                    Response<ApiResponse<WorkerStatsResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    callback.onResult(Result.error(
                            new AppError("Không tải được dữ liệu thống kê. HTTP " + response.code())
                    ));
                    return;
                }

                ApiResponse<WorkerStatsResponse> body = response.body();
                if (body == null) {
                    callback.onResult(Result.error(
                            new AppError("Không tải được dữ liệu thống kê: response rỗng")
                    ));
                    return;
                }

                if (!body.isSuccess()) {
                    callback.onResult(Result.error(
                            new AppError(body.getMessage() != null
                                    ? body.getMessage()
                                    : "Không tải được dữ liệu thống kê")
                    ));
                    return;
                }

                WorkerStats stats = WorkerStatsMapper.toDomain(body.getData());
                if (stats == null) {
                    callback.onResult(Result.error(
                            new AppError("Dữ liệu thống kê không hợp lệ")
                    ));
                    return;
                }

                callback.onResult(Result.success(stats));
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerStatsResponse>> call, Throwable t) {
                callback.onResult(Result.error(
                        new AppError("Lỗi kết nối API workers/me/stats: " + t.getMessage(), t)
                ));
            }
        });
    }
}
