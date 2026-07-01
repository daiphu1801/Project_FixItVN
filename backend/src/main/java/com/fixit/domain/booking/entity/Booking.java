package com.fixit.domain.booking.entity;

import com.fixit.domain.customer.entity.Customer;
import com.fixit.domain.service_categories.entity.ServiceCategory;
import com.fixit.domain.worker.entity.Worker;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceCategory serviceCategory;

    @Column(name = "address", nullable = false, columnDefinition = "text")
    private String address;

    @Column(name = "destination_lat", precision = 10, scale = 8)
    private BigDecimal destinationLat;

    @Column(name = "destination_lng", precision = 11, scale = 8)
    private BigDecimal destinationLng;

    @Column(name = "issue_description", columnDefinition = "text")
    private String issueDescription;

    @Column(name = "scheduled_time")
    private OffsetDateTime scheduledTime;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50)
    private BookingPaymentMethod paymentMethod = BookingPaymentMethod.CASH;

    @Column(name = "final_price", precision = 12, scale = 2)
    private BigDecimal finalPrice;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private BookingStatus status = BookingStatus.Pending;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
}
