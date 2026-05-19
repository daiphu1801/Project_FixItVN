package com.fixit.domain.worker.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkerHomeResponse {

    private UUID workerId;
    private String fullName;
    private String avatarUrl;
    private String greetingText;

    private Boolean hasUnreadNotification;
    private Integer unreadNotificationCount;

    private Boolean available;
    private String statusText;
    private String statusHelpText;

    private String verificationStatus;
    private BigDecimal reputationScore;
    private BigDecimal latitude;
    private BigDecimal longitude;

    private Boolean canReceiveJob;
    private String receiveJobBlockedReason;

    private Integer todayAppointmentCount;
    private Integer pendingAssignmentCount;

    private BigDecimal availableBalance;
    private BigDecimal heldBalance;
    private BigDecimal debtBalance;

    private ActiveOrderSummary activeOrder;
    private WorkerStatsOverview statsOverview;
    private List<IncomeChartPoint> incomeChart;
    private List<TodayAppointmentItem> todayAppointments;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ActiveOrderSummary {
        private UUID bookingId;
        private String serviceName;
        private String customerName;
        private String address;
        private String status;
        private String statusText;
        private OffsetDateTime scheduledTime;
        private BigDecimal finalPrice;
        private String nextAction;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class WorkerStatsOverview {
        private Integer completedJobsToday;
        private Integer completedJobsThisMonth;
        private BigDecimal incomeToday;
        private BigDecimal incomeThisWeek;
        private BigDecimal incomeThisMonth;
        private BigDecimal averageRating;
        private Integer totalReviews;
    }

    @Getter
    @Builder
    public static class IncomeChartPoint {
        private String label;
        private BigDecimal income;
        private Integer completedJobs;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TodayAppointmentItem {
        private UUID bookingId;
        private String serviceName;
        private String customerName;
        private String address;
        private String status;
        private String statusText;
        private OffsetDateTime scheduledTime;
        private BigDecimal finalPrice;
    }
}