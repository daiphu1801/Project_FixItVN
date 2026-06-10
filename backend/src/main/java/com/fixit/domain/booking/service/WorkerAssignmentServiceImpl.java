package com.fixit.domain.booking.service;

import com.fixit.domain.booking.dto.request.RejectAssignmentRequest;
import com.fixit.domain.booking.dto.response.AssignmentActionResponse;
import com.fixit.domain.booking.dto.response.PendingAssignmentItemResponse;
import com.fixit.domain.booking.dto.response.PendingAssignmentResponse;
import com.fixit.domain.booking.entity.AssignmentStatus;
import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingHistory;
import com.fixit.domain.booking.entity.BookingStatus;
import com.fixit.domain.booking.entity.BookingWorkerAssignment;
import com.fixit.domain.booking.repository.BookingHistoryRepository;
import com.fixit.domain.booking.repository.BookingWorkerAssignmentRepository;
import com.fixit.domain.booking.repository.projection.PendingAssignmentProjection;
import com.fixit.domain.booking.repository.query.WorkerAssignmentQueryRepository;
import com.fixit.domain.worker.entity.Worker;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.Duration;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.fixit.domain.notification.service.NotificationSenderService;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerAssignmentServiceImpl implements WorkerAssignmentService {
        private static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
        /**
         * Thời gian thợ được phép phản hồi assignment.
         * Theo use case hiện tại: 3 phút.
         */
        private static final long ASSIGNMENT_TIMEOUT_MINUTES = 3;

        /**
         * Nếu thợ miss 3 lần thì auto offline.
         */
        private static final int AUTO_OFFLINE_MISSED_THRESHOLD = 3;

        private final CurrentWorkerResolver currentWorkerResolver;
        private final WorkerAssignmentQueryRepository workerAssignmentQueryRepository;
        private final BookingWorkerAssignmentRepository assignmentRepository;
        private final BookingHistoryRepository bookingHistoryRepository;
        private final WorkerRepository workerRepository;
        private final NotificationSenderService notificationSenderService;

        @Override
        @Transactional(readOnly = true)
        public PendingAssignmentResponse getPendingAssignments() {
                UUID workerId = currentWorkerResolver.getCurrentWorkerId();

                List<PendingAssignmentItemResponse> items = workerAssignmentQueryRepository
                                .findPendingAssignmentsByWorkerId(workerId)
                                .stream()
                                .map(this::toPendingItem)
                                .toList();

                return PendingAssignmentResponse.builder()
                                .totalItems(items.size())
                                .empty(items.isEmpty())
                                .items(items)
                                .build();
        }

        @Override
        @Transactional
        public AssignmentActionResponse accept(UUID bookingId, UUID assignmentId) {
                UUID workerId = currentWorkerResolver.getCurrentWorkerId();
                OffsetDateTime now = OffsetDateTime.now();

                BookingWorkerAssignment assignment = getAssignmentForAction(
                                assignmentId,
                                bookingId,
                                workerId);

                validatePendingAssignment(assignment);
                validateNotExpired(assignment, now);

                Booking booking = assignment.getBooking();
                Worker worker = assignment.getWorker();

                /*
                 * Chặn case:
                 * - booking đã không còn Pending
                 * - booking đã được worker khác nhận
                 */
                if (booking.getStatus() != BookingStatus.Pending || booking.getWorker() != null) {
                        throw new AppException(ErrorCode.BOOKING_ALREADY_TAKEN);
                }

                /*
                 * Chốt assignment này.
                 */
                assignment.setStatus(AssignmentStatus.Accepted);
                assignment.setRespondedAt(now);

                /*
                 * Chốt booking cho worker hiện tại.
                 */
                booking.setWorker(worker);
                booking.setStatus(BookingStatus.Accepted);

                /*
                 * Các assignment khác của cùng booking không còn hiệu lực.
                 */
                assignmentRepository.markOtherPendingAssignmentsAsMissed(
                                bookingId,
                                assignmentId,
                                now);

                /*
                 * Accept thành công thì reset missed_count.
                 */
                workerRepository.resetMissedCount(workerId);

                /*
                 * Ghi lịch sử trạng thái booking.
                 */
                bookingHistoryRepository.save(newHistory(booking, BookingStatus.Accepted.name(), now));

                try {
                        if (booking.getCustomer() != null && booking.getCustomer().getUser() != null) {
                                UUID customerUserId = booking.getCustomer().getUser().getId();
                                String workerName = worker != null && worker.getFullName() != null ? worker.getFullName() : "Thợ";
                                String title = "Thợ đã nhận đơn đặt lịch";
                                String content = "Thợ " + workerName + " đã nhận đơn đặt lịch của bạn.";
                                Map<String, String> data = Map.of(
                                                "bookingId", bookingId.toString(),
                                                "status", BookingStatus.Accepted.name(),
                                                "type", "BOOKING_ACCEPTED"
                                );
                                notificationSenderService.sendNotification(customerUserId, title, content, data);
                        } else {
                                log.warn("Customer user is null for bookingId: {}. Skipping notification.", bookingId);
                        }
                } catch (Exception e) {
                        log.error("Failed to send notification for accepted booking: {}", bookingId, e);
                }

                return AssignmentActionResponse.builder()
                                .bookingId(bookingId)
                                .assignmentId(assignmentId)
                                .assignmentStatus(AssignmentStatus.Accepted.name())
                                .bookingStatus(BookingStatus.Accepted.name())
                                .nextAction("START_MOVING")
                                .message("Nhận đơn thành công")
                                .build();
        }

        @Override
        @Transactional
        public AssignmentActionResponse reject(
                        UUID bookingId,
                        UUID assignmentId,
                        RejectAssignmentRequest request) {
                UUID workerId = currentWorkerResolver.getCurrentWorkerId();
                OffsetDateTime now = OffsetDateTime.now();

                BookingWorkerAssignment assignment = getAssignmentForAction(
                                assignmentId,
                                bookingId,
                                workerId);

                validatePendingAssignment(assignment);

                /*
                 * Giai đoạn MVP:
                 * - Chỉ update status/repondedAt.
                 * - Chưa lưu reason vì DB chưa có cột reject_reason.
                 */
                assignment.setStatus(AssignmentStatus.Rejected);
                assignment.setRespondedAt(now);

                /*
                 * Tăng rejection_count.
                 * Nếu >= 5 thì giảm ưu tiên 24h.
                 */
                workerRepository.recordRejectedAssignment(workerId);

                return AssignmentActionResponse.builder()
                                .bookingId(bookingId)
                                .assignmentId(assignmentId)
                                .assignmentStatus(AssignmentStatus.Rejected.name())
                                .bookingStatus(assignment.getBooking().getStatus().name())
                                .nextAction("WAIT_NEXT_ASSIGNMENT")
                                .message("Từ chối đơn thành công")
                                .build();
        }

        @Override
        @Transactional
        public AssignmentActionResponse miss(UUID bookingId, UUID assignmentId) {
                UUID workerId = currentWorkerResolver.getCurrentWorkerId();
                OffsetDateTime now = OffsetDateTime.now();

                BookingWorkerAssignment assignment = getAssignmentForAction(
                                assignmentId,
                                bookingId,
                                workerId);

                validatePendingAssignment(assignment);

                assignment.setStatus(AssignmentStatus.Missed);
                assignment.setRespondedAt(now);

                /*
                 * Tăng missed_count.
                 * Nếu missed_count >= 3 thì tự động set is_available = false.
                 */
                workerRepository.recordMissedAssignment(
                                workerId,
                                AUTO_OFFLINE_MISSED_THRESHOLD);

                return AssignmentActionResponse.builder()
                                .bookingId(bookingId)
                                .assignmentId(assignmentId)
                                .assignmentStatus(AssignmentStatus.Missed.name())
                                .bookingStatus(assignment.getBooking().getStatus().name())
                                .nextAction("WAIT_NEXT_ASSIGNMENT")
                                .message("Đã ghi nhận bỏ lỡ đơn")
                                .build();
        }

        private BookingWorkerAssignment getAssignmentForAction(
                        UUID assignmentId,
                        UUID bookingId,
                        UUID workerId) {
                return assignmentRepository.findForAction(assignmentId, bookingId, workerId)
                                .orElseThrow(() -> new AppException(ErrorCode.WORKER_ASSIGNMENT_NOT_FOUND));
        }

        private void validatePendingAssignment(BookingWorkerAssignment assignment) {
                if (assignment.getStatus() != AssignmentStatus.Pending) {
                        throw new AppException(ErrorCode.WORKER_ASSIGNMENT_ALREADY_HANDLED);
                }
        }

        private void validateNotExpired(
                        BookingWorkerAssignment assignment,
                        OffsetDateTime now) {
                OffsetDateTime expiresAt = assignment.getAssignedAt()
                                .plusMinutes(ASSIGNMENT_TIMEOUT_MINUTES);

                if (!expiresAt.isAfter(now)) {
                        throw new AppException(ErrorCode.WORKER_ASSIGNMENT_EXPIRED);
                }
        }

        private BookingHistory newHistory(
                        Booking booking,
                        String status,
                        OffsetDateTime now) {
                /*
                 * Cách này yêu cầu entity BookingHistory có @Builder.
                 * Nếu entity của bạn không có @Builder, dùng bản setter ở dưới.
                 */
                return BookingHistory.builder()
                                .booking(booking)
                                .statusUpdate(status)
                                .updatedAt(now)
                                .build();
        }

        private PendingAssignmentItemResponse toPendingItem(PendingAssignmentProjection p) {
                // Trong WorkerAssignmentServiceImpl.java, đổi thành:
                OffsetDateTime assignedAt = p.getAssignedAt();
                OffsetDateTime scheduledTime = p.getScheduledTime();

                OffsetDateTime expiresAt = assignedAt != null
                                ? assignedAt.plusMinutes(ASSIGNMENT_TIMEOUT_MINUTES)
                                : null;

                int remainingSeconds = 0;

                if (expiresAt != null) {
                        long seconds = Duration.between(OffsetDateTime.now(APP_ZONE), expiresAt).getSeconds();
                        remainingSeconds = (int) Math.max(seconds, 0);
                }

                return PendingAssignmentItemResponse.builder()
                                .assignmentId(p.getAssignmentId())
                                .bookingId(p.getBookingId())
                                .serviceName(p.getServiceName())
                                .customerName(p.getCustomerName())
                                .addressPreview(p.getAddressPreview())
                                .issueDescription(p.getIssueDescription())
                                .scheduledTime(scheduledTime)
                                .assignedAt(assignedAt)
                                .expiresAt(expiresAt)
                                .remainingSeconds(remainingSeconds)
                                .destinationLat(p.getDestinationLat())
                                .destinationLng(p.getDestinationLng())
                                .finalPrice(p.getFinalPrice())
                                .paymentMethod(p.getPaymentMethod())
                                .build();
        }

        private OffsetDateTime parseBangkokDateTime(String value) {
                if (value == null || value.isBlank()) {
                        return null;
                }

                LocalDateTime localDateTime = LocalDateTime.parse(value);
                return localDateTime.atZone(APP_ZONE).toOffsetDateTime();
        }

        @Override
        @Transactional
        public int markExpiredAssignmentsAsMissed() {
                OffsetDateTime now = OffsetDateTime.now(APP_ZONE);
                OffsetDateTime expiredBefore = now.minusMinutes(ASSIGNMENT_TIMEOUT_MINUTES);

                List<BookingWorkerAssignment> expiredAssignments = assignmentRepository
                                .findExpiredPendingAssignmentsForUpdate(expiredBefore);

                int handledCount = 0;

                for (BookingWorkerAssignment assignment : expiredAssignments) {
                        if (assignment.getStatus() != AssignmentStatus.Pending) {
                                continue;
                        }

                        assignment.setStatus(AssignmentStatus.Missed);
                        assignment.setRespondedAt(now);

                        UUID workerId = assignment.getWorker().getWorkerId();

                        workerRepository.recordMissedAssignment(
                                        workerId,
                                        AUTO_OFFLINE_MISSED_THRESHOLD);

                        handledCount++;
                }

                return handledCount;
        }

}