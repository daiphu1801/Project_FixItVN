package com.fixit.feature.worker.availability.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.availability.domain.repository.WorkerAvailabilityRepository;
import com.fixit.feature.worker.home.data.remote.api.WorkerHomeApi;
import com.fixit.feature.worker.home.data.remote.dto.WorkerHomeResponse;
import com.fixit.feature.worker.home.data.remote.dto.request.WorkerStatusUpdateRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkerAvailabilityRepositoryImpl implements WorkerAvailabilityRepository {
    private final WorkerHomeApi workerHomeApi;
    private boolean cachedOnlineState = false;

    @Inject
    public WorkerAvailabilityRepositoryImpl(WorkerHomeApi workerHomeApi) {
        this.workerHomeApi = workerHomeApi;
    }

    @Override
    public void isOnline(ResultCallback<Boolean> callback) {
        workerHomeApi.getWorkerHome().enqueue(new Callback<ApiResponse<WorkerHomeResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<WorkerHomeResponse>> call, Response<ApiResponse<WorkerHomeResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Boolean available = response.body().getData().getAvailable();
                    cachedOnlineState = available != null && available;
                    callback.onResult(Result.success(cachedOnlineState));
                } else {
                    callback.onResult(Result.success(cachedOnlineState)); // fallback to cached
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerHomeResponse>> call, Throwable t) {
                callback.onResult(Result.success(cachedOnlineState)); // fallback to cached
            }
        });
    }

    @Override
    public void setOnline(boolean online, ResultCallback<Boolean> callback) {
        workerHomeApi.updateStatus(new WorkerStatusUpdateRequest(online)).enqueue(new Callback<ApiResponse<WorkerHomeResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<WorkerHomeResponse>> call, Response<ApiResponse<WorkerHomeResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Boolean available = response.body().getData().getAvailable();
                    cachedOnlineState = available != null && available;
                    callback.onResult(Result.success(cachedOnlineState));
                } else {
                    ApiResponse<?> apiResponse = ApiResponse.parseError(response);
                    String msg = (apiResponse != null && apiResponse.getMessage() != null)
                            ? apiResponse.getMessage() : "Lỗi cập nhật trạng thái";
                    callback.onResult(Result.error(new AppError(msg)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerHomeResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
            }
        });
    }
}
