package com.fixit.domain.worker.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkerStatsResponse {

    private UUID workerId;
    private StatsOverview overview;
    private List<IncomeChartPoint> incomeChart;
    private List<ServiceBreakdown> serviceBreakdown;
    private List<RatingCount> ratingDistribution;
    private JobCompletionRate completionRate;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatsOverview {
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
    public static class ServiceBreakdown {
        private String categoryName;
        private Integer bookingCount;
        private BigDecimal totalRevenue;
        private Double revenuePercentage;
    }

    @Getter
    @Builder
    public static class RatingCount {
        private Integer rating;
        private Integer count;
    }

    @Getter
    @Builder
    public static class JobCompletionRate {
        private Integer totalJobs;
        private Integer completedJobs;
        private Integer cancelledJobs;
        private Double completionRatePercent;
    }
}