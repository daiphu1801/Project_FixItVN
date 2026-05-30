package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.response.WorkerStatsResponse;
import com.fixit.domain.worker.repository.projection.WorkerIncomeChartPointProjection;
import com.fixit.domain.worker.repository.projection.WorkerPerformanceStatsProjection;
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
    public WorkerStatsResponse getMyStats() {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        WorkerPerformanceStatsProjection stats =
                workerHomeQueryRepository.findStatsOverview(workerId);

        List<WorkerIncomeChartPointProjection> chart =
                workerHomeQueryRepository.findIncomeChartLast7Days(workerId);

        return WorkerStatsResponse.builder()
                .workerId(workerId)
                .overview(toStatsOverview(stats))
                .incomeChart(toIncomeChart(chart))
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
}