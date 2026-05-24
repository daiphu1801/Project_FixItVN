package com.fixit.domain.booking.repository;

import com.fixit.domain.booking.entity.BookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingHistoryRepository extends JpaRepository<BookingHistory, UUID> {

    boolean existsByBooking_IdAndStatusUpdate(UUID bookingId, String statusUpdate);

    Optional<BookingHistory> findTopByBooking_IdOrderByUpdatedAtDesc(UUID bookingId);

    // Thêm vào file: BookingHistoryRepository.java
    @Query("SELECT bh.statusUpdate FROM BookingHistory bh WHERE bh.booking.id = :bookingId")
    List<String> findStatusUpdatesByBookingId(@Param("bookingId") UUID bookingId);

}