package com.fixit.feature.upload.data.repository;

import android.util.Log;

import com.fixit.core.network.ApiResponse;
import com.fixit.feature.upload.data.local.dao.PendingUploadDao;
import com.fixit.feature.upload.data.local.entity.PendingUploadEntity;
import com.fixit.feature.upload.data.remote.api.CloudinaryUploadApi;
import com.fixit.feature.upload.data.remote.api.UploadApi;
import com.fixit.feature.upload.data.remote.dto.request.PresignedUrlRequest;
import com.fixit.feature.upload.data.remote.dto.request.ProofOfWorkCreateRequest;
import com.fixit.feature.upload.data.remote.dto.request.UploadConfirmRequest;
import com.fixit.feature.upload.data.remote.dto.request.UserAvatarUpdateRequest;
import com.fixit.feature.upload.data.remote.dto.request.WorkerKycSubmitRequest;
import com.fixit.feature.upload.data.remote.dto.response.CloudinaryUploadResponse;
import com.fixit.feature.upload.data.remote.dto.response.PresignedUrlResponse;
import com.fixit.feature.upload.data.remote.dto.response.UploadedFileResponse;
import com.fixit.feature.upload.domain.model.PendingUploadStatus;
import com.fixit.feature.upload.domain.model.UploadPurpose;
import com.fixit.feature.upload.domain.model.UploadTargetType;
import com.fixit.feature.upload.util.UploadFilePreparer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

@Singleton
public class UploadWorkflowProcessor {
    private static final String TAG = "UploadWorkflow";
    private static final long PRESIGNED_EXPIRY_SAFETY_MS = 30_000L;
    private static final int MAX_STEPS_PER_RUN = 80;
    /** Số lần retry tối đa trước khi xóa hẳn item khỏi hàng đợi. */
    private static final int MAX_RETRY_COUNT = 10;

    private final UploadApi uploadApi;
    private final CloudinaryUploadApi cloudinaryUploadApi;
    private final PendingUploadDao pendingUploadDao;
    private final Gson gson;

    @Inject
    public UploadWorkflowProcessor(
            UploadApi uploadApi,
            CloudinaryUploadApi cloudinaryUploadApi,
            PendingUploadDao pendingUploadDao,
            Gson gson
    ) {
        this.uploadApi = uploadApi;
        this.cloudinaryUploadApi = cloudinaryUploadApi;
        this.pendingUploadDao = pendingUploadDao;
        this.gson = gson;
    }

    public synchronized void processAll() {
        int steps = 0;
        boolean progressed;
        Exception firstFailure = null;
        Set<Long> failedIdsThisRun = new HashSet<>();

        do {
            progressed = false;
            List<PendingUploadEntity> uploads = pendingUploadDao.getRunnableUploads();
            for (PendingUploadEntity upload : uploads) {
                if (failedIdsThisRun.contains(upload.getId())) {
                    continue;
                }

                if (steps++ >= MAX_STEPS_PER_RUN) {
                    Log.w(TAG, "Stop processing because max steps was reached");
                    if (firstFailure != null) {
                        throw new RuntimeException(firstFailure);
                    }
                    return;
                }

                try {
                    progressed |= processOne(upload);
                } catch (Exception ex) {
                    failedIdsThisRun.add(upload.getId());
                    if (firstFailure == null) {
                        firstFailure = ex;
                    }
                    Log.e(TAG, "Upload item failed. id=" + upload.getId()
                            + ", status=" + upload.getStatus()
                            + ", error=" + ex.getMessage(), ex);
                }
            }
        } while (progressed);

        if (firstFailure != null) {
            throw new RuntimeException(firstFailure);
        }
    }

