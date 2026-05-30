package com.fixit.domain.booking.repository;

import com.fixit.domain.booking.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    // SỬA LẠI TRONG: BookingRepository.java
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT booking
            FROM Booking booking
            WHERE booking.id = :bookingId
              AND booking.worker.workerId = :workerId
            """)
    Optional<Booking> findWorkerBookingForUpdate(
            @Param("bookingId") UUID bookingId,
            @Param("workerId") UUID workerId
    );

}