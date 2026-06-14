package com.fixit.feature.customer.workerprofile.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.workerprofile.data.remote.dto.response.PublicWorkerProfileResponse;
import com.fixit.feature.customer.workerprofile.data.remote.dto.response.PublicWorkerSkillResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PublicWorkerApi {

    @GET("api/v1/workers/{workerId}/profile")
    Call<ApiResponse<PublicWorkerProfileResponse>> getWorkerProfile(@Path("workerId") String workerId);

    @GET("api/v1/workers/{workerId}/skills")
    Call<ApiResponse<List<PublicWorkerSkillResponse>>> getWorkerSkills(@Path("workerId") String workerId);
}
