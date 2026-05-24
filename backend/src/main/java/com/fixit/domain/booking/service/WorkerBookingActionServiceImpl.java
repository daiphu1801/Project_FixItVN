package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.response.BookingActionResponse;
import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingHistory;
import com.fixit.domain.booking.entity.BookingStatus;
import com.fixit.domain.booking.repository.BookingHistoryRepository;
import com.fixit.domain.booking.repository.BookingRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerBookingActionServiceImpl implements WorkerBookingActionService {

    private static final String HISTORY_MOVING = "Moving";
    private static final String HISTORY_ARRIVED = "Arrived";
    private static final String HISTORY_SURVEYING = "Surveying";
    private static final String HISTORY_IN_PROGRESS = "In_Progress";
    private static final String HISTORY_WORKER_COMPLETED = "Worker_Completed";

    private final CurrentWorkerResolver currentWorkerResolver;
    private final BookingRepository bookingRepository;
    private final BookingHistoryRepository bookingHistoryRepository;

    @Override
    @Transactional
    public BookingActionResponse startMoving(UUID bookingId) {
        Booking booking = getCurrentWorkerBookingForUpdate(bookingId);

        // TỐI ƯU: Chỉ SELECT lấy toàn bộ lịch sử 1 lần duy nhất
        List<String> doneActions = getDoneActions(bookingId);

        requireStatus(booking, BookingStatus.Accepted);
        requireActionNotDone(doneActions, HISTORY_MOVING);

        OffsetDateTime now = OffsetDateTime.now();
        saveHistory(booking, HISTORY_MOVING, now);

        return buildResponse(
                booking,
                HISTORY_MOVING,
                "ARRIVE",
                "Bắt đầu di chuyển thành công",
                now
        );
    }

    @Override
    @Transactional
    public BookingActionResponse arrive(UUID bookingId) {
        Booking booking = getCurrentWorkerBookingForUpdate(bookingId);

        // TỐI ƯU: Chỉ SELECT lấy toàn bộ lịch sử 1 lần duy nhất
        List<String> doneActions = getDoneActions(bookingId);

        requireStatus(booking, BookingStatus.Accepted);
        requirePreviousActionDone(doneActions, HISTORY_MOVING);
        requireActionNotDone(doneActions, HISTORY_ARRIVED);

        OffsetDateTime now = OffsetDateTime.now();
        saveHistory(booking, HISTORY_ARRIVED, now);

        return buildResponse(
                booking,
                HISTORY_ARRIVED,
                "START_SURVEY",
                "Đã ghi nhận thợ đến nơi",
                now
        );
    }

    @Override
    @Transactional
    public BookingActionResponse startSurvey(UUID bookingId) {
        Booking booking = getCurrentWorkerBookingForUpdate(bookingId);

        // TỐI ƯU: Chỉ SELECT lấy toàn bộ lịch sử 1 lần duy nhất
        List<String> doneActions = getDoneActions(bookingId);

        requireStatus(booking, BookingStatus.Accepted);
        requirePreviousActionDone(doneActions, HISTORY_ARRIVED);
        requireActionNotDone(doneActions, HISTORY_SURVEYING);

        OffsetDateTime now = OffsetDateTime.now();

        booking.setStatus(BookingStatus.Surveying);
        saveHistory(booking, HISTORY_SURVEYING, now);

        return buildResponse(
                booking,
                HISTORY_SURVEYING,
                "START_REPAIR",
                "Bắt đầu khảo sát thành công",
                now
        );
    }

    @Override
    @Transactional
    public BookingActionResponse startRepair(UUID bookingId) {
        Booking booking = getCurrentWorkerBookingForUpdate(bookingId);

        // TỐI ƯU: Chỉ SELECT lấy toàn bộ lịch sử 1 lần duy nhất
        List<String> doneActions = getDoneActions(bookingId);

        requireStatus(booking, BookingStatus.Surveying, BookingStatus.Waiting_Approval);
        requirePreviousActionDone(doneActions, HISTORY_SURVEYING);
        requireActionNotDone(doneActions, HISTORY_IN_PROGRESS);

        OffsetDateTime now = OffsetDateTime.now();

        booking.setStatus(BookingStatus.In_Progress);
        saveHistory(booking, HISTORY_IN_PROGRESS, now);

        return buildResponse(
                booking,
                HISTORY_IN_PROGRESS,
                "WORKER_COMPLETE",
                "Bắt đầu sửa chữa thành công",
                now
        );
    }

    @Override
    @Transactional
    public BookingActionResponse workerComplete(UUID bookingId) {
        Booking booking = getCurrentWorkerBookingForUpdate(bookingId);

        // TỐI ƯU: Chỉ SELECT lấy toàn bộ lịch sử 1 lần duy nhất
        List<String> doneActions = getDoneActions(bookingId);

        requireStatus(booking, BookingStatus.In_Progress);
        requirePreviousActionDone(doneActions, HISTORY_IN_PROGRESS);
        requireActionNotDone(doneActions, HISTORY_WORKER_COMPLETED);

        OffsetDateTime now = OffsetDateTime.now();

        booking.setStatus(BookingStatus.Waiting_Approval);
        saveHistory(booking, HISTORY_WORKER_COMPLETED, now);

        return buildResponse(
                booking,
                HISTORY_WORKER_COMPLETED,
                "CUSTOMER_COMPLETE",
                "Thợ đã báo hoàn thành, chờ khách nghiệm thu",
                now
        );
    }

    private Booking getCurrentWorkerBookingForUpdate(UUID bookingId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        return bookingRepository.findWorkerBookingForUpdate(bookingId, workerId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
    }

    private void requireStatus(Booking booking, BookingStatus... allowedStatuses) {
        boolean matched = Arrays.stream(allowedStatuses)
                .anyMatch(status -> status == booking.getStatus());

        if (!matched) {
            throw new AppException(ErrorCode.BOOKING_INVALID_STATUS_TRANSITION);
        }
    }

    // TỐI ƯU: Lấy danh sách lịch sử trạng thái
    private List<String> getDoneActions(UUID bookingId) {
        return bookingHistoryRepository.findStatusUpdatesByBookingId(bookingId);
    }

    // TỐI ƯU: Kiểm tra thông qua List trong Java bộ nhớ trong (không gọi Database nữa)
    private void requirePreviousActionDone(List<String> doneActions, String previousAction) {
        if (!doneActions.contains(previousAction)) {
            throw new AppException(ErrorCode.BOOKING_PREVIOUS_ACTION_REQUIRED);
        }
    }

    // TỐI ƯU: Kiểm tra thông qua List trong Java bộ nhớ trong (không gọi Database nữa)
    private void requireActionNotDone(List<String> doneActions, String action) {
        if (doneActions.contains(action)) {
            throw new AppException(ErrorCode.BOOKING_ACTION_ALREADY_DONE);
        }
    }

    private void saveHistory(Booking booking, String statusUpdate, OffsetDateTime now) {
        bookingHistoryRepository.save(
                BookingHistory.builder()
                        .booking(booking)
                        .statusUpdate(statusUpdate)
                        .updatedAt(now)
                        .build()
        );
    }

    private BookingActionResponse buildResponse(
            Booking booking,
            String action,
            String nextAction,
            String message,
            OffsetDateTime now
    ) {
        return BookingActionResponse.builder()
                .bookingId(booking.getId())
                .bookingStatus(booking.getStatus().name())
                .action(action)
                .nextAction(nextAction)
                .message(message)
                .updatedAt(now)
                .build();
    }
}
