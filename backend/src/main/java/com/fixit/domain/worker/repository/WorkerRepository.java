package com.fixit.domain.worker.repository;

import com.fixit.domain.worker.entity.Worker;
import com.fixit.domain.worker.repository.projection.WorkerDashboardSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkerRepository extends JpaRepository<Worker, UUID> {

        @Query(value = """
                        SELECT
                            w.worker_id AS "workerId",
                            w.full_name AS "fullName",
                            u.avatar_url AS "avatarUrl",
                            w.is_available AS "available",
                            w.verification_status AS "verificationStatus",
                            w.reputation_score AS "reputationScore",
                            w.latitude AS "latitude",
                            w.longitude AS "longitude",

                            CAST(COALESCE((
                                SELECT COUNT(*)
                                FROM bookings b
                                WHERE b.worker_id = w.worker_id
                                  AND DATE(b.scheduled_time) = CURRENT_DATE
                                  AND b.status IN ('Accepted', 'Surveying', 'Waiting_Approval', 'In_Progress')
                            ), 0) AS int) AS "todayAppointmentCount",

                            CAST(COALESCE((
                                SELECT COUNT(*)
                                FROM booking_worker_assignments bwa
                                WHERE bwa.worker_id = w.worker_id
                                  AND bwa.status = 'Pending'
                            ), 0) AS int) AS "pendingAssignmentCount",

                            CAST(COALESCE((
                                SELECT COUNT(*)
                                FROM notifications n
                                WHERE n.user_id = w.worker_id
                                  AND n.is_read = false
                            ), 0) AS int) AS "unreadNotificationCount",

                            COALESCE(ww.available_balance, 0) AS "availableBalance",
                            COALESCE(ww.held_balance, 0) AS "heldBalance",
                            COALESCE(ww.debt_balance, 0) AS "debtBalance"

                        FROM workers w
                        JOIN users u ON u.id = w.worker_id
                        LEFT JOIN worker_wallets ww ON ww.worker_id = w.worker_id
                        WHERE w.worker_id = :workerId
                        """, nativeQuery = true)
        Optional<WorkerDashboardSummaryProjection> findHomeSummaryByWorkerId(
                        @Param("workerId") UUID workerId);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query(value = """
                        UPDATE workers
                        SET is_available = :available
                        WHERE worker_id = :workerId
                        """, nativeQuery = true)
        int updateAvailability(
                        @Param("workerId") UUID workerId,
                        @Param("available") Boolean available);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query(value = """
                        UPDATE workers
                        SET latitude = :latitude,
                            longitude = :longitude
                        WHERE worker_id = :workerId
                        """, nativeQuery = true)
        int updateLocation(
                        @Param("workerId") UUID workerId,
                        @Param("latitude") BigDecimal latitude,
                        @Param("longitude") BigDecimal longitude);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query(value = """
                        UPDATE workers
                        SET missed_count = 0
                        WHERE worker_id = :workerId
                        """, nativeQuery = true)
        int resetMissedCount(@Param("workerId") UUID workerId);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query(value = """
                        UPDATE workers
                        SET missed_count = missed_count + 1,
                            is_available = CASE
                                WHEN missed_count + 1 >= :autoOfflineThreshold THEN false
                                ELSE is_available
                            END
                        WHERE worker_id = :workerId
                        """, nativeQuery = true)
        int recordMissedAssignment(
                        @Param("workerId") UUID workerId,
                        @Param("autoOfflineThreshold") int autoOfflineThreshold);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query(value = """
                        UPDATE workers
                        SET rejection_count = rejection_count + 1,
                            rejected_priority_until = CASE
                                WHEN rejection_count + 1 >= 5 THEN now() + INTERVAL '24 hours'
                                ELSE rejected_priority_until
                            END
                        WHERE worker_id = :workerId
                        """, nativeQuery = true)
        int recordRejectedAssignment(@Param("workerId") UUID workerId);

        // =========================================================================
        // MATCHING ALGORITHM QUERIES
        // =========================================================================

        /**
         * Lấy danh sách Thợ ứng viên gần nhất bằng công thức Toán học Haversine.
         * Trả về WorkerCandidateProjection thay vì toàn bộ Entity để tối ưu RAM.
         * 
         * Công thức Haversine:
         * 6371 * acos(cos(radians(lat1)) * cos(radians(lat2)) * cos(radians(lon2) -
         * radians(lon1)) + sin(radians(lat1)) * sin(radians(lat2)))
         */
        @Query(value = """
                        SELECT
                            w.worker_id AS workerId,
                            w.latitude AS latitude,
                            w.longitude AS longitude,
                            w.reputation_score AS reputationScore,
                            w.rejection_count AS rejectionCount,
                            w.rejected_priority_until AS rejectedPriorityUntil
                        FROM workers w
                        JOIN worker_services ws ON ws.worker_id = w.worker_id
                        WHERE w.is_available = true
                          AND w.verification_status = 'Approved'
                          AND ws.service_id = :serviceId
                          AND w.latitude IS NOT NULL
                          AND w.longitude IS NOT NULL
                          AND (
                              6371 * acos(
                                  cos(radians(:latitude)) * cos(radians(w.latitude)) * cos(radians(w.longitude) - radians(:longitude)) +
                                  sin(radians(:latitude)) * sin(radians(w.latitude))
                              )
                          ) <= :radiusKm
                        ORDER BY (
                              6371 * acos(
                                  cos(radians(:latitude)) * cos(radians(w.latitude)) * cos(radians(w.longitude) - radians(:longitude)) +
                                  sin(radians(:latitude)) * sin(radians(w.latitude))
                              )
                        ) ASC
                        LIMIT :limit
                        """, nativeQuery = true)
        List<com.fixit.domain.worker.repository.projection.WorkerCandidateProjection> findCandidatesNearby(
                        @Param("serviceId") Integer serviceId,
                        @Param("latitude") double latitude,
                        @Param("longitude") double longitude,
                        @Param("radiusKm") double radiusKm,
                        @Param("limit") int limit);
}