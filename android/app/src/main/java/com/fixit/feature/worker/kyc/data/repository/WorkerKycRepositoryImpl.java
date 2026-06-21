package com.fixit.feature.worker.kyc.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.upload.data.remote.dto.request.WorkerKycSubmitRequest;
import com.fixit.feature.upload.data.remote.dto.response.WorkerKycResponse;
import com.fixit.feature.worker.kyc.data.remote.api.WorkerKycApi;
import com.fixit.feature.worker.kyc.data.remote.dto.response.VnptKycConfigResponse;
import com.fixit.feature.worker.kyc.data.remote.mapper.WorkerKycMapper;
import com.fixit.feature.worker.kyc.domain.model.WorkerKyc;
import com.fixit.feature.worker.kyc.domain.repository.WorkerKycRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkerKycRepositoryImpl implements WorkerKycRepository {

    private final WorkerKycApi workerKycApi;

    @Inject
    public WorkerKycRepositoryImpl(WorkerKycApi workerKycApi) {
        this.workerKycApi = workerKycApi;
    }

    @Override
    public void getKycConfig(ResultCallback<VnptKycConfigResponse> callback) {
        workerKycApi.getKycConfig().enqueue(new Callback<ApiResponse<VnptKycConfigResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<VnptKycConfigResponse>> call,
                    Response<ApiResponse<VnptKycConfigResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    callback.onResult(Result.error(
                            new AppError("Không tải được cấu hình eKYC VNPT. HTTP " + response.code())
                    ));
                    return;
                }

                ApiResponse<VnptKycConfigResponse> body = response.body();
                if (body == null || !body.isSuccess() || body.getData() == null) {
                    callback.onResult(Result.error(
                            new AppError(body != null && body.getMessage() != null
                                    ? body.getMessage()
                                    : "Không tải được cấu hình eKYC VNPT")
                    ));
                    return;
                }

                callback.onResult(Result.success(body.getData()));
            }

            @Override
            public void onFailure(Call<ApiResponse<VnptKycConfigResponse>> call, Throwable t) {
                callback.onResult(Result.error(
                        new AppError("Lỗi kết nối khi tải cấu hình eKYC: " + t.getMessage(), t)
                ));
            }
        });
    }

    @Override
    public void getKycStatus(ResultCallback<WorkerKyc> callback) {
        workerKycApi.getKycStatus().enqueue(new Callback<ApiResponse<WorkerKycResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<WorkerKycResponse>> call,
                    Response<ApiResponse<WorkerKycResponse>> response
            ) {
                // Trả về trạng thái chưa xác minh nếu backend trả 404 (chưa gửi KYC bao giờ)
                if (response.code() == 404) {
                    callback.onResult(Result.success(new WorkerKyc(null, "UNVERIFIED", null, null, null, null, null, null, null)));
                    return;
                }

                if (!response.isSuccessful()) {
                    callback.onResult(Result.error(
                            new AppError("Không tải được trạng thái KYC. HTTP " + response.code())
                    ));
                    return;
                }

                ApiResponse<WorkerKycResponse> body = response.body();
                if (body == null) {
                    callback.onResult(Result.error(
                            new AppError("Không tải được trạng thái KYC: response rỗng")
                    ));
                    return;
                }

                if (!body.isSuccess()) {
                    callback.onResult(Result.error(
                            new AppError(body.getMessage() != null
                                    ? body.getMessage()
                                    : "Không tải được trạng thái KYC")
                    ));
                    return;
                }

                WorkerKyc kyc = WorkerKycMapper.toDomain(body.getData());
                if (kyc == null) {
                    callback.onResult(Result.error(
                            new AppError("Dữ liệu KYC không hợp lệ")
                    ));
                    return;
                }

                callback.onResult(Result.success(kyc));
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerKycResponse>> call, Throwable t) {
                callback.onResult(Result.error(
                        new AppError("Lỗi kết nối API KYC: " + t.getMessage(), t)
                ));
            }
        });
    }

    @Override
    public void submitKyc(WorkerKycSubmitRequest request, ResultCallback<WorkerKyc> callback) {
        workerKycApi.submitKyc(request).enqueue(new Callback<ApiResponse<WorkerKycResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<WorkerKycResponse>> call,
                    Response<ApiResponse<WorkerKycResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    callback.onResult(Result.error(
                            new AppError("Gửi yêu cầu KYC thất bại. HTTP " + response.code())
                    ));
                    return;
                }

                ApiResponse<WorkerKycResponse> body = response.body();
                if (body == null) {
                    callback.onResult(Result.error(
                            new AppError("Gửi yêu cầu KYC thất bại: response rỗng")
                    ));
                    return;
                }

                if (!body.isSuccess()) {
                    callback.onResult(Result.error(
                            new AppError(body.getMessage() != null
                                    ? body.getMessage()
                                    : "Gửi yêu cầu KYC thất bại")
                    ));
                    return;
                }

                WorkerKyc kyc = WorkerKycMapper.toDomain(body.getData());
                if (kyc == null) {
                    callback.onResult(Result.error(
                            new AppError("Dữ liệu KYC sau khi gửi không hợp lệ")
                    ));
                    return;
                }

                callback.onResult(Result.success(kyc));
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerKycResponse>> call, Throwable t) {
                callback.onResult(Result.error(
                        new AppError("Lỗi kết nối khi gửi yêu cầu KYC: " + t.getMessage(), t)
                ));
            }
        });
    }
}
