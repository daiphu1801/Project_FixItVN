package com.fixit.feature.worker.profile.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.profile.data.remote.dto.response.WorkerProfileResponse;
import com.fixit.feature.worker.profile.data.remote.dto.request.WorkerProfileUpdateRequest;
import com.fixit.feature.worker.profile.data.remote.dto.response.WorkerSkillsResponse;
import com.fixit.feature.worker.profile.data.remote.dto.request.WorkerSkillsUpdateRequest;
import com.fixit.feature.worker.profile.data.remote.dto.response.ServiceCategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;

public interface WorkerProfileApi {

    @GET("api/v1/workers/me/profile")
    Call<ApiResponse<WorkerProfileResponse>> getProfile();

    @PATCH("api/v1/workers/me/profile")
    Call<ApiResponse<WorkerProfileResponse>> updateProfile(
            @Body WorkerProfileUpdateRequest request
    );

    @GET("api/v1/workers/me/skills")
    Call<ApiResponse<WorkerSkillsResponse>> getSkills();

    @PUT("api/v1/workers/me/skills")
    Call<ApiResponse<WorkerSkillsResponse>> updateSkills(
            @Body WorkerSkillsUpdateRequest request
    );

    @GET("api/v1/services/categories")
    Call<ApiResponse<List<ServiceCategoryResponse>>> getServiceCategories();
}