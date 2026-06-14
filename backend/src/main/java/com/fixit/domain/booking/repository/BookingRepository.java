package com.fixit.domain.booking.repository;

import com.fixit.domain.booking.entity.Booking;
import java.util.List;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

  List<Booking> findByCustomer_CustomerIdOrderByCreatedAtDesc(UUID customerId);

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
      @Param("workerId") UUID workerId);

  // =========================================================================
  // MATCHING ALGORITHM QUERIES
  // =========================================================================

  /**
   * Lấy danh sách các đơn hàng đang chờ thợ (Trạng thái Pending)
   * mà HIỆN TẠI KHÔNG CÓ THỢ NÀO ĐANG ĐƯỢC GÁN CHỜ TRẢ LỜI.
   * (Tức là không có bản ghi nào trong booking_worker_assignments đang ở trạng
   * thái Pending).
   *
   * Ưu tiên đơn hàng cũ được tạo trước (ORDER BY created_at ASC).
   */
  @Query(value = """
      SELECT
          b.id AS bookingId,
          b.service_id AS serviceId,
          b.destination_lat AS destinationLat,
          b.destination_lng AS destinationLng
      FROM bookings b
      WHERE b.status = 'Pending'
        AND b.id NOT IN (
            SELECT bwa.booking_id
            FROM booking_worker_assignments bwa
            WHERE bwa.status = 'Pending'
        )
      ORDER BY b.created_at ASC
      """, nativeQuery = true)
  List<com.fixit.domain.booking.repository.projection.PendingBookingProjection> findUnassignedPendingBookings();
}