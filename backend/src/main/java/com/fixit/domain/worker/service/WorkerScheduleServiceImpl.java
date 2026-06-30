package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.response.WorkerScheduleResponse;
import com.fixit.domain.worker.repository.projection.WorkerScheduleItemProjection;
import com.fixit.domain.worker.repository.query.WorkerScheduleQueryRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerScheduleServiceImpl implements WorkerScheduleService {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Bangkok");

    private final CurrentWorkerResolver currentWorkerResolver;
    private final WorkerScheduleQueryRepository workerScheduleQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public WorkerScheduleResponse getMySchedule(LocalDate date) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        List<WorkerScheduleItemProjection> projections;

        if (date != null) {
            // Có date cụ thể → lọc theo ngày (dùng cho lịch hẹn trang chủ)
            projections = workerScheduleQueryRepository.findScheduleByWorkerIdAndDate(workerId, date);
        } else {
            // Không có date → trả tất cả đơn active (dùng cho tab Đơn hàng trên Android)
            projections = workerScheduleQueryRepository.findActiveByWorkerId(workerId);
        }

        List<WorkerScheduleResponse.ScheduleItem> items = projections.stream()
                .map(this::toScheduleItem)
                .toList();

        return WorkerScheduleResponse.builder()
                .date(date)
                .totalItems(items.size())
                .empty(items.isEmpty())
                .items(items)
                .build();
    }

    private WorkerScheduleResponse.ScheduleItem toScheduleItem(WorkerScheduleItemProjection p) {
        return WorkerScheduleResponse.ScheduleItem.builder()
                .bookingId(p.getBookingId())
                .serviceName(p.getServiceName())
                .customerName(p.getCustomerName())
                .address(p.getAddress())
                .status(p.getStatus())
                .statusText(toStatusText(p.getStatus()))
                .scheduledTime((p.getScheduledTime()))
                .finalPrice(p.getFinalPrice())
                .paymentMethod(p.getPaymentMethod())
                .issueDescription(p.getIssueDescription())
                .build();
    }

    private String formatDateTime(OffsetDateTime dt) {
        if (dt == null) return null;
        return dt.atZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"))
                 .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    private String toStatusText(String status) {
        if (status == null) {
            return "Không xác định";
        }

        return switch (status) {
            case "Accepted" -> "Đã nhận đơn";
            case "Surveying" -> "Đang khảo sát";
            case "Waiting_Approval" -> "Chờ khách nghiệm thu";
            case "Waiting_Payment" -> "Chờ xác nhận nhận tiền";
            case "In_Progress" -> "Đang sửa chữa";
            case "Completed" -> "Hoàn thành";
            case "Cancelled" -> "Đã hủy";
            default -> status;
        };
    }
}