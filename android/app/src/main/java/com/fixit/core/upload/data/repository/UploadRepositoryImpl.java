package com.fixit.core.upload.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.core.upload.data.remote.api.UploadApi;
import com.fixit.core.upload.data.remote.dto.UploadRequest;
import com.fixit.core.upload.data.remote.dto.UploadResponse;
import com.fixit.core.upload.data.remote.mapper.UploadMapper;
import com.fixit.core.upload.domain.model.ConfirmedUpload;
import com.fixit.core.upload.domain.model.PresignedUpload;
import com.fixit.core.upload.domain.model.UploadPurpose;
import com.fixit.core.upload.domain.repository.UploadRepository;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadRepositoryImpl implements UploadRepository {
    private final UploadApi uploadApi;

    @Inject
    public UploadRepositoryImpl(UploadApi uploadApi) {
        this.uploadApi = uploadApi;
    }

    @Override
    public void getPresignedUrl(
            String fileName,
            String contentType,
            long fileSizeBytes,
            UploadPurpose uploadPurpose,
            String referenceId,
            ResultCallback<PresignedUpload> callback
    ) {
        UploadRequest.PresignedUrl request = new UploadRequest.PresignedUrl(
                fileName,
                contentType,
                fileSizeBytes,
                uploadPurpose.name(),
                referenceId
        );

        uploadApi.getPresignedUrl(request).enqueue(new Callback<ApiResponse<UploadResponse.PresignedUrl>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<UploadResponse.PresignedUrl>> call,
                    Response<ApiResponse<UploadResponse.PresignedUrl>> response
            ) {
                ApiResponse<UploadResponse.PresignedUrl> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    callback.onResult(Result.success(UploadMapper.toDomain(body.getData())));
                    return;
                }

                callback.onResult(Result.error(new AppError(resolveMessage(response, body))));
            }

            @Override
            public void onFailure(Call<ApiResponse<UploadResponse.PresignedUrl>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    @Override
    public void confirmUpload(
            String uploadId,
            String storageKey,
            String publicUrl,
            String fileName,
            String contentType,
            long fileSizeBytes,
            String checksum,
            UploadPurpose uploadPurpose,
            String referenceId,
            ResultCallback<ConfirmedUpload> callback
    ) {
        UploadRequest.Confirm request = new UploadRequest.Confirm(
                uploadId,
                storageKey,
                publicUrl,
                fileName,
                contentType,
                fileSizeBytes,
                checksum,
                uploadPurpose.name(),
                referenceId
        );

        uploadApi.confirmUpload(request).enqueue(new Callback<ApiResponse<UploadResponse.ConfirmedUpload>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<UploadResponse.ConfirmedUpload>> call,
                    Response<ApiResponse<UploadResponse.ConfirmedUpload>> response
            ) {
                ApiResponse<UploadResponse.ConfirmedUpload> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    callback.onResult(Result.success(UploadMapper.toDomain(body.getData())));
                    return;
                }

                callback.onResult(Result.error(new AppError(resolveMessage(response, body))));
            }

            @Override
            public void onFailure(Call<ApiResponse<UploadResponse.ConfirmedUpload>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    private String resolveMessage(Response<?> response, ApiResponse<?> body) {
        if (body != null && body.getMessage() != null) {
            return body.getMessage();
        }
        return "Upload request failed: HTTP " + response.code();
    }
}
