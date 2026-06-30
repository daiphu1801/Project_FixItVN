package com.fixit.feature.customer.booking.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerBookingRepository {
    void createBooking(Integer serviceId, String address, BigDecimal lat, BigDecimal lng, String issueDescription, ResultCallback<CustomerBooking> callback);
    void getBookingDetail(String bookingId, ResultCallback<CustomerBooking> callback);
    void getBookings(ResultCallback<List<CustomerBooking>> callback);
    void cancelBooking(String bookingId, String reason, boolean isWorkerFault, ResultCallback<Void> callback);
    void acceptQuotation(String bookingId, String quotationId, ResultCallback<Void> callback);
    void processPayment(String bookingId, ResultCallback<Void> callback);
}
