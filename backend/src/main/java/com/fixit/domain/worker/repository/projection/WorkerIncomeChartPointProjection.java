package com.fixit.domain.worker.repository.projection;

import java.math.BigDecimal;

public interface WorkerIncomeChartPointProjection {

    String getLabel();

    BigDecimal getIncome();

    Integer getCompletedJobs();
}