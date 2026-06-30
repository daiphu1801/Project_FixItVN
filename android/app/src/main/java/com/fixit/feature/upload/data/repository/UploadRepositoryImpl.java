package com.fixit.feature.upload.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.upload.data.local.dao.PendingUploadDao;
import com.fixit.feature.upload.data.local.entity.PendingUploadEntity;
import com.fixit.feature.upload.data.mapper.UploadMapper;
import com.fixit.feature.upload.data.remote.api.CloudinaryUploadApi;
import com.fixit.feature.upload.data.remote.api.UploadApi;
import com.fixit.feature.upload.data.remote.dto.request.PresignedUrlRequest;
import com.fixit.feature.upload.data.remote.dto.request.UploadConfirmRequest;
import com.fixit.feature.upload.data.remote.dto.response.CloudinaryUploadResponse;
import com.fixit.feature.upload.data.remote.dto.response.PresignedUrlResponse;
import com.fixit.feature.upload.data.remote.dto.response.UploadedFileResponse;
import com.fixit.feature.upload.data.worker.UploadWorkManagerScheduler;
import com.fixit.feature.upload.domain.model.ConfirmedUpload;
import com.fixit.feature.upload.domain.model.LocalUploadFile;
import com.fixit.feature.upload.domain.model.PendingUploadConfirm;
import com.fixit.feature.upload.domain.model.PendingUploadStatus;
import com.fixit.feature.upload.domain.model.QueuedUpload;
import com.fixit.feature.upload.domain.model.UploadQueueRequest;
import com.fixit.feature.upload.domain.model.UploadTargetType;
import com.fixit.feature.upload.domain.model.UploadTicket;
import com.fixit.feature.upload.domain.repository.UploadRepository;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fixit.feature.upload.util.UploadFilePreparer;


