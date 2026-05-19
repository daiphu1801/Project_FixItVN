package com.fixit.domain.booking.repository;

import com.fixit.domain.booking.entity.BookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingHistoryRepository extends JpaRepository<BookingHistory, UUID> {
}