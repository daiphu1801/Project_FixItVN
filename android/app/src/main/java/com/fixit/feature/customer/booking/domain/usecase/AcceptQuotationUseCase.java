package com.fixit.feature.customer.booking.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.booking.domain.repository.CustomerBookingRepository;

import javax.inject.Inject;

public class AcceptQuotationUseCase {
    private final CustomerBookingRepository repository;

    @Inject
    public AcceptQuotationUseCase(CustomerBookingRepository repository) {
        this.repository = repository;
    }

    public void execute(String bookingId, String quotationId, ResultCallback<Void> callback) {
        repository.acceptQuotation(bookingId, quotationId, callback);
    }
}
