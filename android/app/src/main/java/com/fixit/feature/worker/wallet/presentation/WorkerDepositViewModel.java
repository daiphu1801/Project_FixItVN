package com.fixit.feature.worker.wallet.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;
import com.fixit.feature.worker.wallet.domain.usecase.CreateDepositUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.GetDepositQrUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerDepositViewModel extends BaseViewModel {

    private final CreateDepositUseCase createDepositUseCase;
    private final GetDepositQrUseCase getDepositQrUseCase;
    private final WorkerWalletRepository repository;

    private final MutableLiveData<String> _transactionId = new MutableLiveData<>();
    public LiveData<String> transactionId = _transactionId;

    private final MutableLiveData<String> _qrCodeUrl = new MutableLiveData<>();
    public LiveData<String> qrCodeUrl = _qrCodeUrl;

    private final MutableLiveData<Long> _amount = new MutableLiveData<>(0L);
    public LiveData<Long> amount = _amount;

    private final MutableLiveData<String> _status = new MutableLiveData<>("PENDING");
    public LiveData<String> status = _status;

    @Inject
    public WorkerDepositViewModel(CreateDepositUseCase createDepositUseCase,
                                  GetDepositQrUseCase getDepositQrUseCase,
                                  WorkerWalletRepository repository) {
        this.createDepositUseCase = createDepositUseCase;
        this.getDepositQrUseCase = getDepositQrUseCase;
        this.repository = repository;
    }

    public void generateQr(long amt) {
        _amount.setValue(amt);
        _status.setValue("PENDING");
        String txId = createDepositUseCase.execute(amt, "Thanh toán nợ chiết khấu");
        _transactionId.setValue(txId);

        String qrUrl = getDepositQrUseCase.execute(txId);
        _qrCodeUrl.setValue(qrUrl);
    }

    public void simulateSuccess() {
        String txId = _transactionId.getValue();
        if (txId != null) {
            repository.simulateDepositSuccess(txId);
            _status.setValue("SUCCESS");
        }
    }
}
