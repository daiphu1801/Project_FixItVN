package com.fixit.feature.upload.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.upload.domain.model.ConfirmedUpload;
import com.fixit.feature.upload.domain.model.LocalUploadFile;
import com.fixit.feature.upload.domain.model.PendingUploadConfirm;
import com.fixit.feature.upload.domain.model.QueuedUpload;
import com.fixit.feature.upload.domain.model.UploadQueueRequest;
import com.fixit.feature.upload.domain.model.UploadTicket;

public interface UploadRepository {

        void requestPresignedUrl(
                        LocalUploadFile file,
                        ResultCallback<UploadTicket> callback);

        void uploadToCloudinary(
                        UploadTicket ticket,
                        LocalUploadFile file,
                        ResultCallback<PendingUploadConfirm> callback);

        void confirmUpload(
                        PendingUploadConfirm pending,
                        ResultCallback<ConfirmedUpload> callback);

        void uploadAndConfirm(
                        LocalUploadFile file,
                        ResultCallback<ConfirmedUpload> callback);

        void enqueueUpload(
                        UploadQueueRequest request,
                        ResultCallback<QueuedUpload> callback);

        void runPendingUploads(
                        ResultCallback<Void> callback);

        void retryPendingConfirm(ResultCallback<ConfirmedUpload> callback);

        /**
         * Kiểm tra xem có upload nào đang active (chưa CONSUMED) cho targetType này
         * không.
         * Chạy trên background thread, trả kết quả qua callback.
         * Dùng để redirect sang màn Pending khi user quay lại app.
         */
        void hasActiveUploads(String targetType, ResultCallback<Boolean> callback);

        /**
         * Kiểm tra xem có upload nào đã SUBMIT và đang active cho targetType này không.
         * Tránh trường hợp chỉ có ảnh nháp chưa submit mà bị redirect.
         */
        void hasSubmittedActiveUploads(String targetType, ResultCallback<Boolean> callback);

        /**
         * Xóa các upload nháp cũ (chưa từng submit) hoặc các upload đã quá cũ (bị kẹt).
         */
        void clearStaleUploads(String targetType, long maxAgeMillis, ResultCallback<Void> callback);

        /**
         * Kích hoạt chuyển đổi các nháp trong nhóm sang trạng thái LOCAL_SELECTED để bắt đầu tải lên.
         */
        void submitKycGroup(String groupId, ResultCallback<Void> callback);
}
