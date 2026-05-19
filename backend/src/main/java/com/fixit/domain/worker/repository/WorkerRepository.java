package com.fixit.domain.worker.repository;

import com.fixit.domain.worker.entity.Worker;
import com.fixit.domain.worker.repository.projection.WorkerHomeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface WorkerRepository extends JpaRepository<Worker, UUID> {

    @Query(
            value = """
                    SELECT
                        w.worker_id AS workerId,
                        w.full_name AS fullName,
                        w.is_available AS available,
                        w.verification_status AS verificationStatus,
                        w.reputation_score AS reputationScore,
                        w.latitude AS latitude,
                        w.longitude AS longitude,

                        COALESCE((
                            SELECT COUNT(*)
                            FROM bookings b
                            WHERE b.worker_id = w.worker_id
                              AND DATE(b.scheduled_time) = CURRENT_DATE
                              AND b.status IN ('Accepted', 'Surveying', 'Waiting_Approval', 'In_Progress')
                        ), 0) AS todayAppointmentCount,

                        COALESCE((
                            SELECT COUNT(*)
                            FROM booking_worker_assignments bwa
                            WHERE bwa.worker_id = w.worker_id
                              AND bwa.status = 'Pending'
                        ), 0) AS pendingAssignmentCount,

                        COALESCE(ww.available_balance, 0) AS availableBalance,
                        COALESCE(ww.held_balance, 0) AS heldBalance,
                        COALESCE(ww.debt_balance, 0) AS debtBalance

                    FROM workers w
                    LEFT JOIN worker_wallets ww
                        ON ww.worker_id = w.worker_id
                    WHERE w.worker_id = :workerId
                    """,
            nativeQuery = true
    )
    Optional<WorkerHomeProjection> findWorkerHomeByWorkerId(@Param("workerId") UUID workerId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            value = """
                    UPDATE workers
                    SET is_available = :available
                    WHERE worker_id = :workerId
                    """,
            nativeQuery = true
    )
    int updateAvailability(
            @Param("workerId") UUID workerId,
            @Param("available") Boolean available
    );


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            value = """
                    UPDATE workers
                    SET latitude = :latitude,
                        longitude = :longitude
                    WHERE worker_id = :workerId
                    """,
            nativeQuery = true
    )
    int updateLocation(
            @Param("workerId") UUID workerId,
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude
    );
}