package com.fixit.feature.upload.presentation;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.network.NetworkUtils;
import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.upload.domain.model.ConfirmedUpload;
import com.fixit.feature.upload.domain.model.QueuedUpload;
import com.fixit.feature.upload.domain.model.UploadQueueRequest;
import com.fixit.feature.upload.domain.usecase.UploadAndConfirmUseCase;
import com.fixit.feature.upload.util.UploadFilePreparer;
import com.fixit.feature.upload.domain.model.LocalUploadFile;
import com.fixit.feature.upload.domain.repository.UploadRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel dùng chung cho chức năng upload ảnh.
 *
 * Bất kỳ Fragment nào cần upload ảnh đều có thể dùng ViewModel này
 * bằng cách gọi upload(context, uri, purpose).
 *
 * Kết quả upload được phát qua LiveData để Fragment observe.
 */
@HiltViewModel
public class UploadViewModel extends BaseViewModel {

    private final UploadAndConfirmUseCase uploadAndConfirmUseCase;
    private final UploadRepository uploadRepository;

    // Kết quả upload mới nhất (dùng cho upload đơn lẻ: avatar, kyc, proof)
    private final MutableLiveData<UploadResult> _uploadResult = new MutableLiveData<>();
    public LiveData<UploadResult> uploadResult = _uploadResult;

    // Trạng thái đang upload (hiển thị loading spinner)
    private final MutableLiveData<Boolean> _isUploading = new MutableLiveData<>(false);
    public LiveData<Boolean> isUploading = _isUploading;

    // Danh sách upload đã hoàn thành (dùng cho multi-image: booking, complaint)
    private final MutableLiveData<List<ConfirmedUpload>> _confirmedUploads = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<ConfirmedUpload>> confirmedUploads = _confirmedUploads;

    // Kết quả xử lý hàng đợi (dùng khi cần chờ toàn bộ queue xong)
    private final MutableLiveData<UploadResult> _queueProcessResult = new MutableLiveData<>();
    public LiveData<UploadResult> queueProcessResult = _queueProcessResult;

    @Inject
    public UploadViewModel(
            UploadAndConfirmUseCase uploadAndConfirmUseCase,
            UploadRepository uploadRepository) {
        this.uploadAndConfirmUseCase = uploadAndConfirmUseCase;
        this.uploadRepository = uploadRepository;
    }

