package com.fixit.feature.worker.orders.data.repository;

import com.fixit.feature.worker.orders.domain.model.ExtraCostItem;
import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkerOrdersRepositoryImpl implements WorkerOrdersRepository {
    private static final String ADMIN_BANK_ID = "MB";
    private static final String ADMIN_ACCOUNT_NO = "0859226688";
    private static final String ADMIN_ACCOUNT_NAME = "CONG TY FIXIT VN";

    private final List<WorkerOrder> orders;
    private List<ExtraCostItem> extraCosts = new ArrayList<>();

    @Inject
    public WorkerOrdersRepositoryImpl() {
        orders = createMockOrders();
    }

    @Override
    public List<WorkerOrder> getOrders() {
        return orders;
    }

    @Override
    public List<WorkerOrder> filterOrders(String status) {
        if ("history".equals(status)) {
            return orders.stream()
                    .filter(order -> "completed".equals(order.getStatus())
                            || "cancelled".equals(order.getStatus()))
                    .collect(Collectors.toList());
        }
        return orders.stream()
                .filter(order -> status.equals(order.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public WorkerOrder getOrderById(String orderId) {
        return orders.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public JobStatus getInitialStatus(String orderStatus) {
        if ("ongoing".equals(orderStatus)) {
            return JobStatus.SURVEYING;
        }
        return JobStatus.ACCEPTED;
    }

    @Override
    public JobStatus advanceStatus(JobStatus currentStatus) {
        if (currentStatus == null) {
            return null;
        }

        switch (currentStatus) {
            case ACCEPTED:
                return JobStatus.ARRIVING;
            case ARRIVING:
                return JobStatus.SURVEYING;
            case SURVEYING:
                return JobStatus.REPAIRING;
            case REPAIRING:
                return JobStatus.COMPLETED;
            default:
                return currentStatus;
        }
    }

    @Override
    public void saveExtraCosts(List<ExtraCostItem> items) {
        extraCosts = items == null ? new ArrayList<>() : new ArrayList<>(items);
    }

    @Override
    public List<ExtraCostItem> getExtraCosts() {
        return extraCosts;
    }

    @Override
    public long calculateTotalExtra() {
        long total = 0;
        for (ExtraCostItem item : extraCosts) {
            total += item.getTotal();
        }
        return total;
    }

    @Override
    public String generatePaymentQrUrl(String orderId, long amount) {
        String template = "qr_only";
        String description = "FIXIT ORD " + orderId;

        try {
            return String.format("https://img.vietqr.io/image/%s-%s-%s.png?amount=%d&addInfo=%s&accountName=%s",
                    ADMIN_BANK_ID,
                    ADMIN_ACCOUNT_NO,
                    template,
                    amount,
                    URLEncoder.encode(description, "UTF-8"),
                    URLEncoder.encode(ADMIN_ACCOUNT_NAME, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private List<WorkerOrder> createMockOrders() {
        List<WorkerOrder> mockOrders = Arrays.asList(
                new WorkerOrder("ORD001", "customer_b_id", "Sua ro ri ong nuoc bon rua chen",
                        "123 Nguyen Van Linh, Quan 7, TP.HCM",
                        "Hom nay 08:00", "150.000 d", "pending", "Tran Thi B"),
                new WorkerOrder("ORD002", "customer_c_id", "Thay o khoa cua chinh",
                        "78 Dinh Tien Hoang, Binh Thanh, TP.HCM",
                        "Hom nay 10:30", "200.000 d", "pending", "Le Van C"),
                new WorkerOrder("ORD003", "customer_d_id", "Sua dieu hoa khong mat",
                        "456 Le Van Sy, Quan 3, TP.HCM",
                        "Hom nay 14:00", "350.000 d", "ongoing", "Pham Thi D"),
                new WorkerOrder("ORD004", "customer_e_id", "Thong tac bon rua bat",
                        "99 Cach Mang Thang 8, Quan 10, TP.HCM",
                        "Hom qua 09:00", "120.000 d", "completed", "Nguyen Van E"),
                new WorkerOrder("ORD005", "customer_f_id", "Sua may giat khong vat",
                        "21 Phan Van Tri, Go Vap, TP.HCM",
                        "Hom qua 15:30", "250.000 d", "completed", "Hoang Thi F"),
                new WorkerOrder("ORD006", "customer_g_id", "Lap den phong ngu",
                        "5 Huynh Tan Phat, Nha Be, TP.HCM",
                        "2 ngay truoc 11:00", "80.000 d", "cancelled", "Trinh Van G"));

        WorkerOrder orderWithComplaint = mockOrders.stream()
                .filter(order -> order.getOrderId().equals("ORD004"))
                .findFirst()
                .orElse(null);
        if (orderWithComplaint != null) {
            orderWithComplaint.setComplaintStatus("pending");
            orderWithComplaint.setComplaintReason("May lanh van con keu sau khi sua, chay 2 ngay lai hong");
            orderWithComplaint.setComplaintDeadline("18:42:00");
        }
        return mockOrders;
    }
}
