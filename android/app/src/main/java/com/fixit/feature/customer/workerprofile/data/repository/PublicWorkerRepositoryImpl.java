package com.fixit.feature.customer.workerprofile.data.repository;

import androidx.annotation.NonNull;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.workerprofile.data.remote.api.PublicWorkerApi;
import com.fixit.feature.customer.workerprofile.data.remote.dto.response.PublicWorkerProfileResponse;
import com.fixit.feature.customer.workerprofile.data.remote.dto.response.PublicWorkerSkillResponse;
import com.fixit.feature.customer.workerprofile.data.remote.mapper.PublicWorkerMapper;
import com.fixit.feature.customer.workerprofile.domain.model.PublicWorkerProfile;
import com.fixit.feature.customer.workerprofile.domain.model.PublicWorkerSkill;
import com.fixit.feature.customer.workerprofile.domain.repository.PublicWorkerRepository;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicWorkerRepositoryImpl implements PublicWorkerRepository {

    private final PublicWorkerApi api;

    @Inject
    public PublicWorkerRepositoryImpl(PublicWorkerApi api) {
        this.api = api;
    }

    @Override
    public void getWorkerProfile(String workerId, ResultCallback<PublicWorkerProfile> callback) {
        api.getWorkerProfile(workerId).enqueue(new Callback<ApiResponse<PublicWorkerProfileResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<PublicWorkerProfileResponse>> call, @NonNull Response<ApiResponse<PublicWorkerProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(PublicWorkerMapper.toDomain(response.body().getData())));
                } else {
                    callback.onResult(Result.error(new AppError("Failed to fetch worker profile")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<PublicWorkerProfileResponse>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    @Override
    public void getWorkerSkills(String workerId, ResultCallback<List<PublicWorkerSkill>> callback) {
        api.getWorkerSkills(workerId).enqueue(new Callback<ApiResponse<List<PublicWorkerSkillResponse>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<PublicWorkerSkillResponse>>> call, @NonNull Response<ApiResponse<List<PublicWorkerSkillResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onResult(Result.success(PublicWorkerMapper.toSkillDomainList(response.body().getData())));
                } else {
                    callback.onResult(Result.error(new AppError("Failed to fetch worker skills")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<PublicWorkerSkillResponse>>> call, @NonNull Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }
}