import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class UploadRepositoryImpl implements UploadRepository {

    private final UploadApi uploadApi;
    private final CloudinaryUploadApi cloudinaryUploadApi;
    private final PendingUploadDao pendingUploadDao;
    private final UploadWorkflowProcessor uploadWorkflowProcessor;
    private final UploadWorkManagerScheduler uploadWorkManagerScheduler;
    private final Gson gson;

    @Inject
    public UploadRepositoryImpl(
            UploadApi uploadApi,
            CloudinaryUploadApi cloudinaryUploadApi,
            PendingUploadDao pendingUploadDao,
            UploadWorkflowProcessor uploadWorkflowProcessor,
            UploadWorkManagerScheduler uploadWorkManagerScheduler,
            Gson gson) {
        this.uploadApi = uploadApi;
        this.cloudinaryUploadApi = cloudinaryUploadApi;
        this.pendingUploadDao = pendingUploadDao;
        this.uploadWorkflowProcessor = uploadWorkflowProcessor;
        this.uploadWorkManagerScheduler = uploadWorkManagerScheduler;
        this.gson = gson;
    }

    @Override
    public void requestPresignedUrl(LocalUploadFile file, ResultCallback<UploadTicket> callback) {
        if (!isValidLocalFile(file)) {
            callback.onResult(Result.error(new AppError("File upload không hợp lệ")));
            return;
        }

        PresignedUrlRequest request = new PresignedUrlRequest(
                file.getPurpose(),
                file.getOriginalFileName(),
                file.getContentType(),
                file.getFileSize());

        uploadApi.requestPresignedUrl(request).enqueue(new Callback<ApiResponse<PresignedUrlResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<PresignedUrlResponse>> call,
                    Response<ApiResponse<PresignedUrlResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    callback.onResult(
                            Result.error(new AppError("Không tạo được quyền upload. HTTP " + response.code())));
                    return;
                }

                UploadTicket ticket = UploadMapper.toTicket(response.body().getData());
                if (ticket == null || ticket.getUploadId() == null || ticket.getUploadUrl() == null) {
                    callback.onResult(Result.error(new AppError("Dữ liệu quyền upload không hợp lệ")));
                    return;
                }

                callback.onResult(Result.success(ticket));
            }

            @Override
            public void onFailure(Call<ApiResponse<PresignedUrlResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError("Lỗi kết nối khi xin quyền upload: " + t.getMessage(), t)));
            }
        });
    }

    @Override
    public void uploadToCloudinary(
            UploadTicket ticket,
            LocalUploadFile file,
            ResultCallback<PendingUploadConfirm> callback) {
        if (ticket == null || file == null) {
            callback.onResult(Result.error(new AppError("Thiếu dữ liệu upload Cloudinary")));
            return;
        }

        MediaType mediaType = MediaType.parse(file.getContentType());
        RequestBody fileBody = RequestBody.create(file.getFile(), mediaType);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                file.getOriginalFileName(),
                fileBody);

        Map<String, RequestBody> formData = new HashMap<>();
        if (ticket.getFormData() != null) {
            for (Map.Entry<String, String> entry : ticket.getFormData().entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    formData.put(entry.getKey(), RequestBody.create(entry.getValue(), MultipartBody.FORM));
                }
            }
        }

        cloudinaryUploadApi.uploadImage(ticket.getUploadUrl(), formData, filePart)
                .enqueue(new Callback<CloudinaryUploadResponse>() {
                    @Override
                    public void onResponse(
                            Call<CloudinaryUploadResponse> call,
                            Response<CloudinaryUploadResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            callback.onResult(
                                    Result.error(new AppError("Upload Cloudinary thất bại. HTTP " + response.code())));
                            return;
                        }

                        CloudinaryUploadResponse cloudinary = response.body();
                        callback.onResult(Result.success(new PendingUploadConfirm(
                                ticket.getUploadId(),
                                cloudinary.getPublicId(),
                                cloudinary.getSecureUrl(),
                                file.getContentType(),
                                cloudinary.getBytes() > 0 ? cloudinary.getBytes() : file.getFileSize())));
                    }

                    @Override
                    public void onFailure(Call<CloudinaryUploadResponse> call, Throwable t) {
                        callback.onResult(
                                Result.error(new AppError("Lỗi kết nối khi upload Cloudinary: " + t.getMessage(), t)));
                    }
                });
    }

    @Override
    public void confirmUpload(PendingUploadConfirm pending, ResultCallback<ConfirmedUpload> callback) {
        if (pending == null) {
            callback.onResult(Result.error(new AppError("Không có upload nào cần xác nhận")));
            return;
        }

        UploadConfirmRequest request = new UploadConfirmRequest(
                pending.getUploadId(),
                pending.getObjectKey(),
                pending.getFileUrl(),
                pending.getContentType(),
                pending.getFileSize());

        uploadApi.confirmUpload(request).enqueue(new Callback<ApiResponse<UploadedFileResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<UploadedFileResponse>> call,
                    Response<ApiResponse<UploadedFileResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    callback.onResult(Result.error(new AppError("Xác nhận upload thất bại. HTTP " + response.code())));
                    return;
                }

                ConfirmedUpload confirmedUpload = UploadMapper.toConfirmedUpload(response.body().getData());
                if (confirmedUpload == null) {
                    callback.onResult(Result.error(new AppError("Dữ liệu xác nhận upload không hợp lệ")));
                    return;
                }

                callback.onResult(Result.success(confirmedUpload));
            }

            @Override
            public void onFailure(Call<ApiResponse<UploadedFileResponse>> call, Throwable t) {
                callback.onResult(Result.error(new AppError("Lỗi kết nối khi xác nhận upload: " + t.getMessage(), t)));
            }
        });
    }

    @Override
    public void uploadAndConfirm(LocalUploadFile file, ResultCallback<ConfirmedUpload> callback) {
        enqueueUpload(UploadQueueRequest.of(file), result -> {
            if (!result.isSuccess()) {
                callback.onResult(Result.error(result.getError()));
                return;
            }

            long localId = result.getData().getId();
            new Thread(() -> {
                try {
                    uploadWorkflowProcessor.processAll();
                    PendingUploadEntity upload = pendingUploadDao.findById(localId);
                    postConfirmedResult(upload, callback);
                    uploadWorkManagerScheduler.schedule();
                } catch (Exception ex) {
                    uploadWorkManagerScheduler.schedule();
                    postError(callback, "Upload đã được lưu hàng đợi retry: " + ex.getMessage(), ex);
                }
            }).start();
        });
    }

    @Override
    public void enqueueUpload(UploadQueueRequest request, ResultCallback<QueuedUpload> callback) {
        if (request == null || !isValidLocalFile(request.getLocalFile())) {
            callback.onResult(Result.error(new AppError("File upload không hợp lệ")));
            return;
        }

        new Thread(() -> {
            try {
                LocalUploadFile file = request.getLocalFile();
                long now = System.currentTimeMillis();
                String initialStatus = UploadTargetType.WORKER_KYC.equals(request.getTargetType())
                        ? PendingUploadStatus.LOCAL_DRAFT
                        : PendingUploadStatus.LOCAL_SELECTED;

                PendingUploadEntity entity = new PendingUploadEntity(
                        file.getFile().getAbsolutePath(),
                        file.getOriginalFileName(),
                        file.getContentType(),
                        file.getFileSize(),
                        file.getPurpose(),
                        initialStatus,
                        request.getTargetType(),
                        request.getTargetEntityId(),
                        request.getGroupId(),
                        request.getSlotKey(),
                        request.getExtraPayloadJson(),
                        now);

                long id = pendingUploadDao.insert(entity);
                // KYC dùng processQueue() thủ công khi nhấn Submit, không tự schedule ngay
                if (!UploadTargetType.WORKER_KYC.equals(request.getTargetType())) {
                    uploadWorkManagerScheduler.schedule();
                }
                postSuccess(callback, new QueuedUpload(id, file.getPurpose(), initialStatus));
            } catch (Exception ex) {
                postError(callback, "Không lưu được upload vào Room: " + ex.getMessage(), ex);
            }
        }).start();
    }

    @Override
    public void runPendingUploads(ResultCallback<Void> callback) {
        new Thread(() -> {
            try {
                uploadWorkflowProcessor.processAll();
                uploadWorkManagerScheduler.schedule();
                postSuccess(callback, null);
            } catch (Exception ex) {
                uploadWorkManagerScheduler.schedule();
                postError(callback, "Retry upload thất bại: " + ex.getMessage(), ex);
            }
        }).start();
    }

    @Override
    public void retryPendingConfirm(ResultCallback<ConfirmedUpload> callback) {
        runPendingUploads(result -> {
            if (result.isSuccess()) {
                callback.onResult(Result.error(new AppError("Đã chạy retry upload queue")));
            } else {
                callback.onResult(Result.error(result.getError()));
            }
        });
    }

    @Override
    public void hasActiveUploads(String targetType, ResultCallback<Boolean> callback) {
        new Thread(() -> {
            try {
                int count = pendingUploadDao.countActiveByTargetType(targetType);
                postSuccess(callback, count > 0);
            } catch (Exception ex) {
                postError(callback, "Lỗi kiểm tra trạng thái upload: " + ex.getMessage(), ex);
            }
        }).start();
    }

    @Override
    public void hasSubmittedActiveUploads(String targetType, ResultCallback<Boolean> callback) {
        new Thread(() -> {
            try {
                int count = pendingUploadDao.countSubmittedActiveByTargetType(targetType);
                postSuccess(callback, count > 0);
            } catch (Exception ex) {
                postError(callback, "Lỗi kiểm tra trạng thái upload: " + ex.getMessage(), ex);
            }
        }).start();
    }

    @Override
    public void clearStaleUploads(String targetType, long maxAgeMillis, ResultCallback<Void> callback) {
        new Thread(() -> {
            try {
                long timeLimit = System.currentTimeMillis() - maxAgeMillis;
                pendingUploadDao.deleteStaleUploads(targetType, timeLimit);
                postSuccess(callback, null);
            } catch (Exception ex) {
                postError(callback, "Lỗi dọn dẹp ảnh nháp cũ: " + ex.getMessage(), ex);
            }
        }).start();
    }

    @Override
    public void submitKycGroup(String groupId, ResultCallback<Void> callback) {
        new Thread(() -> {
            try {
                pendingUploadDao.submitGroup(groupId);
                uploadWorkflowProcessor.processAll();
                uploadWorkManagerScheduler.schedule();
                postSuccess(callback, null);
            } catch (Exception ex) {
                uploadWorkManagerScheduler.schedule();
                postError(callback, "Lỗi bắt đầu tải lên KYC: " + ex.getMessage(), ex);
            }
        }).start();
    }

    @Override
    public void getSubmittedActiveUploads(String targetType, ResultCallback<List<QueuedUpload>> callback) {
        new Thread(() -> {
            try {
                List<PendingUploadEntity> entities = pendingUploadDao.getSubmittedActiveUploadsByTargetType(targetType);
                List<QueuedUpload> list = new ArrayList<>();
                for (PendingUploadEntity entity : entities) {
                    list.add(new QueuedUpload(
                            entity.getId(),
                            entity.getPurpose(),
                            entity.getStatus(),
                            entity.getSlotKey(),
                            entity.getLastError(),
                            entity.getRetryCount(),
                            entity.getGroupId()
                    ));
                }
                postSuccess(callback, list);
            } catch (Exception ex) {
                postError(callback, "Lỗi lấy danh sách upload: " + ex.getMessage(), ex);
            }
        }).start();
    }

    @Override
    public void retryKycGroup(String groupId, ResultCallback<Void> callback) {
        new Thread(() -> {
            try {
                List<PendingUploadEntity> group = pendingUploadDao.getByGroupId(groupId);
                for (PendingUploadEntity entity : group) {
                    entity.setRetryCount(0);
                    entity.setLastError(null);
                    pendingUploadDao.update(entity);
                }
                uploadWorkflowProcessor.processAll();
                uploadWorkManagerScheduler.schedule();
                postSuccess(callback, null);
            } catch (Exception ex) {
                uploadWorkManagerScheduler.schedule();
                postError(callback, "Lỗi thử lại tải lên KYC: " + ex.getMessage(), ex);
            }
        }).start();
    }

    @Override
    public void cancelKycGroup(String groupId, ResultCallback<Void> callback) {
        new Thread(() -> {
            try {
                List<PendingUploadEntity> group = pendingUploadDao.getByGroupId(groupId);
                for (PendingUploadEntity entity : group) {
                    UploadFilePreparer.deleteLocalFile(entity.getLocalFilePath());
                    pendingUploadDao.deleteById(entity.getId());
                }
                postSuccess(callback, null);
            } catch (Exception ex) {
                postError(callback, "Lỗi hủy tải lên KYC: " + ex.getMessage(), ex);
            }
        }).start();
    }

    // ──────────────────────────── helpers ────────────────────────────

    private void postConfirmedResult(
            PendingUploadEntity upload,
            ResultCallback<ConfirmedUpload> callback) {
        if (upload == null) {
            postError(callback, "Upload đã hoàn tất và được gắn nghiệp vụ", null);
            return;
        }

        if (PendingUploadStatus.CONFIRMED.equals(upload.getStatus())
                || PendingUploadStatus.CONSUMED.equals(upload.getStatus())) {
            ConfirmedUpload confirmed = new ConfirmedUpload(
                    upload.getUploadId(),
                    upload.getPurpose(),
                    upload.getObjectKey(),
                    upload.getFileUrl(),
                    upload.getContentType(),
                    upload.getFileSize(),
                    upload.getStatus());
            postSuccess(callback, confirmed);
            return;
        }

        postError(callback, "Upload đã vào hàng đợi retry. Trạng thái: " + upload.getStatus(), null);
    }

    private boolean isValidLocalFile(LocalUploadFile file) {
        return file != null
                && file.getFile() != null
                && file.getFile().exists()
                && file.getFile().isFile()
                && file.getFileSize() > 0
                && file.getPurpose() != null
                && file.getContentType() != null;
    }

    private <T> void postSuccess(ResultCallback<T> callback, T data) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onResult(Result.success(data)));
    }

    private <T> void postError(ResultCallback<T> callback, String message, Throwable throwable) {
        new Handler(Looper.getMainLooper())
                .post(() -> callback.onResult(Result.error(new AppError(message, throwable))));
    }
}
