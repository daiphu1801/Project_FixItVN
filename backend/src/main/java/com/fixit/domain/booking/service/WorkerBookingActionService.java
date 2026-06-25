package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.response.BookingActionResponse;
import com.fixit.domain.booking.dto.response.WorkerBookingDetailResponse;

import java.util.UUID;

public interface WorkerBookingActionService {

    WorkerBookingDetailResponse getBookingDetails(UUID bookingId);

    BookingActionResponse startMoving(UUID bookingId);

    BookingActionResponse arrive(UUID bookingId);

    BookingActionResponse startSurvey(UUID bookingId);

    BookingActionResponse startRepair(UUID bookingId);

    BookingActionResponse workerComplete(UUID bookingId);
}