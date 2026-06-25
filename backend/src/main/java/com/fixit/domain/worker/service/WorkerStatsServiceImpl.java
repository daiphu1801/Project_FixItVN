package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.response.WorkerStatsResponse;
import com.fixit.domain.worker.repository.projection.WorkerIncomeChartPointProjection;
import com.fixit.domain.worker.repository.projection.WorkerPerformanceStatsProjection;
import com.fixit.domain.worker.repository.projection.WorkerRatingDistributionProjection;
import com.fixit.domain.worker.repository.projection.WorkerServiceBreakdownProjection;
import com.fixit.domain.worker.repository.projection.WorkerJobCompletionRateProjection;
import com.fixit.domain.worker.repository.query.WorkerHomeQueryRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerStatsServiceImpl implements WorkerStatsService {

    private final CurrentWorkerResolver currentWorkerResolver;
    private final WorkerHomeQueryRepository workerHomeQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public WorkerStatsResponse getMyStats(String period) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        WorkerPerformanceStatsProjection stats =
                workerHomeQueryRepository.findStatsOverview(workerId);

        List<WorkerIncomeChartPointProjection> chart;
        String normalizedPeriod = period != null ? period.toLowerCase() : "week";

        switch (normalizedPeriod) {
            case "today":
                chart = workerHomeQueryRepository.findIncomeChartToday(workerId);
                break;
            case "month":
                chart = workerHomeQueryRepository.findIncomeChartMonth(workerId);
                break;
            case "week":
            default:
                chart = workerHomeQueryRepository.findIncomeChartLast7Days(workerId);
                break;
        }

        return WorkerStatsResponse.builder()
                .workerId(workerId)
                .overview(toStatsOverview(stats))
                .incomeChart(toIncomeChart(chart))
                .ratingDistribution(toRatingDistribution(workerHomeQueryRepository.findRatingDistribution(workerId)))
                .serviceBreakdown(toServiceBreakdown(workerHomeQueryRepository.findServiceBreakdown(workerId)))
                .completionRate(toCompletionRate(workerHomeQueryRepository.findJobCompletionRate(workerId)))
                .build();
    }

    private WorkerStatsResponse.StatsOverview toStatsOverview(WorkerPerformanceStatsProjection p) {
        if (p == null) {
            return WorkerStatsResponse.StatsOverview.builder()
                    .completedJobsToday(0)
                    .completedJobsThisMonth(0)
                    .incomeToday(BigDecimal.ZERO)
                    .incomeThisWeek(BigDecimal.ZERO)
                    .incomeThisMonth(BigDecimal.ZERO)
                    .averageRating(BigDecimal.ZERO)
                    .totalReviews(0)
                    .build();
        }

        return WorkerStatsResponse.StatsOverview.builder()
                .completedJobsToday(defaultInt(p.getCompletedJobsToday()))
                .completedJobsThisMonth(defaultInt(p.getCompletedJobsThisMonth()))
                .incomeToday(defaultMoney(p.getIncomeToday()))
                .incomeThisWeek(defaultMoney(p.getIncomeThisWeek()))
                .incomeThisMonth(defaultMoney(p.getIncomeThisMonth()))
                .averageRating(defaultMoney(p.getAverageRating()))
                .totalReviews(defaultInt(p.getTotalReviews()))
                .build();
    }

    private List<WorkerStatsResponse.IncomeChartPoint> toIncomeChart(
            List<WorkerIncomeChartPointProjection> projections
    ) {
        if (projections == null || projections.isEmpty()) {
            return Collections.emptyList();
        }

        return projections.stream()
                .map(p -> WorkerStatsResponse.IncomeChartPoint.builder()
                        .label(p.getLabel())
                        .income(defaultMoney(p.getIncome()))
                        .completedJobs(defaultInt(p.getCompletedJobs()))
                        .build())
                .toList();
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private List<WorkerStatsResponse.RatingCount> toRatingDistribution(
            List<WorkerRatingDistributionProjection> projections
    ) {
        if (projections == null) {
            return Collections.emptyList();
        }
        return projections.stream()
                .map(p -> WorkerStatsResponse.RatingCount.builder()
                        .rating(p.getRating())
                        .count(p.getCount())
                        .build())
                .toList();
    }

    private List<WorkerStatsResponse.ServiceBreakdown> toServiceBreakdown(
            List<WorkerServiceBreakdownProjection> projections
    ) {
        if (projections == null || projections.isEmpty()) {
            return Collections.emptyList();
        }
        BigDecimal totalRevenueAllServices = projections.stream()
                .map(p -> defaultMoney(p.getTotalRevenue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return projections.stream()
                .map(p -> {
                    BigDecimal rev = defaultMoney(p.getTotalRevenue());
                    double pct = 0.0;
                    if (totalRevenueAllServices.compareTo(BigDecimal.ZERO) > 0) {
                        pct = rev.multiply(BigDecimal.valueOf(100))
                                 .divide(totalRevenueAllServices, 2, java.math.RoundingMode.HALF_UP)
                                 .doubleValue();
                    }
                    return WorkerStatsResponse.ServiceBreakdown.builder()
                            .categoryName(p.getCategoryName())
                            .bookingCount(p.getBookingCount())
                            .totalRevenue(rev)
                            .revenuePercentage(pct)
                            .build();
                })
                .toList();
    }

    private WorkerStatsResponse.JobCompletionRate toCompletionRate(
            WorkerJobCompletionRateProjection p
    ) {
        if (p == null) {
            return WorkerStatsResponse.JobCompletionRate.builder()
                    .totalJobs(0)
                    .completedJobs(0)
                    .cancelledJobs(0)
                    .completionRatePercent(100.0)
                    .build();
        }
        int total = defaultInt(p.getTotalJobs());
        int completed = defaultInt(p.getCompletedJobs());
        int cancelled = defaultInt(p.getCancelledJobs());
        double pct = 100.0;
        if (total > 0) {
            pct = ((double) completed / total) * 100.0;
            pct = Math.round(pct * 10.0) / 10.0;
        }
        return WorkerStatsResponse.JobCompletionRate.builder()
                .totalJobs(total)
                .completedJobs(completed)
                .cancelledJobs(cancelled)
                .completionRatePercent(pct)
                .build();
    }
}