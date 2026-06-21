package com.fixit.feature.customer.booking.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;
import com.fixit.feature.customer.booking.domain.repository.CustomerBookingRepository;

import java.util.List;
import javax.inject.Inject;

public class GetBookingsUseCase {
    private final CustomerBookingRepository repository;

    @Inject
    public GetBookingsUseCase(CustomerBookingRepository repository) {
        this.repository = repository;
    }

    public void execute(ResultCallback<List<CustomerBooking>> callback) {
        repository.getBookings(callback);
    }
}
