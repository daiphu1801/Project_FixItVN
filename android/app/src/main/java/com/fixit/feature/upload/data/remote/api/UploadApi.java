package com.fixit.feature.upload.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.upload.data.remote.dto.request.ProofOfWorkCreateRequest;
import com.fixit.feature.upload.data.remote.dto.request.PresignedUrlRequest;
import com.fixit.feature.upload.data.remote.dto.request.UploadConfirmRequest;
import com.fixit.feature.upload.data.remote.dto.request.UserAvatarUpdateRequest;
import com.fixit.feature.upload.data.remote.dto.request.WorkerKycSubmitRequest;
import com.fixit.feature.upload.data.remote.dto.response.ProofOfWorkResponse;
import com.fixit.feature.upload.data.remote.dto.response.PresignedUrlResponse;
import com.fixit.feature.upload.data.remote.dto.response.UploadedFileResponse;
import com.fixit.feature.upload.data.remote.dto.response.UserAvatarResponse;
import com.fixit.feature.upload.data.remote.dto.response.WorkerKycResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.POST;

public interface UploadApi {

    @POST("api/v1/uploads/presigned-url")
    Call<ApiResponse<PresignedUrlResponse>> requestPresignedUrl(
            @Body PresignedUrlRequest request
    );

    @POST("api/v1/uploads/confirm")
    Call<ApiResponse<UploadedFileResponse>> confirmUpload(
            @Body UploadConfirmRequest request
    );

    @PATCH("api/v1/users/me/avatar")
    Call<ApiResponse<UserAvatarResponse>> updateAvatar(
            @Body UserAvatarUpdateRequest request
    );

    @POST("api/v1/workers/me/kyc")
    Call<ApiResponse<WorkerKycResponse>> submitWorkerKyc(
            @Body WorkerKycSubmitRequest request
    );

    @POST("api/v1/bookings/{bookingId}/proof-of-work")
    Call<ApiResponse<ProofOfWorkResponse>> createProofOfWork(
            @Path("bookingId") String bookingId,
            @Body ProofOfWorkCreateRequest request
    );
}