    /**
     * Upload 1 ảnh lên Cloudinary và confirm với backend.
     * Dùng cho avatar — chạy full pipeline ngay lập tức.
     *
     * @param context Android context (dùng để đọc file từ Uri)
     * @param uri     Uri của ảnh được chọn từ gallery/camera
     * @param purpose Mục đích upload (lấy từ UploadPurpose.*)
     */
    public void upload(Context context, Uri uri, String purpose) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _uploadResult.postValue(UploadResult.error(purpose, "Yêu cầu kết nối mạng để thực hiện chức năng này"));
            return;
        }

        _isUploading.postValue(true);

        try {
            LocalUploadFile localFile = UploadFilePreparer.fromUri(context, uri, purpose);

            uploadAndConfirmUseCase.execute(localFile, result -> {
                _isUploading.postValue(false);

                if (result.isSuccess()) {
                    ConfirmedUpload confirmed = result.getData();
                    _uploadResult.postValue(UploadResult.success(purpose, confirmed));
                    addToConfirmedList(confirmed);
                } else {
                    String errorMsg = result.getError() != null
                            ? result.getError().getMessage()
                            : "Upload thất bại";
                    _uploadResult.postValue(UploadResult.error(purpose, errorMsg));
                }
            });
        } catch (Exception e) {
            _isUploading.postValue(false);
            _uploadResult.postValue(
                    UploadResult.error(purpose, "Không đọc được file: " + e.getMessage()));
        }
    }

    /**
     * Enqueue ảnh vào hàng đợi local + tự động chạy upload (autoProcess=true).
     */
    public void upload(
            Context context,
            Uri uri,
            String purpose,
            String targetType,
            String targetEntityId,
            String groupId,
            String slotKey,
            String extraPayloadJson) {
        upload(context, uri, purpose, targetType, targetEntityId, groupId, slotKey, extraPayloadJson, true);
    }

    /**
     * Enqueue ảnh vào hàng đợi local Room.
     * Nếu autoProcess=false, upload sẽ không chạy ngay (dùng cho KYC — submit thủ
     * công).
     */
    public void upload(
            Context context,
            Uri uri,
            String purpose,
            String targetType,
            String targetEntityId,
            String groupId,
            String slotKey,
            String extraPayloadJson,
            boolean autoProcess) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _uploadResult.postValue(UploadResult.error(purpose, "Yêu cầu kết nối mạng để thực hiện chức năng này"));
            return;
        }

        _isUploading.postValue(true);

        try {
            LocalUploadFile localFile = UploadFilePreparer.fromUri(context, uri, purpose);
            UploadQueueRequest request = new UploadQueueRequest(
                    localFile,
                    targetType,
                    targetEntityId,
                    groupId,
                    slotKey,
                    extraPayloadJson);

            uploadRepository.enqueueUpload(request, result -> {
                _isUploading.postValue(false);

                if (result.isSuccess()) {
                    QueuedUpload queuedUpload = result.getData();
                    ConfirmedUpload queued = new ConfirmedUpload(
                            String.valueOf(queuedUpload.getId()),
                            purpose,
                            null,
                            null,
                            localFile.getContentType(),
                            localFile.getFileSize(),
                            queuedUpload.getStatus());
                    _uploadResult.postValue(UploadResult.success(purpose, queued));
                    if (autoProcess) {
                        uploadRepository.runPendingUploads(queueResult -> {
                            if (!queueResult.isSuccess() && queueResult.getError() != null) {
                                _uploadResult.postValue(UploadResult.error(
                                        purpose,
                                        queueResult.getError().getMessage()));
                            }
                        });
                    }
                } else {
                    String errorMsg = result.getError() != null
                            ? result.getError().getMessage()
                            : "Không lưu được upload vào hàng đợi";
                    _uploadResult.postValue(UploadResult.error(purpose, errorMsg));
                }
            });
        } catch (Exception e) {
            _isUploading.postValue(false);
            _uploadResult.postValue(
                    UploadResult.error(purpose, "Không đọc được file: " + e.getMessage()));
        }
    }

    // ─── KYC upload state (dùng cho màn "Đang tải hồ sơ lên") ───

    /**
     * true = còn pending trong Room DB; false = đã upload xong hoặc lỗi vĩnh viễn
     */
    private final MutableLiveData<Boolean> _hasPendingKyc = new MutableLiveData<>();
    public LiveData<Boolean> hasPendingKyc = _hasPendingKyc;

    /**
     * Phát true khi Room DB báo không còn pending KYC nào → navigate sang màn "Chờ duyệt"
     */
    private final MutableLiveData<Boolean> _kycUploadDone = new MutableLiveData<>(false);
    public LiveData<Boolean> kycUploadDone = _kycUploadDone;

    /**
     * Danh sách chi tiết các upload ảnh KYC đang xử lý.
     */
    private final MutableLiveData<List<QueuedUpload>> _kycUploadsList = new MutableLiveData<>();
    public LiveData<List<QueuedUpload>> kycUploadsList = _kycUploadsList;

    /**
     * Phát true khi tiến trình upload KYC bị hủy bỏ bởi người dùng.
     */
    private final MutableLiveData<Boolean> _kycUploadCancelled = new MutableLiveData<>(false);
    public LiveData<Boolean> kycUploadCancelled = _kycUploadCancelled;

    private volatile boolean _pollingActive = false;
    private static final long POLL_INTERVAL_MS = 2_000L;

    /**
     * Kiểm tra một lần Room DB xem có KYC upload active không.
     * Dùng trong onResume() của WorkerKycFragment để redirect về màn uploading.
     */
    public void checkHasPendingKyc() {
        uploadRepository.hasSubmittedActiveUploads(
                com.fixit.feature.upload.domain.model.UploadTargetType.WORKER_KYC,
                result -> {
                    if (result.isSuccess()) {
                        _hasPendingKyc.postValue(result.getData());
                    }
                });
    }

    /**
     * Bắt đầu polling Room DB mỗi 2 giây.
     * Khi count = 0 → phát kycUploadDone=true → màn Uploading navigate sang màn Pending.
     * Gọi trong onResume() của WorkerKycUploadingFragment.
     */
    public void startKycUploadPolling() {
        if (_pollingActive)
            return;
        _pollingActive = true;
        _kycUploadCancelled.postValue(false);
        _kycUploadDone.postValue(false);
        new Thread(() -> {
            while (_pollingActive) {
                uploadRepository.getSubmittedActiveUploads(
                        com.fixit.feature.upload.domain.model.UploadTargetType.WORKER_KYC,
                        result -> {
                            if (result.isSuccess()) {
                                List<QueuedUpload> list = result.getData();
                                _kycUploadsList.postValue(list);
                                if (list.isEmpty()) {
                                    _pollingActive = false;
                                    _kycUploadDone.postValue(true);
                                }
                            }
                        });
                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    public void retryKycGroup(String groupId) {
        _isUploading.postValue(true);
        uploadRepository.retryKycGroup(groupId, result -> {
            _isUploading.postValue(false);
            if (result.isSuccess()) {
                startKycUploadPolling();
            }
        });
    }

    public void cancelKycGroup(String groupId) {
        _pollingActive = false;
        uploadRepository.cancelKycGroup(groupId, result -> {
            _kycUploadsList.postValue(new ArrayList<>());
            _kycUploadCancelled.postValue(true);
        });
    }

    /** Dừng polling khi fragment bị destroy (tránh leak). */
    public void stopKycUploadPolling() {
        _pollingActive = false;
    }

    /**
     * Kích hoạt toàn bộ hàng đợi upload: upload Cloudinary → confirm → consume.
     * Chạy trên background thread, không block UI.
     */
    public void processQueue() {
        uploadRepository.runPendingUploads(result -> {
            // Chạy ngầm, không cần notify UI ở đây
        });
    }

    /**
     * Kích hoạt chuyển đổi các nháp trong nhóm sang trạng thái LOCAL_SELECTED để bắt đầu tải lên.
     */
    public void scheduleKycUpload(String groupId) {
        uploadRepository.submitKycGroup(groupId, result -> {
            // Fire-and-forget: màn Pending sẽ tự refresh khi backend cập nhật trạng thái KYC.
        });
    }

    /**
     * Xóa sạch các ảnh nháp cũ và các ảnh kẹt của các lần trước.
     */
    public void clearStaleKycUploads(Runnable onComplete) {
        uploadRepository.clearStaleUploads(
                com.fixit.feature.upload.domain.model.UploadTargetType.WORKER_KYC,
                900_000L, // 15 phút
                result -> {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
        );
    }

    /**
     * Xử lý hàng đợi và phát kết quả ra queueProcessResult.
     * Dùng khi màn hình cần chờ upload xong mới tiếp tục (không dùng cho KYC).
     */
    public void processQueueWithResult() {
        _isUploading.postValue(true);
        uploadRepository.runPendingUploads(result -> {
            _isUploading.postValue(false);
            if (result.isSuccess()) {
                _queueProcessResult.postValue(UploadResult.success("QUEUE", null));
            } else {
                String message = result.getError() != null
                        ? result.getError().getMessage()
                        : "Xử lý hàng đợi upload thất bại";
                _queueProcessResult.postValue(UploadResult.error("QUEUE", message));
            }
        });
    }

    /**
     * Xóa danh sách upload đã confirmed (dùng khi chuyển sang form mới).
     */
    public void clearConfirmedUploads() {
        _confirmedUploads.postValue(new ArrayList<>());
    }

    /**
     * Lấy danh sách fileUrl đã upload (tiện cho việc gửi kèm request tạo
     * booking/complaint).
     */
    public List<String> getConfirmedFileUrls() {
        List<String> urls = new ArrayList<>();
        List<ConfirmedUpload> list = _confirmedUploads.getValue();
        if (list != null) {
            for (ConfirmedUpload upload : list) {
                urls.add(upload.getFileUrl());
            }
        }
        return urls;
    }

    private synchronized void addToConfirmedList(ConfirmedUpload confirmed) {
        List<ConfirmedUpload> current = _confirmedUploads.getValue();
        if (current == null) {
            current = new ArrayList<>();
        }
        List<ConfirmedUpload> updated = new ArrayList<>(current);
        updated.add(confirmed);
        _confirmedUploads.postValue(updated);
    }
}
