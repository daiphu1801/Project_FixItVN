package com.fixit.feature.worker.home.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.home.data.remote.api.WorkerHomeApi;
import com.fixit.feature.worker.home.data.remote.dto.WorkerHomeResponse;
import com.fixit.feature.worker.home.data.remote.mapper.WorkerHomeMapper;
import com.fixit.feature.worker.home.domain.model.WorkerHome;
import com.fixit.feature.worker.home.domain.repository.WorkerHomeRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkerHomeRepositoryImpl implements WorkerHomeRepository {

    private final WorkerHomeApi workerHomeApi;

    @Inject
    public WorkerHomeRepositoryImpl(WorkerHomeApi workerHomeApi) {
        this.workerHomeApi = workerHomeApi;
    }

    @Override
    public void getWorkerHome(ResultCallback<WorkerHome> callback) {
        workerHomeApi.getWorkerHome().enqueue(new Callback<ApiResponse<WorkerHomeResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<WorkerHomeResponse>> call,
                    Response<ApiResponse<WorkerHomeResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    callback.onResult(Result.error(
                            new AppError("Không tải được trang chủ thợ. HTTP " + response.code())
                    ));
                    return;
                }

                ApiResponse<WorkerHomeResponse> body = response.body();
                if (body == null) {
                    callback.onResult(Result.error(
                            new AppError("Không tải được trang chủ thợ: response rỗng")
                    ));
                    return;
                }

                if (!body.isSuccess()) {
                    callback.onResult(Result.error(
                            new AppError(body.getMessage() != null
                                    ? body.getMessage()
                                    : "Không tải được trang chủ thợ")
                    ));
                    return;
                }

                WorkerHome home = WorkerHomeMapper.toDomain(body.getData());
                if (home == null) {
                    callback.onResult(Result.error(
                            new AppError("Dữ liệu trang chủ thợ không hợp lệ")
                    ));
                    return;
                }

                callback.onResult(Result.success(home));
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerHomeResponse>> call, Throwable t) {
                callback.onResult(Result.error(
                        new AppError("Lỗi kết nối API workers/me/home: " + t.getMessage(), t)
                ));
            }
        });
    }
}