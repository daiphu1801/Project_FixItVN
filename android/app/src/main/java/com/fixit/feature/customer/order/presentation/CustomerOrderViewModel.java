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

    private final CreateBookingUseCase createBookingUseCase;
    private final GetBookingDetailUseCase getBookingDetailUseCase;
    private final GetBookingsUseCase getBookingsUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final ProcessPaymentUseCase processPaymentUseCase;

    private String currentBookingId;

    @Inject
    public CustomerOrderViewModel(
            CreateBookingUseCase createBookingUseCase, 
            GetBookingDetailUseCase getBookingDetailUseCase,
            GetBookingsUseCase getBookingsUseCase,
            CancelBookingUseCase cancelBookingUseCase,
            ProcessPaymentUseCase processPaymentUseCase) {
        this.createBookingUseCase = createBookingUseCase;
        this.getBookingDetailUseCase = getBookingDetailUseCase;
        this.getBookingsUseCase = getBookingsUseCase;
        this.cancelBookingUseCase = cancelBookingUseCase;
        this.processPaymentUseCase = processPaymentUseCase;
    }

    public void setStatus(int status) {
        _orderStatus.setValue(status);
    }

    public void cancelOrder() {
        _orderStatus.setValue(0);
        currentBookingId = null;
        _currentBooking.setValue(null);
    }

    public void startFinding() {
        _orderStatus.setValue(1);
    }

    public void onWorkerAccepted() {
        _orderStatus.setValue(2);
    }

    public void createBooking(Integer serviceId, String address, BigDecimal lat, BigDecimal lng, String issueDescription) {
        setLoading(true);
        createBookingUseCase.execute(serviceId, address, lat, lng, issueDescription, result -> {
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

    public void pollBookingDetail() {
        if (currentBookingId == null) return;
        
        getBookingDetailUseCase.execute(currentBookingId, result -> {
            if (result != null && result.isSuccess()) {
                CustomerBooking booking = result.getData();
                _currentBooking.setValue(booking);
                
                // Cập nhật UI dựa vào trạng thái booking
                if ("Accepted".equalsIgnoreCase(booking.getStatus()) || "Surveying".equalsIgnoreCase(booking.getStatus()) || 
                    "In_Progress".equalsIgnoreCase(booking.getStatus()) || "Waiting_Approval".equalsIgnoreCase(booking.getStatus())) {
                    onWorkerAccepted();
                } else if ("Pending".equalsIgnoreCase(booking.getStatus())) {
                    // Tiếp tục gọi lại sau 3 giây nếu vẫn Pending
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::pollBookingDetail, 3000);
                }
            } else if (result != null) {
                _error.setValue(result.getError().getMessage());
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
                    pollBookingDetail(); // Tiếp tục hỏi API
                } else {
                    // Hủy thành công
                    cancelOrder();
                }
            } else if (result != null) {
                _error.setValue(result.getError().getMessage());
            }
        });
    }

    public void confirmAndPayBooking(String bookingId, com.fixit.core.common.ResultCallback<Void> callback) {
        setLoading(true);
        processPaymentUseCase.execute(bookingId, result -> {
            setLoading(false);
            if (result != null && result.isSuccess()) {
                cancelOrder();
            }
            callback.onResult(result);
        });
    }
}

