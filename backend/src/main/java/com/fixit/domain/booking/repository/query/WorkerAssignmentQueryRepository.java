package com.fixit.domain.booking.repository.query;

import com.fixit.domain.booking.entity.BookingWorkerAssignment;
import com.fixit.domain.booking.repository.projection.PendingAssignmentProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface WorkerAssignmentQueryRepository extends Repository<BookingWorkerAssignment, UUID> {

    @Query(value = """
            SELECT
                bwa.id AS "assignmentId",
                b.id AS "bookingId",
                sc.service_name AS "serviceName",
                c.full_name AS "customerName",
                b.address AS "addressPreview",
                b.issue_description AS "issueDescription",

                -- Hãy sửa lại thành trực tiếp tên cột:
                b.scheduled_time AS "scheduledTime",
                bwa.assigned_at AS "assignedAt",

                b.destination_lat AS "destinationLat",
                b.destination_lng AS "destinationLng",
                b.final_price AS "finalPrice",
                b.payment_method AS "paymentMethod"
            FROM booking_worker_assignments bwa
            JOIN bookings b
                ON b.id = bwa.booking_id
            LEFT JOIN service_categories sc
                ON sc.id = b.service_id
            LEFT JOIN customers c
                ON c.customer_id = b.customer_id
            WHERE bwa.worker_id = :workerId
              AND bwa.status = 'Pending'
              AND b.status = 'Pending'
              AND bwa.assigned_at > :expiredAfter
            ORDER BY bwa.assigned_at ASC
            """, nativeQuery = true)
    List<PendingAssignmentProjection> findPendingAssignmentsByWorkerId(
            @Param("workerId") UUID workerId,
            @Param("expiredAfter") OffsetDateTime expiredAfter
    );
}