package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.worker.orders.domain.repository.WorkerBookingRepository;

import java.math.BigDecimal;
import javax.inject.Inject;

public class SubmitQuotationUseCase {
    private final WorkerBookingRepository repository;

    @Inject
    public SubmitQuotationUseCase(WorkerBookingRepository repository) {
        this.repository = repository;
    }

    public void execute(String bookingId, BigDecimal laborCost, BigDecimal materialCost, ResultCallback<Void> callback) {
        repository.submitQuotation(bookingId, laborCost, materialCost, callback);
    }
}
