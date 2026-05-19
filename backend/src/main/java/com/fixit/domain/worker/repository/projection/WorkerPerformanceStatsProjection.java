package com.fixit.domain.worker.repository.projection;

import java.math.BigDecimal;

public interface WorkerPerformanceStatsProjection {

    Integer getCompletedJobsToday();

    Integer getCompletedJobsThisMonth();

    BigDecimal getIncomeToday();

    BigDecimal getIncomeThisWeek();

    BigDecimal getIncomeThisMonth();

    BigDecimal getAverageRating();

    Integer getTotalReviews();
}