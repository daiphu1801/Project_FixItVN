package com.fixit.feature.worker.stats.data.remote.mapper;

import com.fixit.feature.worker.stats.data.remote.dto.WorkerStatsResponse;
import com.fixit.feature.worker.stats.domain.model.WorkerStats;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WorkerStatsMapper {

    private WorkerStatsMapper() {
    }

    public static WorkerStats toDomain(WorkerStatsResponse response) {
        if (response == null) {
            return null;
        }

        WorkerStats.StatsOverview statsOverview = new WorkerStats.StatsOverview(
                0,
                0,
                0L,
                0L,
                0L,
                0.0,
                0
        );

        if (response.getOverview() != null) {
            WorkerStatsResponse.StatsOverview s = response.getOverview();
            statsOverview = new WorkerStats.StatsOverview(
                    safeInt(s.getCompletedJobsToday()),
                    safeInt(s.getCompletedJobsThisMonth()),
                    moneyToLong(s.getIncomeToday()),
                    moneyToLong(s.getIncomeThisWeek()),
                    moneyToLong(s.getIncomeThisMonth()),
                    decimalToDouble(s.getAverageRating()),
                    safeInt(s.getTotalReviews())
            );
        }

        List<WorkerStats.IncomeChartPoint> incomeChart = new ArrayList<>();
        if (response.getIncomeChart() != null) {
            for (WorkerStatsResponse.IncomeChartPoint item : response.getIncomeChart()) {
                incomeChart.add(new WorkerStats.IncomeChartPoint(
                        safeText(item.getLabel(), ""),
                        moneyToLong(item.getIncome()),
                        safeInt(item.getCompletedJobs())
                ));
            }
        }

        List<WorkerStats.ServiceBreakdown> serviceBreakdown = new ArrayList<>();
        if (response.getServiceBreakdown() != null) {
            for (WorkerStatsResponse.ServiceBreakdown item : response.getServiceBreakdown()) {
                serviceBreakdown.add(new WorkerStats.ServiceBreakdown(
                        safeText(item.getCategoryName(), "Khác"),
                        safeInt(item.getBookingCount()),
                        moneyToLong(item.getTotalRevenue()),
                        item.getRevenuePercentage() == null ? 0.0 : item.getRevenuePercentage()
                ));
            }
        }

        List<WorkerStats.RatingCount> ratingDistribution = new ArrayList<>();
        if (response.getRatingDistribution() != null) {
            for (WorkerStatsResponse.RatingCount item : response.getRatingDistribution()) {
                ratingDistribution.add(new WorkerStats.RatingCount(
                        safeInt(item.getRating()),
                        safeInt(item.getCount())
                ));
            }
        }

        WorkerStats.JobCompletionRate completionRate = new WorkerStats.JobCompletionRate(0, 0, 0, 100.0);
        if (response.getCompletionRate() != null) {
            WorkerStatsResponse.JobCompletionRate r = response.getCompletionRate();
            completionRate = new WorkerStats.JobCompletionRate(
                    safeInt(r.getTotalJobs()),
                    safeInt(r.getCompletedJobs()),
                    safeInt(r.getCancelledJobs()),
                    r.getCompletionRatePercent() == null ? 100.0 : r.getCompletionRatePercent()
            );
        }

        return new WorkerStats(
                response.getWorkerId(),
                statsOverview,
                incomeChart,
                serviceBreakdown,
                ratingDistribution,
                completionRate
        );
    }

    private static String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }

    private static int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private static long moneyToLong(BigDecimal value) {
        return value == null ? 0L : value.longValue();
    }

    private static double decimalToDouble(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }
}
