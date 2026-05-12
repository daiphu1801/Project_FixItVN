package com.fixit.ui.worker.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.base.BaseViewModel;
import com.fixit.data.model.WorkerOrder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerOrdersViewModel extends BaseViewModel {

    public static class ExtraCostItem {
        public String name;
        public int quantity;
        public long unitPrice;

        public ExtraCostItem(String name, int quantity, long unitPrice) {
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public long getTotal() {
            return (long) quantity * unitPrice;
        }
    }

    public enum JobStatus {
        ACCEPTED(1, "Bắt đầu di chuyển"),
        ARRIVING(2, "Đã đến nơi"),
        SURVEYING(3, "Bắt đầu sửa chữa"),
        REPAIRING(4, "Xác nhận hoàn thành"),
        COMPLETED(5, "Đã hoàn thành");

        private final int step;
        private final String nextActionText;

        JobStatus(int step, String nextActionText) {
            this.step = step;
            this.nextActionText = nextActionText;
        }

        public int getStep() {
            return step;
        }

        public String getNextActionText() {
            return nextActionText;
        }
    }

    private final MutableLiveData<JobStatus> _currentStatus = new MutableLiveData<>(JobStatus.ACCEPTED);
    public LiveData<JobStatus> currentStatus = _currentStatus;

    private final MutableLiveData<List<ExtraCostItem>> _extraItems = new MutableLiveData<>(new java.util.ArrayList<>());
    public LiveData<List<ExtraCostItem>> extraItems = _extraItems;

    public void setExtraItems(List<ExtraCostItem> items) {
        _extraItems.setValue(items);
    }

    public long calculateTotalExtra() {
        List<ExtraCostItem> items = _extraItems.getValue();
        if (items == null)
            return 0;
        long total = 0;
        for (ExtraCostItem item : items) {
            total += item.getTotal();
        }
        return total;
    }

    public static final double COMMISSION_RATE = 0.15;
    private static final String ADMIN_BANK_ID = "MB";
    private static final String ADMIN_ACCOUNT_NO = "0859226688"; // Tài khoản Admin/Công ty
    private static final String ADMIN_ACCOUNT_NAME = "CONG TY FIXIT VN";

    public String generateVietQrUrl(String orderId, long amount) {
        String template = "qr_only";
        String description = "FIXIT ORD " + orderId;

        try {
            return String.format("https://img.vietqr.io/image/%s-%s-%s.png?amount=%d&addInfo=%s&accountName=%s",
                    ADMIN_BANK_ID, ADMIN_ACCOUNT_NO, template, amount,
                    java.net.URLEncoder.encode(description, "UTF-8"),
                    java.net.URLEncoder.encode(ADMIN_ACCOUNT_NAME, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            return "";
        }
    }

    /** Khởi tạo trạng thái dựa trên dữ liệu đơn hàng ban đầu */
    public void initializeStatus(String orderStatus) {
        if ("ongoing".equals(orderStatus)) {
            _currentStatus.setValue(JobStatus.SURVEYING);
        } else {
            _currentStatus.setValue(JobStatus.ACCEPTED);
        }
    }

    public WorkerOrder getOrderById(String orderId) {
        return ALL_MOCK_ORDERS.stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    /** Tất cả đơn hàng gốc (mock – sẽ thay bằng API sau) */
    private static final List<WorkerOrder> ALL_MOCK_ORDERS = Arrays.asList(
            new WorkerOrder("ORD001", "Sửa rò rỉ ống nước bồn rửa chén",
                    "123 Nguyễn Văn Linh, Quận 7, TP.HCM",
                    "Hôm nay 08:00", "150.000 đ", "pending", "Trần Thị B"),

            new WorkerOrder("ORD002", "Thay ổ khoá cửa chính",
                    "78 Đinh Tiên Hoàng, Bình Thạnh, TP.HCM",
                    "Hôm nay 10:30", "200.000 đ", "pending", "Lê Văn C"),

            new WorkerOrder("ORD003", "Sửa điều hoà không mát",
                    "456 Lê Văn Sỹ, Quận 3, TP.HCM",
                    "Hôm nay 14:00", "350.000 đ", "ongoing", "Phạm Thị D"),

            new WorkerOrder("ORD004", "Thông tắc bồn rửa bát",
                    "99 Cách Mạng Tháng 8, Quận 10, TP.HCM",
                    "Hôm qua 09:00", "120.000 đ", "completed", "Nguyễn Văn E"),

            new WorkerOrder("ORD005", "Sửa máy giặt không vắt",
                    "21 Phan Văn Trị, Gò Vấp, TP.HCM",
                    "Hôm qua 15:30", "250.000 đ", "completed", "Hoàng Thị F"),

            new WorkerOrder("ORD006", "Lắp đèn phòng ngủ",
                    "5 Huỳnh Tấn Phát, Nhà Bè, TP.HCM",
                    "2 ngày trước 11:00", "80.000 đ", "cancelled", "Trịnh Văn G"));

    static {
        // Mock một khiếu nại cho đơn ORD004
        WorkerOrder orderWithComplaint = ALL_MOCK_ORDERS.stream()
                .filter(o -> o.getOrderId().equals("ORD004"))
                .findFirst().orElse(null);
        if (orderWithComplaint != null) {
            orderWithComplaint.setComplaintStatus("pending");
            orderWithComplaint.setComplaintReason("Máy lạnh vẫn còn kêu rè sau khi sửa, chạy 2 ngày lại hỏng");
            orderWithComplaint.setComplaintDeadline("18:42:00");
        }
    }

    private final MutableLiveData<List<WorkerOrder>> _filteredOrders = new MutableLiveData<>();
    public LiveData<List<WorkerOrder>> filteredOrders = _filteredOrders;

    private String currentFilterStatus = "pending";

    public String getCurrentFilterStatus() {
        return currentFilterStatus;
    }

    @Inject
    public WorkerOrdersViewModel() {
        // Mặc định khởi tạo lần đầu
        filterByStatus(currentFilterStatus);
    }

    /**
     * Lọc danh sách theo tab được chọn.
     * 
     * @param status "pending" | "ongoing" | "history"
     */
    public void filterByStatus(String status) {
        this.currentFilterStatus = status;
        List<WorkerOrder> result;
        if ("history".equals(status)) {
            result = ALL_MOCK_ORDERS.stream()
                    .filter(o -> "completed".equals(o.getStatus()) || "cancelled".equals(o.getStatus()))
                    .collect(Collectors.toList());
        } else {
            result = ALL_MOCK_ORDERS.stream()
                    .filter(o -> status.equals(o.getStatus()))
                    .collect(Collectors.toList());
        }
        _filteredOrders.setValue(result);
    }

    public void advanceStatus() {
        JobStatus current = _currentStatus.getValue();
        if (current == null)
            return;

        switch (current) {
            case ACCEPTED:
                _currentStatus.setValue(JobStatus.ARRIVING);
                break;
            case ARRIVING:
                _currentStatus.setValue(JobStatus.SURVEYING);
                break;
            case SURVEYING:
                _currentStatus.setValue(JobStatus.REPAIRING);
                break;
            case REPAIRING:
                _currentStatus.setValue(JobStatus.COMPLETED);
                break;
            default:
                break;
        }
    }
}
