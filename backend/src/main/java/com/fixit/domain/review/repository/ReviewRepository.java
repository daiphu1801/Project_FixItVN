package com.fixit.domain.review.repository;

import com.fixit.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByBookingId(UUID bookingId);

    boolean existsByBookingId(UUID bookingId);

    @Query("SELECT r FROM Review r JOIN FETCH r.customer c JOIN FETCH c.user u WHERE r.booking.worker.workerId = :workerId ORDER BY r.createdAt DESC")
    List<Review> findByWorkerId(@Param("workerId") UUID workerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.booking.worker.workerId = :workerId")
    Double getAverageRatingByWorkerId(@Param("workerId") UUID workerId);
}
