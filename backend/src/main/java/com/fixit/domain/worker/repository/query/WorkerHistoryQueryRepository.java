package com.fixit.domain.worker.repository.query;

import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.worker.repository.projection.WorkerHistoryItemProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface WorkerHistoryQueryRepository extends Repository<Booking, UUID> {

    @Query(value = """
            SELECT
                b.id AS "bookingId",
                sc.service_name AS "serviceName",
                c.full_name AS "customerName",
                b.address AS "address",
                b.status AS "status",
                TO_CHAR(
                    b.scheduled_time AT TIME ZONE 'Asia/Bangkok',
                    'YYYY-MM-DD"T"HH24:MI:SS'
                ) AS "scheduledTime",
                TO_CHAR(
                    COALESCE(MAX(bh.updated_at), b.created_at) AT TIME ZONE 'Asia/Bangkok',
                    'YYYY-MM-DD"T"HH24:MI:SS'
                ) AS "finishedAt",
                b.final_price AS "finalPrice",
                b.payment_method AS "paymentMethod",
                b.issue_description AS "issueDescription"
            FROM bookings b
            LEFT JOIN service_categories sc
                ON sc.id = b.service_id
            LEFT JOIN customers c
                ON c.customer_id = b.customer_id
            LEFT JOIN booking_histories bh
                ON bh.booking_id = b.id
               AND bh.status_update = b.status
            WHERE b.worker_id = :workerId
              AND b.status IN ('Completed', 'Cancelled')
              AND (:status = '' OR b.status = :status)
            GROUP BY
                b.id,
                sc.service_name,
                c.full_name,
                b.address,
                b.status,
                b.scheduled_time,
                b.final_price,
                b.payment_method,
                b.issue_description,
                b.created_at
            ORDER BY COALESCE(MAX(bh.updated_at), b.created_at) DESC
            LIMIT :limit
            OFFSET :offset
            """, nativeQuery = true)
    List<WorkerHistoryItemProjection> findHistoryByWorkerId(
            @Param("workerId") UUID workerId,
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
            SELECT COUNT(*)
            FROM bookings b
            WHERE b.worker_id = :workerId
              AND b.status IN ('Completed', 'Cancelled')
              AND (:status = '' OR b.status = :status)
            """, nativeQuery = true)
    long countHistoryByWorkerId(
            @Param("workerId") UUID workerId,
            @Param("status") String status
    );
}