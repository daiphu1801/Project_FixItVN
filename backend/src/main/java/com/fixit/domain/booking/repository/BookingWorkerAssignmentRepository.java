package com.fixit.domain.booking.repository;

import com.fixit.domain.booking.entity.BookingWorkerAssignment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface BookingWorkerAssignmentRepository extends JpaRepository<BookingWorkerAssignment, UUID> {

    /**
     * Lấy assignment để xử lý accept/reject/miss.
     *
     * PESSIMISTIC_WRITE giúp khóa dòng dữ liệu trong transaction.
     * Mục tiêu: tránh việc cùng một assignment bị xử lý đồng thời.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT assignment
            FROM BookingWorkerAssignment assignment
            JOIN FETCH assignment.booking booking
            JOIN FETCH assignment.worker worker
            WHERE assignment.id = :assignmentId
              AND booking.id = :bookingId
              AND worker.workerId = :workerId
            """)
    Optional<BookingWorkerAssignment> findForAction(
            @Param("assignmentId") UUID assignmentId,
            @Param("bookingId") UUID bookingId,
            @Param("workerId") UUID workerId
    );

    /**
     * Sau khi một thợ accept, các assignment pending còn lại của cùng booking
     * không còn hợp lệ nữa.
     *
     * Giai đoạn MVP: mark là Missed.
     * Nếu muốn chính xác hơn, sau này có thể thêm status Cancelled/Expired.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE booking_worker_assignments
            SET status = 'Missed',
                responded_at = :respondedAt
            WHERE booking_id = :bookingId
              AND id <> :acceptedAssignmentId
              AND status = 'Pending'
            """, nativeQuery = true)
    int markOtherPendingAssignmentsAsMissed(
            @Param("bookingId") UUID bookingId,
            @Param("acceptedAssignmentId") UUID acceptedAssignmentId,
            @Param("respondedAt") OffsetDateTime respondedAt
    );
}