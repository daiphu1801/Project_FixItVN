package com.fixit.feature.customer.order.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.fixit.core.ui.BaseViewModel;
import javax.inject.Inject;
import com.fixit.core.common.Result;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;
import com.fixit.feature.customer.booking.domain.usecase.CreateBookingUseCase;
import com.fixit.feature.customer.booking.domain.usecase.GetBookingDetailUseCase;
import com.fixit.feature.customer.booking.domain.usecase.GetBookingsUseCase;
import com.fixit.feature.customer.booking.domain.usecase.CancelBookingUseCase;
import com.fixit.feature.customer.booking.domain.usecase.ProcessPaymentUseCase;
import com.fixit.feature.customer.booking.domain.usecase.SimulateBankTransferUseCase;

import java.math.BigDecimal;
import java.util.List;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CustomerOrderViewModel extends BaseViewModel {

    // 0: Trống, 1: Đang tìm thợ (Radar), 2: Đã có thợ (Chi tiết)
    private final MutableLiveData<Integer> _orderStatus = new MutableLiveData<>(0);
    public final LiveData<Integer> orderStatus = _orderStatus;

    private final MutableLiveData<CustomerBooking> _currentBooking = new MutableLiveData<>();
    public final LiveData<CustomerBooking> currentBooking = _currentBooking;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    // Signal để Fragment biết cần hiển thị QuotationBottomSheet
    private final MutableLiveData<Boolean> _showQuotation = new MutableLiveData<>(false);
    public final LiveData<Boolean> showQuotation = _showQuotation;

    // Tránh hiển thị quotation nhiều lần cho cùng một quotationId
    private String lastShownQuotationId = null;

    private final CreateBookingUseCase createBookingUseCase;
    private final GetBookingDetailUseCase getBookingDetailUseCase;
    private final GetBookingsUseCase getBookingsUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final ProcessPaymentUseCase processPaymentUseCase;
    private final SimulateBankTransferUseCase simulateBankTransferUseCase;

    private String currentBookingId;

    @Inject
    public CustomerOrderViewModel(
            CreateBookingUseCase createBookingUseCase,
            GetBookingDetailUseCase getBookingDetailUseCase,
            GetBookingsUseCase getBookingsUseCase,
            CancelBookingUseCase cancelBookingUseCase,
            ProcessPaymentUseCase processPaymentUseCase,
            SimulateBankTransferUseCase simulateBankTransferUseCase) {
        this.createBookingUseCase = createBookingUseCase;
        this.getBookingDetailUseCase = getBookingDetailUseCase;
        this.getBookingsUseCase = getBookingsUseCase;
        this.cancelBookingUseCase = cancelBookingUseCase;
        this.processPaymentUseCase = processPaymentUseCase;
        this.simulateBankTransferUseCase = simulateBankTransferUseCase;
    }

    private boolean isPolling = false;
    private final android.os.Handler pollHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            executePoll();
        }
    };

    public void setStatus(int status) {
        _orderStatus.setValue(status);
    }

    public void cancelOrder() {
        stopPolling();
        _orderStatus.setValue(0);
        currentBookingId = null;
        _currentBooking.setValue(null);
        lastShownQuotationId = null;
    }

    public void startFinding() {
        _orderStatus.setValue(1);
    }

    public void onWorkerAccepted() {
        _orderStatus.setValue(2);
    }

    /** Được gọi từ Fragment sau khi QuotationBottomSheet đã hiển thị để tránh show lại */
    public void clearShowQuotation() {
        _showQuotation.setValue(false);
    }

    public void createBooking(Integer serviceId, String address, BigDecimal lat, BigDecimal lng, String issueDescription, String paymentMethod) {
        setLoading(true);
        createBookingUseCase.execute(serviceId, address, lat, lng, issueDescription, paymentMethod, result -> {
            setLoading(false);
            if (result != null && result.isSuccess()) {
                CustomerBooking booking = result.getData();
                currentBookingId = booking.getBookingId();
                _currentBooking.setValue(booking);
                startFinding();
                pollBookingDetail(); // Start polling
            } else if (result != null) {
                _error.setValue(result.getError().getMessage());
            }
        });
    }

    public void checkActiveBooking() {
        setLoading(true);
        getBookingsUseCase.execute(result -> {
            setLoading(false);
            if (result != null && result.isSuccess() && result.getData() != null) {
                List<CustomerBooking> bookings = result.getData();
                CustomerBooking activeBooking = null;
                for (CustomerBooking booking : bookings) {
                    String status = booking.getStatus() != null ? booking.getStatus().toLowerCase() : "";
                    if (!"completed".equals(status) && !"cancelled".equals(status)) {
                        activeBooking = booking;
                        break;
                    }
                }

                if (activeBooking != null) {
                    currentBookingId = activeBooking.getBookingId();
                    _currentBooking.setValue(activeBooking);
                    String status = activeBooking.getStatus().toLowerCase();
                    if ("pending".equals(status)) {
                        startFinding();
                    } else {
                        onWorkerAccepted();
                    }
                    pollBookingDetail();
                } else {
                    cancelOrder();
                }
            } else {
                cancelOrder();
            }
        });
    }

    public void loadBooking(String bookingId) {
        stopPolling();
        this.currentBookingId = bookingId;
        setLoading(true);
        getBookingDetailUseCase.execute(bookingId, result -> {
            setLoading(false);
            if (result != null && result.isSuccess()) {
                CustomerBooking booking = result.getData();
                _currentBooking.setValue(booking);
                
                String status = booking.getStatus() != null ? booking.getStatus().toLowerCase() : "";
                if (!"completed".equals(status) && !"cancelled".equals(status)) {
                    if ("pending".equals(status)) {
                        startFinding();
                    } else {
                        onWorkerAccepted();
                    }
                    pollBookingDetail();
                } else {
                    _orderStatus.setValue(0);
                }
            } else if (result != null) {
                _error.setValue(result.getError().getMessage());
            }
        });
    }

    public void pollBookingDetail() {
        if (currentBookingId == null) {
            stopPolling();
            return;
        }
        if (isPolling) return;
        isPolling = true;
        executePoll();
    }

    public void stopPolling() {
        isPolling = false;
        pollHandler.removeCallbacks(pollRunnable);
    }

    private void executePoll() {
        if (currentBookingId == null || !isPolling) {
            isPolling = false;
            return;
        }

        getBookingDetailUseCase.execute(currentBookingId, result -> {
            if (!isPolling) return;
            
            if (result != null && result.isSuccess()) {
                CustomerBooking booking = result.getData();
                _currentBooking.setValue(booking);

                String status = booking.getStatus() != null ? booking.getStatus().toLowerCase() : "";

                // Tất cả các trạng thái cần hiển thị màn hình chi tiết
                if ("accepted".equals(status) || "surveying".equals(status) ||
                    "in_progress".equals(status) || "waiting_approval".equals(status) ||
                    "waiting_payment".equals(status)) {
                    onWorkerAccepted();
                    pollHandler.postDelayed(pollRunnable, 3000);
                } else if ("pending".equals(status)) {
                    startFinding();
                    pollHandler.postDelayed(pollRunnable, 3000);
                } else {
                    stopPolling();
                    if ("completed".equals(status) || "cancelled".equals(status)) {
                        _orderStatus.setValue(0);
                        _currentBooking.setValue(null);
                        currentBookingId = null;
                    }
                }

                // Khi status = Surveying và có quotation mới → trigger show QuotationBottomSheet
                if ("surveying".equals(status)) {
                    String quotationId = booking.getQuotationId();
                    if (quotationId != null && !quotationId.equals(lastShownQuotationId)) {
                        lastShownQuotationId = quotationId;
                        _showQuotation.postValue(true);
                    }
                }
            } else {
                stopPolling();
                if (result != null) {
                    _error.setValue(result.getError().getMessage());
                }
            }
        });
    }

    private final MutableLiveData<List<CustomerBooking>> _bookingHistory = new MutableLiveData<>();
    public final LiveData<List<CustomerBooking>> bookingHistory = _bookingHistory;

    public void fetchBookings() {
        setLoading(true);
        getBookingsUseCase.execute(result -> {
            setLoading(false);
            if (result != null && result.isSuccess()) {
                _bookingHistory.setValue(result.getData());
            } else if (result != null) {
                _error.setValue(result.getError().getMessage());
            }
        });
    }

    public void cancelCurrentBooking(String reason, boolean isWorkerFault) {
        if (currentBookingId == null) return;
        setLoading(true);
        cancelBookingUseCase.execute(currentBookingId, reason, isWorkerFault, result -> {
            setLoading(false);
            if (result != null && result.isSuccess()) {
                if (isWorkerFault) {
                    // Cần ghép thợ khác, trạng thái về PENDING
                    startFinding();
                    pollBookingDetail();
                } else {
                    cancelOrder();
                }
            } else if (result != null) {
                _error.setValue(result.getError().getMessage());
            }
        });
    }

    public void confirmAndPayBooking(String bookingId, String paymentMethod, com.fixit.core.common.ResultCallback<Void> callback) {
        setLoading(true);
        processPaymentUseCase.execute(bookingId, paymentMethod, result -> {
            setLoading(false);
            if (result != null && result.isSuccess()) {
                cancelOrder();
            }
            callback.onResult(result);
        });
    }

    public void simulateBankTransfer(String bookingId, com.fixit.core.common.ResultCallback<Void> callback) {
        setLoading(true);
        simulateBankTransferUseCase.execute(bookingId, result -> {
            setLoading(false);
            if (result != null && result.isSuccess()) {
                cancelOrder();
            }
            callback.onResult(result);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopPolling();
    }
}
