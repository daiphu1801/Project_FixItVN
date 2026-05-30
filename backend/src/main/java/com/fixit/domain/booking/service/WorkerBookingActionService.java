package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.response.BookingActionResponse;

import java.util.UUID;

public interface WorkerBookingActionService {

    BookingActionResponse startMoving(UUID bookingId);

    BookingActionResponse arrive(UUID bookingId);

    BookingActionResponse startSurvey(UUID bookingId);

    BookingActionResponse startRepair(UUID bookingId);

    BookingActionResponse workerComplete(UUID bookingId);
}