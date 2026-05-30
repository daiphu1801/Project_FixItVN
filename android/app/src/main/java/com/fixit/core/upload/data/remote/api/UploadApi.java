package com.fixit.core.upload.data.remote.api;

import com.fixit.core.network.ApiResponse;
import com.fixit.core.upload.data.remote.dto.UploadRequest;
import com.fixit.core.upload.data.remote.dto.UploadResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UploadApi {
    @POST("api/v1/uploads/presigned-url")
    Call<ApiResponse<UploadResponse.PresignedUrl>> getPresignedUrl(
            @Body UploadRequest.PresignedUrl request
    );

    @POST("api/v1/uploads/confirm")
    Call<ApiResponse<UploadResponse.ConfirmedUpload>> confirmUpload(
            @Body UploadRequest.Confirm request
    );
}
