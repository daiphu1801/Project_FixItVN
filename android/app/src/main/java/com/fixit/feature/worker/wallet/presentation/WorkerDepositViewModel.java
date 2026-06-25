// PATH: android/app/src/main/java/com/fixit/feature/worker/wallet/presentation/WorkerDepositViewModel.java

package com.fixit.feature.worker.wallet.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.wallet.domain.usecase.CreateDepositUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.GetDepositDetailUseCase;

import com.fixit.feature.worker.wallet.domain.usecase.CancelDepositUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerDepositViewModel extends BaseViewModel {

    private final CreateDepositUseCase createDepositUseCase;
    private final GetDepositDetailUseCase getDepositDetailUseCase;
    private final CancelDepositUseCase cancelDepositUseCase;

    private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable pollingRunnable;

    private final MutableLiveData<String> _transactionId = new MutableLiveData<>();
    public LiveData<String> transactionId = _transactionId;

    private final MutableLiveData<String> _qrCodeUrl = new MutableLiveData<>();
    public LiveData<String> qrCodeUrl = _qrCodeUrl;

    // ← THÊM MỚI: nội dung chuyển khoản từ server
    private final MutableLiveData<String> _transferContent = new MutableLiveData<>();
    public LiveData<String> transferContent = _transferContent;

    private final MutableLiveData<Long> _amount = new MutableLiveData<>(0L);
    public LiveData<Long> amount = _amount;

    private final MutableLiveData<String> _status = new MutableLiveData<>("PENDING");
    public LiveData<String> status = _status;

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>(false);
    public LiveData<Boolean> loading = _loading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public LiveData<String> toastMessage = _toastMessage;

    @Inject
    public WorkerDepositViewModel(
            CreateDepositUseCase createDepositUseCase,
            GetDepositDetailUseCase getDepositDetailUseCase,
            CancelDepositUseCase cancelDepositUseCase
    ) {
        this.createDepositUseCase = createDepositUseCase;
        this.getDepositDetailUseCase = getDepositDetailUseCase;
        this.cancelDepositUseCase = cancelDepositUseCase;
    }

    public void generateQr(long amt) {
        _loading.setValue(true);
        _amount.setValue(amt);
        _status.setValue("PENDING");
        _error.setValue(null);

        createDepositUseCase.execute(amt, result -> {
            _loading.postValue(false);
            if (!result.isSuccess()) {
                String msg = result.getError() != null
                        ? result.getError().getMessage()
                        : "Tạo lệnh nạp tiền thất bại";
                _error.postValue(msg);
                return;
            }

            var deposit = result.getData();
            _transactionId.postValue(deposit.getTransactionId());
            _status.postValue(deposit.getStatus());

            // QR được trả kèm ngay trong response khi status = Pending
            if (deposit.getQr() != null) {
                _qrCodeUrl.postValue(deposit.getQr().getQrUrl());
                _transferContent.postValue(deposit.getQr().getTransferContent());
            }

            if (deposit.getTransactionId() != null && "PENDING".equalsIgnoreCase(deposit.getStatus())) {
                startPolling(deposit.getTransactionId());
            }
        });
    }

    public void loadDeposit(String txId) {
        _loading.setValue(true);
        _status.setValue("PENDING");
        _error.setValue(null);

        getDepositDetailUseCase.execute(txId, result -> {
            _loading.postValue(false);
            if (!result.isSuccess() || result.getData() == null) {
                String msg = result.getError() != null
                        ? result.getError().getMessage()
                        : "Không tìm thấy thông tin giao dịch";
                _error.postValue(msg);
                return;
            }

            var deposit = result.getData();
            _transactionId.postValue(deposit.getTransactionId());
            _status.postValue(deposit.getStatus());
            if (deposit.getAmount() != null) {
                _amount.postValue(deposit.getAmount().longValue());
            }

            if (deposit.getQr() != null) {
                _qrCodeUrl.postValue(deposit.getQr().getQrUrl());
                _transferContent.postValue(deposit.getQr().getTransferContent());
            }

            if (deposit.getTransactionId() != null && "PENDING".equalsIgnoreCase(deposit.getStatus())) {
                startPolling(deposit.getTransactionId());
            }
        });
    }

    private void startPolling(String txId) {
        stopPolling();
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                checkDepositStatus(txId);
            }
        };
        handler.postDelayed(pollingRunnable, 10000); // 10 seconds
    }

    public void stopPolling() {
        if (pollingRunnable != null) {
            handler.removeCallbacks(pollingRunnable);
            pollingRunnable = null;
        }
    }

    private void checkDepositStatus(String txId) {
        getDepositDetailUseCase.execute(txId, result -> {
            if (result.isSuccess() && result.getData() != null) {
                String currentStatus = result.getData().getStatus();
                _status.postValue(currentStatus);
                if ("SUCCESS".equalsIgnoreCase(currentStatus) ||
                        "FAILED".equalsIgnoreCase(currentStatus) ||
                        "CANCELLED".equalsIgnoreCase(currentStatus)) {
                    stopPolling();
                } else {
                    // Tiếp tục polling
                    if (pollingRunnable != null) {
                        handler.postDelayed(pollingRunnable, 10000);
                    }
                }
            } else {
                // Lỗi kết nối tạm thời, tiếp tục polling
                if (pollingRunnable != null) {
                    handler.postDelayed(pollingRunnable, 10000);
                }
            }
        });
    }

    public void checkDepositStatusManual() {
        String txId = _transactionId.getValue();
        if (txId == null || txId.isEmpty()) return;

        _loading.setValue(true);
        _error.setValue(null);
        _toastMessage.setValue(null);
        getDepositDetailUseCase.execute(txId, result -> {
            _loading.postValue(false);
            if (result.isSuccess() && result.getData() != null) {
                String currentStatus = result.getData().getStatus();
                _status.postValue(currentStatus);
                if ("PENDING".equalsIgnoreCase(currentStatus)) {
                    _toastMessage.postValue("Giao dịch đang chờ thanh toán. Vui lòng thử lại sau.");
                } else if ("SUCCESS".equalsIgnoreCase(currentStatus)) {
                    _toastMessage.postValue("Thanh toán thành công!");
                    stopPolling();
                } else if ("CANCELLED".equalsIgnoreCase(currentStatus)) {
                    _toastMessage.postValue("Giao dịch này đã bị hủy.");
                    stopPolling();
                } else if ("FAILED".equalsIgnoreCase(currentStatus)) {
                    _toastMessage.postValue("Giao dịch thất bại.");
                    stopPolling();
                }
            } else {
                String msg = result.getError() != null
                        ? result.getError().getMessage()
                        : "Không thể kết nối đến server để kiểm tra trạng thái";
                _error.postValue(msg);
            }
        });
    }

    public void cancelDeposit(Runnable onComplete) {
        String txId = _transactionId.getValue();
        if (txId == null || txId.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        _loading.setValue(true);
        _error.setValue(null);
        stopPolling();
        cancelDepositUseCase.execute(txId, result -> {
            _loading.postValue(false);
            if (result.isSuccess()) {
                _status.postValue("CANCELLED");
            } else {
                String msg = result.getError() != null
                        ? result.getError().getMessage()
                        : "Không thể hủy giao dịch";
                _error.postValue(msg);
            }
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopPolling();
    }
}
