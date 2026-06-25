package com.fixit.feature.customer.booking.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.booking.domain.repository.CustomerBookingRepository;

import javax.inject.Inject;

public class CancelBookingUseCase {
    private final CustomerBookingRepository repository;

    @Inject
    public CancelBookingUseCase(CustomerBookingRepository repository) {
        this.repository = repository;
    }

    public void execute(String bookingId, String reason, boolean isWorkerFault, ResultCallback<Void> callback) {
        repository.cancelBooking(bookingId, reason, isWorkerFault, callback);
    }
}
