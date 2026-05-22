package com.fixit.feature.worker.profile.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.profile.data.remote.api.WorkerProfileApi;
import com.fixit.feature.worker.profile.data.remote.dto.response.WorkerProfileResponse;
import com.fixit.feature.worker.profile.data.remote.dto.request.WorkerProfileUpdateRequest;
import com.fixit.feature.worker.profile.data.remote.dto.response.WorkerSkillsResponse;
import com.fixit.feature.worker.profile.data.remote.dto.request.WorkerSkillsUpdateRequest;
import com.fixit.feature.worker.profile.data.remote.mapper.WorkerProfileMapper;
import com.fixit.feature.worker.profile.data.remote.mapper.WorkerSkillMapper;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;
import com.fixit.feature.worker.profile.domain.model.WorkerProfileUpdateInput;
import com.fixit.feature.worker.profile.domain.model.WorkerSkill;
import com.fixit.feature.worker.profile.domain.repository.WorkerProfileRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkerProfileRepositoryImpl implements WorkerProfileRepository {

    private final WorkerProfileApi api;

    @Inject
    public WorkerProfileRepositoryImpl(WorkerProfileApi api) {
        this.api = api;
    }

    @Override
    public void getProfile(ResultCallback<WorkerProfile> callback) {
        api.getProfile().enqueue(new Callback<ApiResponse<WorkerProfileResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<WorkerProfileResponse>> call,
                    Response<ApiResponse<WorkerProfileResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    callback.onResult(Result.error(new AppError(
                            "Không tải được hồ sơ thợ. HTTP " + response.code()
                    )));
                    return;
                }

                ApiResponse<WorkerProfileResponse> body = response.body();
                if (body == null || !body.isSuccess()) {
                    callback.onResult(Result.error(new AppError(
                            body != null ? body.getMessage() : "Response hồ sơ thợ rỗng"
                    )));
                    return;
                }

                WorkerProfile profile = WorkerProfileMapper.toDomain(body.getData());
                callback.onResult(Result.success(profile));
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerProfileResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(
                        "Lỗi kết nối API profile: " + t.getMessage(),
                        t
                )));
            }
        });
    }

    @Override
    public void updateProfile(
            WorkerProfileUpdateInput input,
            ResultCallback<WorkerProfile> callback
    ) {
        WorkerProfileUpdateRequest request = WorkerProfileMapper.toRequest(input);

        api.updateProfile(request).enqueue(new Callback<ApiResponse<WorkerProfileResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<WorkerProfileResponse>> call,
                    Response<ApiResponse<WorkerProfileResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    callback.onResult(Result.error(new AppError(
                            "Cập nhật hồ sơ thất bại. HTTP " + response.code()
                    )));
                    return;
                }

                ApiResponse<WorkerProfileResponse> body = response.body();
                if (body == null || !body.isSuccess()) {
                    callback.onResult(Result.error(new AppError(
                            body != null ? body.getMessage() : "Response cập nhật hồ sơ rỗng"
                    )));
                    return;
                }

                WorkerProfile profile = WorkerProfileMapper.toDomain(body.getData());
                callback.onResult(Result.success(profile));
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerProfileResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(
                        "Lỗi kết nối API cập nhật profile: " + t.getMessage(),
                        t
                )));
            }
        });
    }

    @Override
    public void getSkills(ResultCallback<List<WorkerSkill>> callback) {
        api.getSkills().enqueue(new Callback<ApiResponse<WorkerSkillsResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<WorkerSkillsResponse>> call,
                    Response<ApiResponse<WorkerSkillsResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    callback.onResult(Result.error(new AppError(
                            "Không tải được kỹ năng thợ. HTTP " + response.code()
                    )));
                    return;
                }

                ApiResponse<WorkerSkillsResponse> body = response.body();
                if (body == null || !body.isSuccess()) {
                    callback.onResult(Result.error(new AppError(
                            body != null ? body.getMessage() : "Response kỹ năng thợ rỗng"
                    )));
                    return;
                }

                List<WorkerSkill> skills = WorkerSkillMapper.toDomainList(
                        body.getData() != null ? body.getData().getSkills() : null
                );

                callback.onResult(Result.success(skills));
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerSkillsResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(
                        "Lỗi kết nối API skills: " + t.getMessage(),
                        t
                )));
            }
        });
    }

    @Override
    public void updateSkills(
            List<WorkerSkill> skills,
            ResultCallback<List<WorkerSkill>> callback
    ) {
        WorkerSkillsUpdateRequest request = new WorkerSkillsUpdateRequest(
                WorkerSkillMapper.toRequestItems(skills)
        );

        api.updateSkills(request).enqueue(new Callback<ApiResponse<WorkerSkillsResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<WorkerSkillsResponse>> call,
                    Response<ApiResponse<WorkerSkillsResponse>> response
            ) {
                if (!response.isSuccessful()) {
                    callback.onResult(Result.error(new AppError(
                            "Cập nhật kỹ năng thất bại. HTTP " + response.code()
                    )));
                    return;
                }

                ApiResponse<WorkerSkillsResponse> body = response.body();
                if (body == null || !body.isSuccess()) {
                    callback.onResult(Result.error(new AppError(
                            body != null ? body.getMessage() : "Response cập nhật kỹ năng rỗng"
                    )));
                    return;
                }

                List<WorkerSkill> updatedSkills = WorkerSkillMapper.toDomainList(
                        body.getData() != null ? body.getData().getSkills() : null
                );

                callback.onResult(Result.success(updatedSkills));
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerSkillsResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(
                        "Lỗi kết nối API cập nhật skills: " + t.getMessage(),
                        t
                )));
            }
        });
    }
}