    public boolean processOne(PendingUploadEntity upload) throws Exception {
        if (upload == null || PendingUploadStatus.CONSUMED.equals(upload.getStatus())) {
            return false;
        }

        // Xóa hẳn item nếu đã vượt quá số lần retry tối đa
        if (upload.getRetryCount() >= MAX_RETRY_COUNT) {
            Log.w(TAG, "Discarding upload after max retries. id=" + upload.getId()
                    + ", status=" + upload.getStatus()
                    + ", retries=" + upload.getRetryCount());
            discardPermanently(upload);
            return true;
        }

        markAttempt(upload);

        try {
            switch (upload.getStatus()) {
                case PendingUploadStatus.LOCAL_SELECTED:
                    requestPresigned(upload);
                    return true;
                case PendingUploadStatus.PRESIGNED_CREATED:
                case PendingUploadStatus.CLOUDINARY_UPLOAD_FAILED:
                    if (isPresignedExpired(upload)) {
                        requestPresigned(upload);
                    } else {
                        uploadToCloudinary(upload);
                    }
                    return true;
                case PendingUploadStatus.CLOUDINARY_UPLOADED:
                case PendingUploadStatus.CONFIRM_FAILED:
                    confirmUpload(upload);
                    return true;
                case PendingUploadStatus.CONFIRMED:
                case PendingUploadStatus.CONSUME_FAILED:
                    return consumeIfReady(upload);
                default:
                    fail(upload, upload.getStatus(), "Invalid upload status: " + upload.getStatus());
                    return false;
            }
        } catch (PermanentApiException ex) {
            // Lỗi vĩnh viễn (403/404/409): xóa hẳn, không retry
            Log.w(TAG, "Permanently discarding upload due to permanent API error. id=" + upload.getId()
                    + ", error=" + ex.getMessage());
            discardPermanently(upload);
            throw ex;
        } catch (Exception ex) {
            fail(upload, failedStatusFor(upload.getStatus()), ex.getMessage());
            throw ex;
        }
    }

    private String failedStatusFor(String status) {
        if (PendingUploadStatus.PRESIGNED_CREATED.equals(status)
                || PendingUploadStatus.CLOUDINARY_UPLOAD_FAILED.equals(status)) {
            return PendingUploadStatus.CLOUDINARY_UPLOAD_FAILED;
        }
        if (PendingUploadStatus.CLOUDINARY_UPLOADED.equals(status)
                || PendingUploadStatus.CONFIRM_FAILED.equals(status)) {
            return PendingUploadStatus.CONFIRM_FAILED;
        }
        if (PendingUploadStatus.CONFIRMED.equals(status)
                || PendingUploadStatus.CONSUME_FAILED.equals(status)) {
            return PendingUploadStatus.CONSUME_FAILED;
        }
        return status;
    }

    private void requestPresigned(PendingUploadEntity upload) throws Exception {
        Log.d(TAG, "Request presigned-url. id=" + upload.getId() + ", purpose=" + upload.getPurpose());
        validateLocalFile(upload);

        PresignedUrlRequest request = new PresignedUrlRequest(
                upload.getPurpose(),
                upload.getOriginalFileName(),
                upload.getContentType(),
                upload.getFileSize()
        );

        ApiResponse<PresignedUrlResponse> body = executeApi(uploadApi.requestPresignedUrl(request));
        PresignedUrlResponse response = body.getData();
        if (response == null || response.getUploadId() == null || response.getUploadUrl() == null) {
            throw new IllegalStateException("Invalid presigned-url response");
        }

        int expiresInSeconds = response.getExpiresInSeconds() != null
                ? response.getExpiresInSeconds()
                : 300;

        upload.setUploadId(response.getUploadId());
        upload.setObjectKey(response.getObjectKey());
        upload.setUploadUrl(response.getUploadUrl());
        upload.setFileUrl(response.getFileUrl());
        upload.setFormDataJson(gson.toJson(response.getFormData()));
        upload.setPresignedExpiresAt(System.currentTimeMillis() + Math.max(1, expiresInSeconds) * 1000L);
        upload.setStatus(PendingUploadStatus.PRESIGNED_CREATED);
        success(upload);
        Log.d(TAG, "Presigned-url created. id=" + upload.getId() + ", uploadId=" + upload.getUploadId());
    }

    private void uploadToCloudinary(PendingUploadEntity upload) throws Exception {
        Log.d(TAG, "Upload Cloudinary. id=" + upload.getId() + ", uploadId=" + upload.getUploadId());
        validateLocalFile(upload);
        if (upload.getUploadUrl() == null || upload.getUploadUrl().isBlank()) {
            requestPresigned(upload);
            return;
        }

        File file = new File(upload.getLocalFilePath());
        MediaType mediaType = MediaType.parse(upload.getContentType());
        RequestBody fileBody = RequestBody.create(file, mediaType);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                upload.getOriginalFileName(),
                fileBody
        );

