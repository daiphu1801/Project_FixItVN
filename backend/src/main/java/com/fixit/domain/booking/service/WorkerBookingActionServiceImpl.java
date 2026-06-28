package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.response.BookingActionResponse;
import com.fixit.domain.booking.dto.response.WorkerBookingDetailResponse;
import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingHistory;
import com.fixit.domain.booking.entity.BookingStatus;
import com.fixit.domain.booking.repository.BookingHistoryRepository;
import com.fixit.domain.booking.repository.BookingRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.fixit.domain.notification.service.NotificationSenderService;
import com.fixit.domain.booking.entity.ProofType;
import com.fixit.domain.booking.repository.ProofOfWorkRepository;
import com.fixit.domain.booking.dto.response.ProofOfWorkResponse;

@Slf4j
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
    private final NotificationSenderService notificationSenderService;
    private final ProofOfWorkRepository proofOfWorkRepository;

    @Override
    @Transactional(readOnly = true)
    public WorkerBookingDetailResponse getBookingDetails(UUID bookingId) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getWorker() == null || !booking.getWorker().getWorkerId().equals(workerId)) {
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }

        List<String> doneActions = getDoneActions(bookingId);

        String scheduledTimeStr = null;
        if (booking.getScheduledTime() != null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            scheduledTimeStr = booking.getScheduledTime().atZoneSameInstant(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).format(formatter);
        }

        List<ProofOfWorkResponse> proofOfWorks = proofOfWorkRepository.findByBooking_IdOrderByCapturedAtAsc(bookingId).stream()
                .map(pow -> ProofOfWorkResponse.builder()
                        .proofId(pow.getId())
                        .bookingId(pow.getBooking().getId())
                        .imageUrl(pow.getImageUrl())
                        .proofType(pow.getProofType().name())
                        .capturedAt(pow.getCapturedAt())
                        .build())
                .toList();

        return WorkerBookingDetailResponse.builder()
                .bookingId(booking.getId())
                .serviceName(booking.getServiceCategory() != null ? booking.getServiceCategory().getServiceName() : null)
                .customerName(booking.getCustomer() != null ? booking.getCustomer().getFullName() : null)
                .customerPhone(booking.getCustomer() != null && booking.getCustomer().getUser() != null ? booking.getCustomer().getUser().getPhoneNumber() : null)
                .customerAvatar(booking.getCustomer() != null && booking.getCustomer().getUser() != null ? booking.getCustomer().getUser().getAvatarUrl() : null)
                .address(booking.getAddress())
                .destinationLat(booking.getDestinationLat())
                .destinationLng(booking.getDestinationLng())
                .issueDescription(booking.getIssueDescription())
                .scheduledTime(scheduledTimeStr)
                .paymentMethod(booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null)
                .finalPrice(booking.getFinalPrice())
                .status(booking.getStatus().name())
                .statusText(toBookingStatusText(booking.getStatus().name()))
                .nextAction(toNextAction(booking.getStatus().name(), doneActions))
                .doneActions(doneActions)
                .proofOfWorks(proofOfWorks)
                .build();
    }

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

        sendNotificationToCustomer(
                booking,
                "Thợ đang di chuyển",
                "Thợ [Tên thợ] đang di chuyển đến địa chỉ của bạn.",
                "WORKER_MOVING"
        );

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

        sendNotificationToCustomer(
                booking,
                "Thợ đã đến nơi",
                "Thợ [Tên thợ] đã có mặt tại điểm hẹn.",
                "WORKER_ARRIVED"
        );

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

        sendNotificationToCustomer(
                booking,
                "Bắt đầu khảo sát",
                "Thợ [Tên thợ] đang thực hiện khảo sát tình trạng hư hỏng.",
                "START_SURVEY"
        );

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

        // Validate proof of work before starting repair
        if (!proofOfWorkRepository.findByBooking_IdAndProofType(bookingId, ProofType.BEFORE_REPAIR).isPresent()) {
            throw new AppException(ErrorCode.PROOF_OF_WORK_BEFORE_REPAIR_REQUIRED);
        }

        OffsetDateTime now = OffsetDateTime.now();

        booking.setStatus(BookingStatus.In_Progress);
        saveHistory(booking, HISTORY_IN_PROGRESS, now);

        sendNotificationToCustomer(
                booking,
                "Bắt đầu sửa chữa",
                "Thợ [Tên thợ] đã bắt đầu sửa chữa thiết bị.",
                "START_REPAIR"
        );

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

        // Validate proof of work before completing work
        if (!proofOfWorkRepository.findByBooking_IdAndProofType(bookingId, ProofType.AFTER_REPAIR).isPresent()) {
            throw new AppException(ErrorCode.PROOF_OF_WORK_AFTER_REPAIR_REQUIRED);
        }

        OffsetDateTime now = OffsetDateTime.now();

        booking.setStatus(BookingStatus.Waiting_Approval);
        saveHistory(booking, HISTORY_WORKER_COMPLETED, now);

        sendNotificationToCustomer(
                booking,
                "Công việc đã hoàn thành",
                "Thợ [Tên thợ] báo cáo đã hoàn thành công việc. Vui lòng kiểm tra và duyệt nghiệm thu.",
                "WORKER_COMPLETE"
        );

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

    private void sendNotificationToCustomer(Booking booking, String title, String content, String type) {
        try {
            if (booking != null && booking.getCustomer() != null && booking.getCustomer().getUser() != null) {
                UUID customerUserId = booking.getCustomer().getUser().getId();
                String workerName = booking.getWorker() != null && booking.getWorker().getFullName() != null 
                        ? booking.getWorker().getFullName() : "Thợ";
                String formattedContent = content.replace("[Tên thợ]", workerName);
                Map<String, String> data = Map.of(
                        "bookingId", booking.getId().toString(),
                        "status", booking.getStatus().name(),
                        "type", type
                );
                notificationSenderService.sendNotification(customerUserId, title, formattedContent, data);
            } else {
                log.warn("Cannot send notification: booking, customer or customer user is null.");
            }
        } catch (Exception e) {
            log.error("Failed to send notification '{}' for booking: {}", type, booking != null ? booking.getId() : null, e);
        }
    }

    private String toBookingStatusText(String status) {
        if (status == null) {
            return "Không xác định";
        }

        return switch (status) {
            case "Accepted" -> "Đã nhận đơn";
            case "Surveying" -> "Đang khảo sát";
            case "Waiting_Approval" -> "Chờ khách nghiệm thu";
            case "Waiting_Payment" -> "Chờ xác nhận nhận tiền";
            case "In_Progress" -> "Đang sửa chữa";
            case "Completed" -> "Hoàn thành";
            case "Cancelled" -> "Đã hủy";
            default -> status;
        };
    }

    private String toNextAction(String status, List<String> doneActions) {
        if (status == null) {
            return null;
        }

        return switch (status) {
            case "Accepted" -> {
                if (doneActions.contains("Arrived")) {
                    yield "START_SURVEY";
                } else if (doneActions.contains("Moving")) {
                    yield "ARRIVE";
                } else {
                    yield "START_MOVING";
                }
            }
            case "Surveying" -> "START_REPAIR";
            case "In_Progress" -> "WORKER_COMPLETE";
            default -> null;
        };
    }
    @Override
    @Transactional
    public BookingActionResponse workerConfirmPayment(UUID bookingId) {
        Booking booking = getCurrentWorkerBookingForUpdate(bookingId);

        requireStatus(booking, BookingStatus.Waiting_Payment);

        OffsetDateTime now = OffsetDateTime.now();

        booking.setStatus(BookingStatus.Completed);
        saveHistory(booking, "Payment_Confirmed", now);

        sendNotificationToCustomer(
                booking,
                "Đơn hàng hoàn tất",
                "Thợ [Tên thợ] đã xác nhận nhận đủ tiền. Đơn hàng của bạn đã hoàn thành.",
                "PAYMENT_CONFIRMED"
        );

        return buildResponse(
                booking,
                "Payment_Confirmed",
                null,
                "Xác nhận nhận tiền thành công",
                now
        );
    }

    private void sendNotificationToWorker(Booking booking, String title, String content, String type) {
        try {
            if (booking != null && booking.getWorker() != null && booking.getWorker().getUser() != null) {
                UUID workerUserId = booking.getWorker().getUser().getId();
                String customerName = booking.getCustomer() != null && booking.getCustomer().getFullName() != null
                        ? booking.getCustomer().getFullName() : "Khách hàng";
                String formattedContent = content.replace("[Tên khách]", customerName);
                Map<String, String> data = Map.of(
                        "bookingId", booking.getId().toString(),
                        "status", booking.getStatus().name(),
                        "type", type
                );
                notificationSenderService.sendNotification(workerUserId, title, formattedContent, data);
            } else {
                log.warn("Cannot send notification to worker: booking or worker is null.");
            }
        } catch (Exception e) {
            log.error("Failed to send notification '{}' to worker for booking: {}", type, booking != null ? booking.getId() : null, e);
        }
    }
}
