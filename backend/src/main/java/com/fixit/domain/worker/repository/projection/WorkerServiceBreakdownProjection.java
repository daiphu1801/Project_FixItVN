package com.fixit.domain.worker.repository.projection;

import java.math.BigDecimal;

public interface WorkerServiceBreakdownProjection {
    String getCategoryName();
    Integer getBookingCount();
    BigDecimal getTotalRevenue();
}
