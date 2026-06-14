package com.fixit.feature.customer.booking.domain.usecase;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;
import com.fixit.feature.customer.booking.domain.repository.CustomerBookingRepository;

import java.math.BigDecimal;

import javax.inject.Inject;

public class CreateBookingUseCase {
    private final CustomerBookingRepository repository;

    @Inject
    public CreateBookingUseCase(CustomerBookingRepository repository) {
        this.repository = repository;
    }

    public void execute(Integer serviceId, String address, BigDecimal lat, BigDecimal lng, String issueDescription, ResultCallback<CustomerBooking> callback) {
        repository.createBooking(serviceId, address, lat, lng, issueDescription, callback);
    }
}
