package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.request.WorkerLocationUpdateRequest;
import com.fixit.domain.worker.dto.request.WorkerStatusUpdateRequest;
import com.fixit.domain.worker.dto.response.WorkerHomeResponse;
import com.fixit.domain.worker.repository.query.WorkerHomeQueryRepository;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.domain.worker.repository.projection.*;
import com.fixit.domain.worker.support.CurrentWorkerResolver;

import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Propagation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerHomeServiceImpl implements WorkerHomeService {

    private final WorkerRepository workerRepository;
    private final WorkerHomeQueryRepository workerHomeQueryRepository;
    private final CurrentWorkerResolver currentWorkerResolver;

    // 1. Inject StringRedisTemplate của Spring
    private final StringRedisTemplate redisTemplate;

    // 2. Định nghĩa Key lưu tọa độ thợ trên Redis
    private static final String WORKERS_LOCATION_KEY = "workers:locations";

    @Override
    @Transactional(readOnly = true)
    public WorkerHomeResponse getHome() {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        return buildHomeResponse(workerId);
    }

    @Override
    @Transactional
    public WorkerHomeResponse updateStatus(WorkerStatusUpdateRequest request) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        WorkerDashboardSummaryProjection summary = getSummary(workerId);

        if (Boolean.TRUE.equals(request.getAvailable())) {
            validateCanGoOnline(summary);
        }

        int updatedRows = workerRepository.updateAvailability(workerId, request.getAvailable());

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Không thể cập nhật trạng thái thợ");
        }

        return buildHomeResponse(workerId);
    }

    @Override
    public void updateLocation(WorkerLocationUpdateRequest request) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        double latitude = request.getLatitude().doubleValue();
        double longitude = request.getLongitude().doubleValue();
        /*
         * LƯU Ý QUAN TRỌNG:
         * Trong thư viện Geo của Spring/Redis:
         * - Tham số thứ nhất của Point là LONGITUDE (Kinh độ - Trục X)
         * - Tham số thứ hai của Point là LATITUDE (Vĩ độ - Trục Y)
         * Truyền ngược sẽ dẫn tới tính sai khoảng cách địa lý.
         */
        Point location = new Point(longitude, latitude);
        // Ghi vị trí của thợ vào Redis Geospatial
        redisTemplate.opsForGeo().add(
                WORKERS_LOCATION_KEY,
                location,
                workerId.toString());
    }

    private WorkerHomeResponse buildHomeResponse(UUID workerId) {
        WorkerDashboardSummaryProjection summary = getSummary(workerId);

        WorkerActiveBookingProjection activeOrder = workerHomeQueryRepository
                .findActiveOrder(workerId)
                .orElse(null);

        WorkerPerformanceStatsProjection stats = workerHomeQueryRepository.findStatsOverview(workerId);

        List<WorkerIncomeChartPointProjection> chart = workerHomeQueryRepository
                .findIncomeChartLast7Days(workerId);

        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

        OffsetDateTime startOfDay = LocalDate.now(zoneId)
                .atStartOfDay(zoneId)
                .toOffsetDateTime();

        OffsetDateTime endOfDay = startOfDay.plusDays(1);

        List<WorkerScheduleItemProjection> appointments = workerHomeQueryRepository
                .findTodayAppointments(workerId, startOfDay, endOfDay);

        boolean canReceiveJob = calculateCanReceiveJob(summary);
        String blockedReason = buildBlockedReason(summary);

        return WorkerHomeResponse.builder()
                .workerId(summary.getWorkerId())
                .fullName(summary.getFullName())
                .avatarUrl(summary.getAvatarUrl())
                .greetingText("Xin chào,")

                .hasUnreadNotification(summary.getUnreadNotificationCount() != null
                        && summary.getUnreadNotificationCount() > 0)
                .unreadNotificationCount(summary.getUnreadNotificationCount())

                .available(summary.getAvailable())
                .statusText(Boolean.TRUE.equals(summary.getAvailable()) ? "ONLINE" : "OFFLINE")
                .statusHelpText("Nhấn để thay đổi trạng thái →")

                .verificationStatus(summary.getVerificationStatus())
                .reputationScore(summary.getReputationScore())
                .latitude(summary.getLatitude())
                .longitude(summary.getLongitude())

                .canReceiveJob(canReceiveJob)
                .receiveJobBlockedReason(blockedReason)

                .todayAppointmentCount(appointments.size())
                .pendingAssignmentCount(summary.getPendingAssignmentCount())

                .availableBalance(defaultMoney(summary.getAvailableBalance()))
                .heldBalance(defaultMoney(summary.getHeldBalance()))
                .debtBalance(defaultMoney(summary.getDebtBalance()))

                .activeOrder(toActiveOrder(activeOrder))
                .statsOverview(toStats(stats))
                .incomeChart(toIncomeChart(chart))
                .todayAppointments(toAppointments(appointments))
                .build();
    }

    private WorkerDashboardSummaryProjection getSummary(UUID workerId) {
        return workerRepository.findHomeSummaryByWorkerId(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thợ hiện tại"));
    }

    private void validateCanGoOnline(WorkerDashboardSummaryProjection summary) {
        if (!"Approved".equalsIgnoreCase(summary.getVerificationStatus())) {
            throw new IllegalStateException("Hồ sơ thợ chưa được duyệt, không thể bật nhận việc");
        }

        if (summary.getDebtBalance() != null && summary.getDebtBalance().signum() > 0) {
            throw new IllegalStateException("Thợ đang có khoản nợ, không thể bật nhận việc");
        }

        if (summary.getLatitude() == null || summary.getLongitude() == null) {
            throw new IllegalStateException("Thiếu vị trí hiện tại, không thể bật nhận việc");
        }
    }

    private boolean calculateCanReceiveJob(WorkerDashboardSummaryProjection summary) {
        return "Approved".equalsIgnoreCase(summary.getVerificationStatus())
                && defaultMoney(summary.getDebtBalance()).signum() == 0
                && summary.getLatitude() != null
                && summary.getLongitude() != null;
    }

    private String buildBlockedReason(WorkerDashboardSummaryProjection summary) {
        if (!"Approved".equalsIgnoreCase(summary.getVerificationStatus())) {
            return "Hồ sơ thợ chưa được duyệt";
        }

        if (defaultMoney(summary.getDebtBalance()).signum() > 0) {
            return "Bạn đang có khoản nợ cần thanh toán";
        }

        if (summary.getLatitude() == null || summary.getLongitude() == null) {
            return "Bạn cần cập nhật vị trí trước khi nhận việc";
        }

        return null;
    }

    private WorkerHomeResponse.ActiveOrderSummary toActiveOrder(
            WorkerActiveBookingProjection p) {
        if (p == null) {
            return null;
        }

        return WorkerHomeResponse.ActiveOrderSummary.builder()
                .bookingId(p.getBookingId())
                .serviceName(p.getServiceName())
                .customerName(p.getCustomerName())
                .address(p.getAddress())
                .status(p.getStatus())
                .statusText(toBookingStatusText(p.getStatus()))
                .scheduledTime(p.getScheduledTime())
                .finalPrice(p.getFinalPrice())
                .nextAction(toNextAction(p.getStatus()))
                .build();
    }

    private WorkerHomeResponse.WorkerStatsOverview toStats(WorkerPerformanceStatsProjection p) {
        if (p == null) {
            return null;
        }

        return WorkerHomeResponse.WorkerStatsOverview.builder()
                .completedJobsToday(p.getCompletedJobsToday())
                .completedJobsThisMonth(p.getCompletedJobsThisMonth())
                .incomeToday(defaultMoney(p.getIncomeToday()))
                .incomeThisWeek(defaultMoney(p.getIncomeThisWeek()))
                .incomeThisMonth(defaultMoney(p.getIncomeThisMonth()))
                .averageRating(p.getAverageRating())
                .totalReviews(p.getTotalReviews())
                .build();
    }

    private List<WorkerHomeResponse.IncomeChartPoint> toIncomeChart(
            List<WorkerIncomeChartPointProjection> projections) {
        if (projections == null || projections.isEmpty()) {
            return Collections.emptyList();
        }

        return projections.stream()
                .map(p -> WorkerHomeResponse.IncomeChartPoint.builder()
                        .label(p.getLabel())
                        .income(defaultMoney(p.getIncome()))
                        .completedJobs(p.getCompletedJobs())
                        .build())
                .toList();
    }

    private List<WorkerHomeResponse.TodayAppointmentItem> toAppointments(
            List<WorkerScheduleItemProjection> projections) {
        if (projections == null || projections.isEmpty()) {
            return Collections.emptyList();
        }

        return projections.stream()
                .map(p -> WorkerHomeResponse.TodayAppointmentItem.builder()
                        .bookingId(p.getBookingId())
                        .serviceName(p.getServiceName())
                        .customerName(p.getCustomerName())
                        .address(p.getAddress())
                        .status(p.getStatus())
                        .statusText(toBookingStatusText(p.getStatus()))
                        .scheduledTime(p.getScheduledTime())
                        .finalPrice(p.getFinalPrice())
                        .build())
                .toList();
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String toBookingStatusText(String status) {
        if (status == null) {
            return "Không xác định";
        }

        return switch (status) {
            case "Accepted" -> "Đã nhận đơn";
            case "Surveying" -> "Đang khảo sát";
            case "Waiting_Approval" -> "Chờ duyệt báo giá";
            case "In_Progress" -> "Đang sửa chữa";
            case "Completed" -> "Hoàn thành";
            case "Cancelled" -> "Đã hủy";
            default -> status;
        };
    }

    private String toNextAction(String status) {
        if (status == null) {
            return null;
        }

        return switch (status) {
            case "Accepted" -> "START_MOVING";
            case "Surveying" -> "CREATE_QUOTATION";
            case "Waiting_Approval" -> "WAIT_CUSTOMER_APPROVAL";
            case "In_Progress" -> "WORKER_COMPLETE";
            default -> null;
        };
    }
}