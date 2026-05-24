package com.fixit.domain.worker.repository.query;

import com.fixit.domain.worker.repository.projection.WorkerActiveBookingProjection;
import com.fixit.domain.worker.repository.projection.WorkerScheduleItemProjection;
import com.fixit.domain.worker.repository.projection.WorkerIncomeChartPointProjection;
import com.fixit.domain.worker.repository.projection.WorkerPerformanceStatsProjection;
import com.fixit.domain.worker.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkerHomeQueryRepository extends JpaRepository<Worker, UUID> {

    @Query(value = """
            SELECT
                b.id AS "bookingId",
                sc.service_name AS "serviceName",
                c.full_name AS "customerName",
                b.address AS "address",
                b.status AS "status",
                TO_CHAR(
                    b.scheduled_time AT TIME ZONE 'Asia/Ho_Chi_Minh',
                    'YYYY-MM-DD"T"HH24:MI:SS'
                ) AS "scheduledTime",
                b.final_price AS "finalPrice"
            FROM bookings b
            LEFT JOIN service_categories sc ON sc.id = b.service_id
            LEFT JOIN customers c ON c.customer_id = b.customer_id
            WHERE b.worker_id = :workerId
              AND b.status IN ('Accepted', 'Surveying', 'Waiting_Approval', 'In_Progress')
            ORDER BY
                CASE b.status
                    WHEN 'In_Progress' THEN 1
                    WHEN 'Waiting_Approval' THEN 2
                    WHEN 'Surveying' THEN 3
                    WHEN 'Accepted' THEN 4
                    ELSE 5
                END,
                b.scheduled_time ASC NULLS LAST,
                b.created_at DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<WorkerActiveBookingProjection> findActiveOrder(
            @Param("workerId") UUID workerId
    );

    @Query(value = """
            SELECT
                b.id AS "bookingId",
                sc.service_name AS "serviceName",
                c.full_name AS "customerName",
                b.address AS "address",
                b.status AS "status",
                TO_CHAR(b.scheduled_time AT TIME ZONE 'Asia/Bangkok',
                        'YYYY-MM-DD"T"HH24:MI:SS') AS "scheduledTime",
                b.final_price AS "finalPrice"
            FROM bookings b
            LEFT JOIN service_categories sc ON sc.id = b.service_id
            LEFT JOIN customers c ON c.customer_id = b.customer_id
            WHERE b.worker_id = :workerId
              AND b.scheduled_time >= :startOfDay
              AND b.scheduled_time < :endOfDay
              AND b.status IN ('Accepted', 'Surveying', 'Waiting_Approval', 'In_Progress')
            ORDER BY b.scheduled_time ASC NULLS LAST
            """, nativeQuery = true)
    List<WorkerScheduleItemProjection> findTodayAppointments(
            @Param("workerId") UUID workerId,
            @Param("startOfDay") OffsetDateTime startOfDay,
            @Param("endOfDay") OffsetDateTime endOfDay
    );

    // SỬA LẠI TRONG: WorkerHomeQueryRepository.java
    @Query(value = """
            SELECT
                CAST(COALESCE((
                    SELECT COUNT(*)
                    FROM bookings b
                    WHERE b.worker_id = :workerId
                      AND b.status = 'Completed'
                      -- TỐI ƯU: So sánh trực tiếp cột với mốc thời gian không dùng hàm DATE()
                      AND b.created_at >= CURRENT_DATE
                ), 0) AS int) AS "completedJobsToday",
                CAST(COALESCE((
                    SELECT COUNT(*)
                    FROM bookings b
                    WHERE b.worker_id = :workerId
                      AND b.status = 'Completed'
                      -- TỐI ƯU: Tránh dùng DATE_TRUNC lên cột dữ liệu bên trái
                      AND b.created_at >= DATE_TRUNC('month', CURRENT_DATE)
                ), 0) AS int) AS "completedJobsThisMonth",
                COALESCE((
                    SELECT SUM(th.amount)
                    FROM transaction_histories th
                    WHERE th.wallet_id = :workerId
                      AND th.transaction_type = 'Release'
                      AND th.status = 'Success'
                      -- TỐI ƯU
                      AND th.transaction_time >= CURRENT_DATE
                ), 0) AS "incomeToday",
                COALESCE((
                    SELECT SUM(th.amount)
                    FROM transaction_histories th
                    WHERE th.wallet_id = :workerId
                      AND th.transaction_type = 'Release'
                      AND th.status = 'Success'
                      AND th.transaction_time >= DATE_TRUNC('week', CURRENT_DATE)
                ), 0) AS "incomeThisWeek",
                COALESCE((
                    SELECT SUM(th.amount)
                    FROM transaction_histories th
                    WHERE th.wallet_id = :workerId
                      AND th.transaction_type = 'Release'
                      AND th.status = 'Success'
                      AND th.transaction_time >= DATE_TRUNC('month', CURRENT_DATE)
                ), 0) AS "incomeThisMonth",
                COALESCE((
                    SELECT AVG(r.rating)
                    FROM reviews r
                    JOIN bookings b ON b.id = r.booking_id
                    WHERE b.worker_id = :workerId
                ), 0) AS "averageRating",
                CAST(COALESCE((
                    SELECT COUNT(*)
                    FROM reviews r
                    JOIN bookings b ON b.id = r.booking_id
                    WHERE b.worker_id = :workerId
                ), 0) AS int) AS "totalReviews"
            """, nativeQuery = true)
    WorkerPerformanceStatsProjection findStatsOverview(
            @Param("workerId") UUID workerId
    );


    @Query(value = """
            SELECT
                TO_CHAR(days.day, 'Dy') AS "label",
                COALESCE(SUM(th.amount), 0) AS "income",
                CAST(COALESCE(COUNT(DISTINCT th.booking_id), 0) AS int) AS "completedJobs"
            FROM generate_series(
                CURRENT_DATE - INTERVAL '6 days',
                CURRENT_DATE,
                INTERVAL '1 day'
            ) AS days(day)
            LEFT JOIN transaction_histories th
                -- TỐI ƯU: Đổi sang so sánh khoảng (Range check) để dùng được Index trên th.transaction_time
                ON th.transaction_time >= days.day
               AND th.transaction_time < days.day + INTERVAL '1 day'
               AND th.wallet_id = :workerId
               AND th.transaction_type = 'Release'
               AND th.status = 'Success'
            GROUP BY days.day
            ORDER BY days.day ASC
            """, nativeQuery = true)
    List<WorkerIncomeChartPointProjection> findIncomeChartLast7Days(
            @Param("workerId") UUID workerId
    );
}