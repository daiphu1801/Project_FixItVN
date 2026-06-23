package com.fixit.feature.worker.job.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.home.data.remote.api.WorkerHomeApi;
import com.fixit.feature.worker.home.data.remote.dto.WorkerHomeResponse;
import com.fixit.feature.worker.job.domain.model.WorkerJobSummary;
import com.fixit.feature.worker.job.domain.repository.WorkerJobRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkerJobRepositoryImpl implements WorkerJobRepository {
    private final WorkerHomeApi workerHomeApi;

    @Inject
    public WorkerJobRepositoryImpl(WorkerHomeApi workerHomeApi) {
        this.workerHomeApi = workerHomeApi;
    }

    @Override
    public void getJobSummary(ResultCallback<WorkerJobSummary> callback) {
        workerHomeApi.getWorkerHome().enqueue(new Callback<ApiResponse<WorkerHomeResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<WorkerHomeResponse>> call, Response<ApiResponse<WorkerHomeResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    WorkerHomeResponse data = response.body().getData();
                    
                    String name = data.getFullName() != null ? data.getFullName() : "";
                    String area = data.getStatusText() != null ? data.getStatusText() : "Chưa xác định";
                    
                    int todayOrders = 0;
                    float rating = 5.0f;
                    if (data.getStatsOverview() != null) {
                        todayOrders = data.getStatsOverview().getCompletedJobsToday() != null 
                                ? data.getStatsOverview().getCompletedJobsToday() : 0;
                        rating = data.getStatsOverview().getAverageRating() != null 
                                ? data.getStatsOverview().getAverageRating().floatValue() : 5.0f;
                    }
                    
                    double debt = 0.0;
                    if (data.getDebtBalance() != null) {
                        debt = data.getDebtBalance().doubleValue();
                    }
                    
                    WorkerJobSummary summary = new WorkerJobSummary(name, area, todayOrders, rating, debt);
                    callback.onResult(Result.success(summary));
                } else {
                    ApiResponse<?> apiResponse = ApiResponse.parseError(response);
                    String msg = (apiResponse != null && apiResponse.getMessage() != null)
                            ? apiResponse.getMessage() : "Lỗi tải thông tin thợ";
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
