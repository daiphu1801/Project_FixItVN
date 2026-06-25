package com.fixit.feature.customer.booking.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.booking.domain.repository.CustomerBookingRepository;

import javax.inject.Inject;

public class ProcessPaymentUseCase {
    private final CustomerBookingRepository repository;

    @Inject
    public ProcessPaymentUseCase(CustomerBookingRepository repository) {
        this.repository = repository;
    }

    public void execute(String bookingId, ResultCallback<Void> callback) {
        repository.processPayment(bookingId, callback);
    }
}
