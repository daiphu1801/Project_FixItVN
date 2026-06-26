package com.fixit.domain.booking.repository;

import com.fixit.domain.booking.entity.ComplaintWarranty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ComplaintWarrantyRepository extends JpaRepository<ComplaintWarranty, UUID> {
    Optional<ComplaintWarranty> findByBooking_Id(UUID bookingId);
}