        Map<String, RequestBody> formData = new HashMap<>();
        for (Map.Entry<String, String> entry : readFormData(upload).entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                formData.put(entry.getKey(), RequestBody.create(entry.getValue(), MultipartBody.FORM));
            }
        }

        Response<CloudinaryUploadResponse> response = cloudinaryUploadApi
                .uploadImage(upload.getUploadUrl(), formData, filePart)
                .execute();

        if (!response.isSuccessful() || response.body() == null) {
            throw new IllegalStateException("Upload Cloudinary failed. HTTP "
                    + response.code()
                    + readErrorBody(response));
        }

        CloudinaryUploadResponse cloudinary = response.body();
        upload.setObjectKey(cloudinary.getPublicId());
        upload.setFileUrl(cloudinary.getSecureUrl());
        upload.setFileSize(cloudinary.getBytes() > 0 ? cloudinary.getBytes() : upload.getFileSize());
        upload.setStatus(PendingUploadStatus.CLOUDINARY_UPLOADED);
        success(upload);
        Log.d(TAG, "Cloudinary uploaded. id=" + upload.getId() + ", publicId=" + upload.getObjectKey());
    }

    private void confirmUpload(PendingUploadEntity upload) throws Exception {
        Log.d(TAG, "Confirm upload. id=" + upload.getId() + ", uploadId=" + upload.getUploadId());
        UploadConfirmRequest request = new UploadConfirmRequest(
                upload.getUploadId(),
                upload.getObjectKey(),
                upload.getFileUrl(),
                upload.getContentType(),
                upload.getFileSize()
        );

        ApiResponse<UploadedFileResponse> body = executeApi(uploadApi.confirmUpload(request));
        UploadedFileResponse confirmed = body.getData();
        if (confirmed == null || confirmed.getUploadId() == null) {
            throw new IllegalStateException("Invalid confirm response");
        }

        upload.setUploadId(confirmed.getUploadId());
        upload.setObjectKey(confirmed.getObjectKey());
        upload.setFileUrl(confirmed.getFileUrl());
        upload.setContentType(confirmed.getContentType());
        upload.setFileSize(confirmed.getFileSize());
        upload.setStatus(PendingUploadStatus.CONFIRMED);
        success(upload);
        Log.d(TAG, "Upload confirmed. id=" + upload.getId() + ", uploadId=" + upload.getUploadId());
    }

    private boolean consumeIfReady(PendingUploadEntity upload) throws Exception {
        if (upload.getTargetType() == null || upload.getTargetType().isBlank()) {
            return false;
        }

        if (UploadTargetType.USER_AVATAR.equals(upload.getTargetType())) {
            Log.d(TAG, "Consume avatar. id=" + upload.getId() + ", uploadId=" + upload.getUploadId());
            executeApi(uploadApi.updateAvatar(new UserAvatarUpdateRequest(upload.getUploadId())));
            markConsumed(upload);
            return true;
        }

        if (UploadTargetType.PROOF_OF_WORK.equals(upload.getTargetType())) {
            executeApi(uploadApi.createProofOfWork(
                    upload.getTargetEntityId(),
                    new ProofOfWorkCreateRequest(upload.getUploadId(), proofTypeOf(upload))
            ));
            markConsumed(upload);
            return true;
        }

        if (UploadTargetType.WORKER_KYC.equals(upload.getTargetType())) {
            return consumeKycGroup(upload);
        }

        return false;
    }

    private boolean consumeKycGroup(PendingUploadEntity upload) throws Exception {
        if (upload.getGroupId() == null || upload.getGroupId().isBlank()) {
            return false;
        }

        List<PendingUploadEntity> group = pendingUploadDao.getByGroupId(upload.getGroupId());
        PendingUploadEntity front = findSlot(group, "front");
        PendingUploadEntity back = findSlot(group, "back");
        if (!isReadyForConsume(front) || !isReadyForConsume(back)) {
            return false;
        }
        for (PendingUploadEntity item : group) {
            if (!isReadyForConsume(item)) {
                return false;
            }
        }

        List<String> certificateIds = new ArrayList<>();
        for (PendingUploadEntity item : group) {
            if ("certificate".equals(item.getSlotKey()) && isReadyForConsume(item)) {
                certificateIds.add(item.getUploadId());
            }
        }

        executeApi(uploadApi.submitWorkerKyc(new WorkerKycSubmitRequest(
                front.getUploadId(),
                back.getUploadId(),
                certificateIds
        )));

        for (PendingUploadEntity item : group) {
            if (isReadyForConsume(item)) {
                markConsumed(item);
            }
        }
        return true;
    }

    private PendingUploadEntity findSlot(List<PendingUploadEntity> uploads, String slotKey) {
        for (PendingUploadEntity upload : uploads) {
            if (slotKey.equals(upload.getSlotKey())) {
                return upload;
            }
        }
        return null;
    }

    private boolean isReadyForConsume(PendingUploadEntity upload) {
        return upload != null
                && (PendingUploadStatus.CONFIRMED.equals(upload.getStatus())
                || PendingUploadStatus.CONSUME_FAILED.equals(upload.getStatus()));
    }

    private String proofTypeOf(PendingUploadEntity upload) {
        if (UploadPurpose.PROOF_AFTER_REPAIR.equals(upload.getPurpose())) {
            return "AFTER_REPAIR";
        }
        return "BEFORE_REPAIR";
    }

    private <T> ApiResponse<T> executeApi(Call<ApiResponse<T>> call) throws Exception {
        Response<ApiResponse<T>> response = call.execute();
        if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
            int code = response.code();
            String body = readErrorBody(response);
            String msg = "API failed. HTTP " + code + body;
            // Lỗi vĩnh viễn: không retry
            if (isPermanentHttpError(code)) {
                throw new PermanentApiException(msg);
            }
            throw new IllegalStateException(msg);
        }
        return response.body();
    }

    /** Lỗi HTTP mà không bao giờ có thể tự recover — xóa item khỏi hàng đợi. */
    private boolean isPermanentHttpError(int code) {
        return code == 403   // Forbidden: sai user hoặc không có quyền
                || code == 404   // Not found: file không tồn tại trên server
                || code == 409   // Conflict/Expired: presigned URL đã hết hạn
                || code == 422;  // Unprocessable: dữ liệu sai vĩnh viễn
    }

    /** Exception đặc biệt để phân biệt lỗi vĩnh viễn với lỗi có thể retry. */
    static final class PermanentApiException extends IllegalStateException {
        PermanentApiException(String msg) { super(msg); }
    }

    private String readErrorBody(Response<?> response) {
        try {
            if (response.errorBody() == null) {
                return "";
            }
            String raw = response.errorBody().string();
            if (raw == null || raw.isBlank()) {
                return "";
            }
            return ". Body=" + raw;
        } catch (Exception ignored) {
            return "";
        }
    }

    private Map<String, String> readFormData(PendingUploadEntity upload) {
        if (upload.getFormDataJson() == null || upload.getFormDataJson().isBlank()) {
            return new HashMap<>();
        }

        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> parsed = gson.fromJson(upload.getFormDataJson(), type);
        return parsed != null ? parsed : new HashMap<>();
    }

    private boolean isPresignedExpired(PendingUploadEntity upload) {
        return upload.getPresignedExpiresAt() <= 0
                || upload.getPresignedExpiresAt() <= System.currentTimeMillis() + PRESIGNED_EXPIRY_SAFETY_MS;
    }

    private void validateLocalFile(PendingUploadEntity upload) {
        if (upload.getLocalFilePath() == null) {
            throw new IllegalStateException("Missing localFilePath");
        }

        File file = new File(upload.getLocalFilePath());
        if (!file.exists() || !file.isFile() || file.length() <= 0) {
            throw new IllegalStateException("Local file no longer exists");
        }
    }

    private void markAttempt(PendingUploadEntity upload) {
        long now = System.currentTimeMillis();
        upload.setLastAttemptAt(now);
        upload.setUpdatedAt(now);
        pendingUploadDao.update(upload);
    }

    private void success(PendingUploadEntity upload) {
        upload.setLastError(null);
        upload.setUpdatedAt(System.currentTimeMillis());
        pendingUploadDao.update(upload);
    }

    private void fail(PendingUploadEntity upload, String failedStatus, String error) {
        upload.setStatus(failedStatus);
        upload.setRetryCount(upload.getRetryCount() + 1);
        upload.setLastError(error);
        upload.setUpdatedAt(System.currentTimeMillis());
        pendingUploadDao.update(upload);
    }

    /**
     * Xóa hẳn item khỏi hàng đợi và file local (nếu còn).
     * Dùng khi gặp lỗi vĩnh viễn (403/404/409) hoặc vượt quá MAX_RETRY_COUNT.
     */
    private void discardPermanently(PendingUploadEntity upload) {
        UploadFilePreparer.deleteLocalFile(upload.getLocalFilePath());
        pendingUploadDao.deleteById(upload.getId());
        Log.w(TAG, "Upload discarded permanently. id=" + upload.getId()
                + ", uploadId=" + upload.getUploadId()
                + ", lastError=" + upload.getLastError());
    }

    private void markConsumed(PendingUploadEntity upload) {
        Log.d(TAG, "Upload consumed. id=" + upload.getId() + ", uploadId=" + upload.getUploadId());
        upload.setStatus(PendingUploadStatus.CONSUMED);
        upload.setLastError(null);
        upload.setUpdatedAt(System.currentTimeMillis());
        pendingUploadDao.update(upload);
        UploadFilePreparer.deleteLocalFile(upload.getLocalFilePath());
        pendingUploadDao.deleteById(upload.getId());
    }
}
