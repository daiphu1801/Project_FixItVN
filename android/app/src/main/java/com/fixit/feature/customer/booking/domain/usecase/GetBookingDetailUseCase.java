package com.fixit.feature.customer.booking.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;
import com.fixit.feature.customer.booking.domain.repository.CustomerBookingRepository;

import javax.inject.Inject;

public class GetBookingDetailUseCase {
    private final CustomerBookingRepository repository;

    @Inject
    public GetBookingDetailUseCase(CustomerBookingRepository repository) {
        this.repository = repository;
    }

    public void execute(String bookingId, ResultCallback<CustomerBooking> callback) {
        repository.getBookingDetail(bookingId, callback);
    }
}
