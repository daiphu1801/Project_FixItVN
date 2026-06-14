package com.fixit.feature.worker.orders.domain.repository;

import com.fixit.core.common.ResultCallback;
import java.math.BigDecimal;

public interface WorkerBookingRepository {
    void submitQuotation(String bookingId, BigDecimal laborCost, BigDecimal materialCost, ResultCallback<Void> callback);
}
