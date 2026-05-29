package com.fixit.domain.worker.repository.query;

import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.worker.repository.projection.WorkerScheduleItemProjection;
import org.springframework.data.repository.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface WorkerScheduleQueryRepository extends Repository<Booking, UUID> {

    @Query(value = """
            SELECT
                b.id AS "bookingId",
                sc.service_name AS "serviceName",
                c.full_name AS "customerName",
                b.address AS "address",
                b.status AS "status",
                b.scheduled_time AS "scheduledTime",
                b.final_price AS "finalPrice",
                b.payment_method AS "paymentMethod",
                b.issue_description AS "issueDescription"
            FROM bookings b
            LEFT JOIN service_categories sc
                ON sc.id = b.service_id
            LEFT JOIN customers c
                ON c.customer_id = b.customer_id
            WHERE b.worker_id = :workerId
              AND b.scheduled_time IS NOT NULL
              AND DATE(b.scheduled_time AT TIME ZONE 'Asia/Bangkok') = :targetDate
              AND b.status IN ('Accepted', 'Surveying', 'Waiting_Approval', 'In_Progress')
            ORDER BY b.scheduled_time ASC
            """, nativeQuery = true)
    List<WorkerScheduleItemProjection> findScheduleByWorkerIdAndDate(
            @Param("workerId") UUID workerId,
            @Param("targetDate") LocalDate targetDate
    );
}