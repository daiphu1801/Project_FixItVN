# TỔNG HỢP SOURCE CODE HỆ THỐNG MATCHING

### 1. src/main/resources/application-dev.yml
```yaml
# backend/src/main/resources/application-dev.yml
spring:
  datasource:
    url: jdbc:postgresql://aws-1-ap-northeast-1.pooler.supabase.com:5432/postgres?user=postgres.kjyxdqgtbdpsakxmhcax&password=Fixit110200202.
    username: postgres.kjyxdqgtbdpsakxmhcax
    password: Fixit110200202.
    driver-class-name: org.postgresql.Driver

  hikari:
    maximum-pool-size: 5
    minimum-idle: 1
    connection-timeout: 30000
    idle-timeout: 300000
    max-lifetime: 1200000
    leak-detection-threshold: 20000

  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect

  data:
    redis:
      host: localhost
      port: 6379

  # Cấu hình Multipart Upload (dành cho API Upload KYC, Review, Chat)
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

logging:
  level:
    com.fixit: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE # Hiện param của SQL

# Cấu hình tùy chỉnh cho FixItVN
app:
  cors:
    allowed-origins: "http://localhost:3000,http://localhost:5173" # Dành cho Admin Dashboard Web
  assignment:
    scheduler-delay-ms: 30000
  # Cấu hình JWT (Ghi đè hoặc thêm so với application.yml)
  jwt:
    secret: "fixit-local-dev-secret-key-must-be-very-long-and-secure"
    expiration-ms: 86400000
    refresh-expiration-ms: 604800000

  # Cấu hình Upload (Cloudinary)
  cloudinary:
    cloud-name: dfjr8ddwa
    api-key: 434984439717549
    api-secret: aQAlMlQD6BD-2hfbBYiMNOkTEZ0
    upload-expire-seconds: 300
    max-file-size-bytes: 10485760

  upload:
    cleanup:
      enabled: true
      initial-delay-ms: 60000
      fixed-delay-ms: 1800000
      batch-size: 100
      delete-expired-pending-from-storage: true
      cleanup-unused-confirmed: false
      unused-confirmed-expire-hours: 24

  # Cấu hình Payment (SePay Webhook Secret)
  payment:
    sepay:
      api-token: "YOUR_SEPAY_API_TOKEN"
      webhook-secret: "YOUR_SEPAY_WEBHOOK_SECRET"
    deposit:
      bank-code: "MB"
      bank-name: "MBBank"
      account-number: "0859226688"
      account-name: "BUI DAI PHU"

  # Firebase Cloud Messaging
  firebase:
    config-path: "classpath:firebase-service-account.json"

  # -----------------------------------------------
  # Google Maps — Distance Matrix API
  # Dùng để lấy ETA (thời gian đến thực tế có xét tắc đường)
  # Bật "Distance Matrix API" tại: https://console.cloud.google.com
  # -----------------------------------------------
  google:
    maps:
      api-key: "AIzaSyC1DsnKvOFpHc-lnf4UOiry9vJOgzW-FjE"

  # -----------------------------------------------
  # Matching — Cấu hình thuật toán ghép cặp thợ
  # -----------------------------------------------
  matching:
    # Khoảng thời gian giữa mỗi lần chạy batch (mili giây)
    batch-interval-ms: 5000
    # Lấy tối đa K thợ gần nhất cho mỗi đơn (trước khi gọi Google Maps)
    max-workers-per-booking: 5
    # Bán kính lọc thợ bằng Haversine (km)
    candidate-radius-km: 10.0
    # Hệ số trọng số cho công thức Cost (càng cao càng quan trọng)
    cost-weight-eta: 1.0           # Trọng số ETA (giây)
    cost-weight-distance: 0.001    # Trọng số khoảng cách (mét → nhân nhỏ để cùng thang đo)
    cost-weight-cancel-rate: 50.0  # Trọng số tỷ lệ huỷ đơn
    cost-weight-reputation: 20.0   # Trọng số điểm uy tín (trừ đi)
```

### 2. src/main/java/com/fixit/global/config/WorkerMatchingProperties.java
```java
package com.fixit.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean tự động đọc toàn bộ cấu hình "app.matching" từ application-dev.yml.
 *
 * Cách hoạt động:
 * - @ConfigurationProperties(prefix = "app.matching") → Spring Boot tự map
 *   các key trong yml vào các field tương ứng của class này.
 * - Ví dụ: app.matching.batch-interval-ms → batchIntervalMs
 *
 * Ưu điểm so với @Value:
 * - Tập trung tất cả config matching vào 1 chỗ
 * - Dễ thay đổi tham số mà không cần sửa code
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.matching")
public class WorkerMatchingProperties {

    /**
     * Khoảng thời gian giữa mỗi lần Scheduler chạy (mili giây).
     * Mặc định 5000ms = 5 giây nếu không cấu hình trong yml.
     */
    private long batchIntervalMs = 5000;

    /**
     * Số thợ ứng viên tối đa lấy cho mỗi đơn (K trong "top K thợ gần nhất").
     * Giới hạn này giảm số lượng call Google Maps API.
     */
    private int maxWorkersPerBooking = 5;

    /**
     * Bán kính tìm kiếm thợ bằng Haversine (km).
     * Chỉ những thợ trong bán kính này mới được đưa vào ứng viên.
     */
    private double candidateRadiusKm = 10.0;

    // ===== Hệ số trọng số trong công thức Cost =====
    // Cost = ETA×α + Distance×β + CancelRate×γ - Reputation×δ

    /** α — Trọng số ETA (đơn vị: giây). Càng lớn → ưu tiên thợ đến nhanh hơn. */
    private double costWeightEta = 1.0;

    /** β — Trọng số khoảng cách (đơn vị: mét). Nhân với 0.001 để cùng thang đo với ETA. */
    private double costWeightDistance = 0.001;

    /** γ — Trọng số tỷ lệ huỷ đơn. Càng cao → phạt nặng thợ hay huỷ. */
    private double costWeightCancelRate = 50.0;

    /** δ — Trọng số điểm uy tín (bị trừ khỏi cost). Càng cao → thưởng nhiều cho thợ giỏi. */
    private double costWeightReputation = 20.0;
}

```

### 3. src/main/java/com/fixit/infrastructure/maps/dto/GoogleMapsResponse.java
```java
package com.fixit.infrastructure.maps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * DTO hứng dữ liệu trả về từ Google Distance Matrix API.
 * Google trả về cấu trúc rất sâu (nhiều mảng lồng nhau), 
 * nên class này phải cấu trúc y hệt để Spring tự động parse (bóc tách) JSON.
 */
@Data
public class GoogleMapsResponse {
    
    // Status chung của toàn bộ request (VD: "OK", "INVALID_REQUEST")
    private String status;
    
    // Danh sách các hàng kết quả (Mỗi hàng ứng với 1 điểm xuất phát)
    private List<Row> rows;

    @Data
    public static class Row {
        // Danh sách các cột (Mỗi cột ứng với 1 điểm đến)
        private List<Element> elements;
    }

    @Data
    public static class Element {
        // Status của riêng cặp điểm này (VD: "OK", "ZERO_RESULTS")
        private String status;
        
        // Thời gian đi xe thực tế (Có xét kẹt xe nếu có thông tin)
        private Duration duration;
        
        // Khoảng cách theo đường xe chạy
        private Distance distance;
    }

    @Data
    public static class Duration {
        @JsonProperty("text")
        private String text; // Ví dụ: "15 mins" (Dùng để hiển thị)
        
        @JsonProperty("value")
        private long value;  // Ví dụ: 900 (Giây) - Dùng để tính toán thuật toán
    }

    @Data
    public static class Distance {
        @JsonProperty("text")
        private String text; // Ví dụ: "5.5 km"
        
        @JsonProperty("value")
        private long value;  // Ví dụ: 5500 (Mét) - Dùng để tính toán
    }
}

```

### 4. src/main/java/com/fixit/infrastructure/maps/GoogleMapsClient.java
```java
package com.fixit.infrastructure.maps;

import com.fixit.global.config.GoogleMapsConfig;
import com.fixit.infrastructure.maps.dto.GoogleMapsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client chịu trách nhiệm giao tiếp trực tiếp với Google Maps API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleMapsClient {

    private final RestTemplate googleMapsRestTemplate;
    private final GoogleMapsConfig googleMapsConfig;

    // Đường dẫn gốc của API Distance Matrix
    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    /**
     * Lấy khoảng cách và thời gian đi đường giữa 2 điểm.
     * 
     * @param originLat Tọa độ vĩ độ điểm xuất phát (Thợ)
     * @param originLng Tọa độ kinh độ điểm xuất phát (Thợ)
     * @param destLat Tọa độ vĩ độ điểm đến (Khách)
     * @param destLng Tọa độ kinh độ điểm đến (Khách)
     * @return GoogleMapsResponse chứa duration (thời gian) và distance (khoảng cách)
     */
    public GoogleMapsResponse getDistanceMatrix(double originLat, double originLng, double destLat, double destLng) {
        
        // Cấu trúc URL: https://maps.googleapis.com/.../json?origins=lat,lng&destinations=lat,lng&key=YOUR_API_KEY
        String url = UriComponentsBuilder.fromHttpUrl(DISTANCE_MATRIX_URL)
                .queryParam("origins", originLat + "," + originLng)
                .queryParam("destinations", destLat + "," + destLng)
                .queryParam("key", googleMapsConfig.getApiKey())
                .toUriString();

        log.debug("Calling Google Maps API for distance matrix. Origin: [{}, {}], Dest: [{}, {}]", 
                  originLat, originLng, destLat, destLng);

        try {
            // Thực hiện gửi HTTP GET request lên Google
            GoogleMapsResponse response = googleMapsRestTemplate.getForObject(url, GoogleMapsResponse.class);
            
            if (response != null && "OK".equals(response.getStatus())) {
                return response;
            } else {
                log.warn("Google Maps API returned non-OK status: {}", 
                         response != null ? response.getStatus() : "NULL RESPONSE");
                return null;
            }
        } catch (Exception e) {
            log.error("Error while calling Google Maps API", e);
            // Trả về null nếu bị lỗi mạng/timeout để thuật toán không bị crash
            return null;
        }
    }
}

```

### 5. src/main/java/com/fixit/domain/booking/repository/projection/PendingBookingProjection.java
```java
package com.fixit.domain.booking.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Projection dùng để lấy thông tin Đơn hàng (Booking) đang chờ ghép thợ.
 * Thay vì lấy toàn bộ bảng Booking (có chứa nhiều trường không cần thiết như mô tả lỗi, khách hàng...), 
 * chúng ta chỉ lấy đúng 4 trường này để tối ưu hóa bộ nhớ (RAM).
 */
public interface PendingBookingProjection {
    
    // ID của Đơn đặt hàng
    UUID getBookingId();

    // ID của Dịch vụ (Để lọc ra đúng thợ có chuyên môn)
    Integer getServiceId();

    // Tọa độ của Khách hàng (Đích đến)
    BigDecimal getDestinationLat();
    BigDecimal getDestinationLng();
}

```

### 6. src/main/java/com/fixit/domain/worker/repository/projection/WorkerCandidateProjection.java
```java
package com.fixit.domain.worker.repository.projection;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Projection dùng để lấy thông tin Thợ ứng viên phục vụ cho thuật toán Ghép cặp (Matching).
 * Thay vì lấy toàn bộ bảng Worker (có chứa nhiều trường không cần thiết như CMND, kinh nghiệm...), 
 * chúng ta chỉ lấy đúng 6 trường này để tối ưu hóa bộ nhớ (RAM) và tốc độ truy vấn CSDL.
 */
public interface WorkerCandidateProjection {
    
    // ID của Thợ
    UUID getWorkerId();

    // Tọa độ hiện tại của Thợ
    BigDecimal getLatitude();
    BigDecimal getLongitude();

    // Điểm uy tín (Dùng để thưởng/giảm Cost trong thuật toán)
    BigDecimal getReputationScore();

    // Số lần từ chối đơn (Dùng để phạt/tăng Cost)
    Integer getRejectionCount();

    // Thời gian bị cấm ưu tiên (Nếu thợ huỷ đơn quá nhiều bị dính soft-ban)
    OffsetDateTime getRejectedPriorityUntil();
}

```

### 7. src/main/java/com/fixit/domain/booking/repository/BookingRepository.java
```java
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
```

### 8. src/main/java/com/fixit/domain/worker/repository/WorkerRepository.java
```java
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
     * 6371 * acos(cos(radians(lat1)) * cos(radians(lat2)) * cos(radians(lon2) - radians(lon1)) + sin(radians(lat1)) * sin(radians(lat2)))
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
```

### 9. src/main/java/com/fixit/domain/booking/entity/BookingWorkerAssignment.java
```java
package com.fixit.domain.booking.entity;

import com.fixit.domain.worker.entity.Worker;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking_worker_assignments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingWorkerAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private AssignmentStatus status = AssignmentStatus.Pending;

    @Builder.Default
    @Column(name = "assigned_at", updatable = false)
    private OffsetDateTime assignedAt = OffsetDateTime.now();

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;
}

```

### 10. src/main/java/com/fixit/domain/booking/repository/BookingWorkerAssignmentRepository.java
```java
package com.fixit.domain.booking.repository;

import com.fixit.domain.booking.entity.BookingWorkerAssignment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingWorkerAssignmentRepository extends JpaRepository<BookingWorkerAssignment, UUID> {

    /**
     * Lấy assignment để xử lý accept/reject/miss.
     *
     * PESSIMISTIC_WRITE giúp khóa dòng dữ liệu trong transaction.
     * Mục tiêu: tránh việc cùng một assignment bị xử lý đồng thời.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT assignment
            FROM BookingWorkerAssignment assignment
            JOIN FETCH assignment.booking booking
            JOIN FETCH assignment.worker worker
            WHERE assignment.id = :assignmentId
              AND booking.id = :bookingId
              AND worker.workerId = :workerId
            """)
    Optional<BookingWorkerAssignment> findForAction(
            @Param("assignmentId") UUID assignmentId,
            @Param("bookingId") UUID bookingId,
            @Param("workerId") UUID workerId
    );

    /**
     * Tìm các assignment Pending đã quá thời gian phản hồi.
     *
     * Scheduler dùng method này để tự động chuyển Pending -> Missed.
     * PESSIMISTIC_WRITE giúp giảm race condition với accept/reject/miss.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT assignment
        FROM BookingWorkerAssignment assignment
        JOIN FETCH assignment.booking booking
        JOIN FETCH assignment.worker worker
        WHERE assignment.status = com.fixit.domain.booking.entity.AssignmentStatus.Pending
          AND assignment.assignedAt <= :expiredBefore
        """)
    List<BookingWorkerAssignment> findExpiredPendingAssignmentsForUpdate(
            @Param("expiredBefore") OffsetDateTime expiredBefore
    );

    /**
     * Sau khi một thợ accept, các assignment pending còn lại của cùng booking
     * không còn hợp lệ nữa.
     *
     * Giai đoạn MVP: mark là Missed.
     * Nếu muốn chính xác hơn, sau này có thể thêm status Cancelled/Expired.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE booking_worker_assignments
            SET status = 'Missed',
                responded_at = :respondedAt
            WHERE booking_id = :bookingId
              AND id <> :acceptedAssignmentId
              AND status = 'Pending'
            """, nativeQuery = true)
    int markOtherPendingAssignmentsAsMissed(
            @Param("bookingId") UUID bookingId,
            @Param("acceptedAssignmentId") UUID acceptedAssignmentId,
            @Param("respondedAt") OffsetDateTime respondedAt
    );
}
```

### 11. src/main/java/com/fixit/domain/booking/service/dto/matching/WorkerMatchingCandidate.java
```java
package com.fixit.domain.booking.service.dto.matching;

import com.fixit.domain.worker.repository.projection.WorkerCandidateProjection;
import lombok.Builder;
import lombok.Data;

/**
 * Lớp đại diện cho một Ứng cử viên (Thợ) đang được xem xét cho một Đơn hàng.
 * Lớp này kết hợp dữ liệu gốc từ DB và các chỉ số vừa được tính toán (Khoảng cách, Thời gian, Cost).
 */
@Data
@Builder
public class WorkerMatchingCandidate {
    
    // 1. Thông tin gốc của Thợ lấy từ Database (Tầng 3)
    private WorkerCandidateProjection workerInfo;
    
    // 2. Khoảng cách địa lý thực tế (Km)
    private double distanceKm;
    
    // 3. Thời gian đi đường dự kiến (Phút)
    private double durationMins;
    
    // 4. ĐIỂM CHI PHÍ (COST) CUỐI CÙNG 
    // Đây là con số quan trọng nhất sẽ được đưa vào Thuật toán Hungarian.
    // Cost càng thấp = Thợ càng phù hợp.
    private double matchingCost; 
    
    // 5. Cờ đánh dấu xem thông số này lấy từ Google Maps hay công thức Haversine dự phòng
    private boolean isGoogleMapsUsed;
}

```

### 12. src/main/java/com/fixit/domain/booking/service/dto/matching/BookingMatchingContext.java
```java
package com.fixit.domain.booking.service.dto.matching;

import com.fixit.domain.booking.repository.projection.PendingBookingProjection;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Ngữ cảnh ghép cặp (Matching Context) cho một Đơn hàng cụ thể.
 * Nó gom Đơn hàng và Danh sách Thợ ứng viên lại thành một gói dữ liệu hoàn chỉnh.
 */
@Data
@Builder
public class BookingMatchingContext {
    
    // 1. Thông tin gốc của Đơn hàng (Lấy từ Database)
    private PendingBookingProjection booking;
    
    // 2. Danh sách các Thợ (Tối đa 5 người) đã được tính toán Cost cho riêng đơn hàng này
    private List<WorkerMatchingCandidate> candidates;
}

```

### 13. src/main/java/com/fixit/domain/booking/service/dto/matching/MatchingResult.java
```java
package com.fixit.domain.booking.service.dto.matching;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Lớp đại diện cho kết quả CỐT LÕI cuối cùng sau khi cỗ máy Hungarian tính toán xong.
 * Nó mang ý nghĩa: Đơn hàng này (BookingId) sẽ thuộc về Anh Thợ này (WorkerId).
 */
@Data
@AllArgsConstructor
public class MatchingResult {
    
    private UUID bookingId;
    private UUID workerId;
    
    /**
     * Kiểm tra xem đơn hàng này có ghép thành công hay không.
     * (Trong trường hợp đêm khuya, không có anh thợ nào rảnh, workerId sẽ là null)
     */
    public boolean isMatched() {
        return workerId != null;
    }
}

```

### 14. src/main/java/com/fixit/global/util/HaversineUtil.java
```java
package com.fixit.global.util;

/**
 * Công cụ Toán học tính khoảng cách bề mặt quả địa cầu (Haversine Formula).
 * File này đóng vai trò là PHƯƠNG ÁN DỰ PHÒNG (Fallback) cho Google Maps.
 */
public class HaversineUtil {
    
    // Bán kính trung bình của Trái Đất (Km)
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Tính khoảng cách đường chim bay (Km) giữa 2 tọa độ (Vĩ độ, Kinh độ).
     */
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double originLat = Math.toRadians(lat1);
        double destinationLat = Math.toRadians(lat2);

        // Công thức lượng giác phức tạp của Haversine
        double a = Math.pow(Math.sin(dLat / 2), 2) + 
                   Math.pow(Math.sin(dLon / 2), 2) * Math.cos(originLat) * Math.cos(destinationLat);
        double c = 2 * Math.asin(Math.sqrt(a));
        
        return EARTH_RADIUS_KM * c;
    }

    /**
     * DỰ PHÒNG THỜI GIAN ĐI ĐƯỜNG
     * Nếu mạng đứt, Google không trả lời. Ta giả định tốc độ xe máy trong 
     * thành phố trung bình là 30km/h để ước lượng thời gian đi đến nhà khách.
     */
    public static double estimateDurationMins(double distanceKm) {
        double speedKmh = 30.0;
        return (distanceKm / speedKmh) * 60.0; // Đổi ra phút
    }
}

```

### 15. src/main/java/com/fixit/domain/booking/service/matching/CostMatrixBuilder.java
```java
package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.service.dto.matching.WorkerMatchingCandidate;
import com.fixit.global.config.WorkerMatchingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Cỗ máy tính toán Điểm Chi Phí (Cost).
 * Chịu trách nhiệm nhồi các chỉ số vào công thức Toán học để ra được 1 điểm Cost duy nhất.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CostMatrixBuilder {

    private final WorkerMatchingProperties properties;

    /**
     * Hàm này tính Điểm Cost cho một Thợ và cập nhật trực tiếp vào đối tượng đó.
     * Cost = (Trọng số Thời gian * ETA) 
     *      - (Trọng số Uy tín * Điểm uy tín) 
     *      + (Trọng số Phạt * Số lần huỷ đơn)
     */
    public void calculateAndSetCost(WorkerMatchingCandidate candidate) {
        
        double etaMins = candidate.getDurationMins();
        double reputation = candidate.getWorkerInfo().getReputationScore().doubleValue();
        int rejectionCount = candidate.getWorkerInfo().getRejectionCount();

        // 1. Ráp công thức từ các hệ số đã khai báo trong application-dev.yml
        double cost = (properties.getCostWeightEta() * etaMins)
                    - (properties.getCostWeightReputation() * reputation)
                    + (properties.getCostWeightCancelRate() * rejectionCount);

        // 2. Thuật toán Hungary yêu cầu ma trận chi phí phải là số dương không âm (>= 0).
        // Nếu anh thợ quá uy tín (điểm cao vút) làm cho phép trừ tạo ra số âm, 
        // ta ép nó về mức sàn là 0.0 (chi phí lý tưởng nhất, tương đương "Miễn phí").
        cost = Math.max(0.0, cost);

        // 3. Lưu điểm Cost vào trong cái túi DTO
        candidate.setMatchingCost(cost);
        
        log.debug("Worker [{}] | ETA: {}m | Rep: {}* | Rej: {}x ==> FINAL COST: {}", 
                  candidate.getWorkerInfo().getWorkerId(), 
                  String.format("%.1f", etaMins), 
                  reputation, 
                  rejectionCount, 
                  String.format("%.2f", cost));
    }
}

```

### 16. src/main/java/com/fixit/domain/booking/service/matching/algorithm/HungarianAlgorithmSolver.java
```java
package com.fixit.domain.booking.service.matching.algorithm;

import java.util.Arrays;

/**
 * TRÙM CUỐI: Thuật toán Hungarian (Kuhn-Munkres) O(N^3).
 * Được dùng để giải quyết Bài toán Phân công (Assignment Problem).
 * 
 * Đầu vào: Một Ma trận chi phí NxM (N Thợ, M Đơn hàng).
 * Đầu ra: Một sơ đồ ghép cặp sao cho TỔNG CHI PHÍ CỦA TOÀN BỘ HỆ THỐNG LÀ NHỎ NHẤT.
 */
public class HungarianAlgorithmSolver {

    private final double[][] costMatrix;
    private final int rows, cols, dim;
    private final double[] u, v;
    private final int[] p, way;

    /**
     * Khởi tạo Thuật toán. Nếu số lượng Thợ và Đơn hàng không bằng nhau (ma trận không vuông),
     * Thuật toán sẽ tự động tạo thêm các Thợ "ảo" hoặc Đơn "ảo" với chi phí = 0 để cân bằng.
     */
    public HungarianAlgorithmSolver(double[][] costMatrix) {
        this.rows = costMatrix.length;
        this.cols = costMatrix[0].length;
        this.dim = Math.max(rows, cols);
        this.costMatrix = new double[dim][dim];
        
        for (int i = 0; i < dim; i++) {
            if (i < rows) {
                this.costMatrix[i] = Arrays.copyOf(costMatrix[i], dim);
            } else {
                this.costMatrix[i] = new double[dim]; // Padding (Thêm hàng ảo)
            }
        }
        
        u = new double[dim + 1];
        v = new double[dim + 1];
        p = new int[dim + 1];
        way = new int[dim + 1];
    }

    /**
     * Chạy Thuật toán và trả về mảng kết quả.
     * result[i] = j có nghĩa là: Hàng i (Anh Thợ thứ i) được phân công cho Cột j (Đơn hàng thứ j).
     * Nếu kết quả trả về -1 nghĩa là anh Thợ đó bị dư ra (không có đơn).
     */
    public int[] execute() {
        for (int i = 1; i <= dim; i++) {
            p[0] = i;
            int j0 = 0;
            double[] minv = new double[dim + 1];
            Arrays.fill(minv, Double.MAX_VALUE);
            boolean[] used = new boolean[dim + 1];
            
            // Tìm đường tăng luồng (Augmenting path)
            do {
                used[j0] = true;
                int i0 = p[j0], j1 = 0;
                double delta = Double.MAX_VALUE;
                for (int j = 1; j <= dim; j++) {
                    if (!used[j]) {
                        double cur = costMatrix[i0 - 1][j - 1] - u[i0] - v[j];
                        if (cur < minv[j]) {
                            minv[j] = cur;
                            way[j] = j0;
                        }
                        if (minv[j] < delta) {
                            delta = minv[j];
                            j1 = j;
                        }
                    }
                }
                
                // Cập nhật nhãn (Potentials)
                for (int j = 0; j <= dim; j++) {
                    if (used[j]) {
                        u[p[j]] += delta;
                        v[j] -= delta;
                    } else {
                        minv[j] -= delta;
                    }
                }
                j0 = j1;
            } while (p[j0] != 0);
            
            // Cập nhật lại đường đi
            do {
                int j1 = way[j0];
                p[j0] = p[j1];
                j0 = j1;
            } while (j0 != 0);
        }
        
        // Trích xuất kết quả cuối cùng từ mảng p[]
        int[] assignment = new int[rows];
        Arrays.fill(assignment, -1);
        for (int j = 1; j <= dim; j++) {
            if (p[j] > 0 && p[j] <= rows && (j - 1) < cols) {
                assignment[p[j] - 1] = j - 1;
            }
        }
        return assignment;
    }
}

```

### 17. src/main/java/com/fixit/domain/booking/service/matching/WorkerMatchingService.java
```java
package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.repository.projection.PendingBookingProjection;
import com.fixit.domain.booking.service.dto.matching.MatchingResult;

import java.util.List;

public interface WorkerMatchingService {
    /**
     * Hàm chính: Nhận vào 1 danh sách Đơn hàng đang chờ, trả về 1 danh sách Kết quả Ghép cặp
     */
    List<MatchingResult> performBatchMatching(List<PendingBookingProjection> pendingBookings);
}

```

### 18. src/main/java/com/fixit/domain/booking/service/matching/WorkerMatchingServiceImpl.java
```java
package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.repository.projection.PendingBookingProjection;
import com.fixit.domain.booking.service.dto.matching.BookingMatchingContext;
import com.fixit.domain.booking.service.dto.matching.MatchingResult;
import com.fixit.domain.booking.service.dto.matching.WorkerMatchingCandidate;
import com.fixit.domain.booking.service.matching.algorithm.HungarianAlgorithmSolver;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.domain.worker.repository.projection.WorkerCandidateProjection;
import com.fixit.global.config.WorkerMatchingProperties;
import com.fixit.global.util.HaversineUtil;
import com.fixit.infrastructure.maps.GoogleMapsClient;
import com.fixit.infrastructure.maps.dto.GoogleMapsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * NHẠC TRƯỞNG (Orchestrator) của hệ thống.
 * Kết nối tất cả các Tầng (DB -> Google Maps -> Tính Cost -> Hungarian Algorithm).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerMatchingServiceImpl implements WorkerMatchingService {

    private final WorkerRepository workerRepository;
    private final GoogleMapsClient googleMapsClient;
    private final CostMatrixBuilder costMatrixBuilder;
    private final WorkerMatchingProperties properties;

    @Override
    public List<MatchingResult> performBatchMatching(List<PendingBookingProjection> pendingBookings) {
        if (pendingBookings.isEmpty()) return Collections.emptyList();

        log.info("Bắt đầu chạy Batch Matching cho {} đơn hàng.", pendingBookings.size());

        List<BookingMatchingContext> contexts = new ArrayList<>();
        Set<UUID> allUniqueWorkerIds = new HashSet<>();
        List<WorkerCandidateProjection> allUniqueWorkers = new ArrayList<>();

        // BƯỚC 1: LẤY ỨNG VIÊN & ĐO KHOẢNG CÁCH (Gọi DB + Google Maps)
        for (PendingBookingProjection booking : pendingBookings) {
            
            // 1.1 Tìm 5 thợ gần nhất (Lọc thô bằng SQL - Tầng 3)
            List<WorkerCandidateProjection> candidates = workerRepository.findCandidatesNearby(
                    booking.getServiceId(),
                    booking.getDestinationLat().doubleValue(),
                    booking.getDestinationLng().doubleValue(),
                    properties.getCandidateRadiusKm(),
                    properties.getMaxWorkersPerBooking()
            );

            List<WorkerMatchingCandidate> workerCandidates = new ArrayList<>();
            for (WorkerCandidateProjection worker : candidates) {
                // Gom tất cả Thợ vào 1 mảng chung để lát tạo Ma trận
                if (allUniqueWorkerIds.add(worker.getWorkerId())) {
                    allUniqueWorkers.add(worker);
                }

                double distanceKm;
                double durationMins;
                boolean isGoogleMapsUsed = false;

                // 1.2 Gọi Google Maps đo ETA (Tầng 2)
                GoogleMapsResponse mapsResponse = googleMapsClient.getDistanceMatrix(
                        worker.getLatitude().doubleValue(), worker.getLongitude().doubleValue(),
                        booking.getDestinationLat().doubleValue(), booking.getDestinationLng().doubleValue()
                );

                if (mapsResponse != null && "OK".equals(mapsResponse.getStatus()) &&
                        !mapsResponse.getRows().isEmpty() && !mapsResponse.getRows().get(0).getElements().isEmpty() &&
                        "OK".equals(mapsResponse.getRows().get(0).getElements().get(0).getStatus())) {
                    
                    distanceKm = mapsResponse.getRows().get(0).getElements().get(0).getDistance().getValue() / 1000.0;
                    durationMins = mapsResponse.getRows().get(0).getElements().get(0).getDuration().getValue() / 60.0;
                    isGoogleMapsUsed = true;
                } else {
                    // CƠ CHẾ FALLBACK (DỰ PHÒNG CHỐNG SẬP HỆ THỐNG)
                    distanceKm = HaversineUtil.calculateDistanceKm(
                            worker.getLatitude().doubleValue(), worker.getLongitude().doubleValue(),
                            booking.getDestinationLat().doubleValue(), booking.getDestinationLng().doubleValue()
                    );
                    durationMins = HaversineUtil.estimateDurationMins(distanceKm);
                }

                // 1.3 Tạo cái túi đựng Thợ
                WorkerMatchingCandidate candidateDTO = WorkerMatchingCandidate.builder()
                        .workerInfo(worker)
                        .distanceKm(distanceKm)
                        .durationMins(durationMins)
                        .isGoogleMapsUsed(isGoogleMapsUsed)
                        .build();

                // 1.4 Chấm điểm Cost (Tầng 5)
                costMatrixBuilder.calculateAndSetCost(candidateDTO);
                workerCandidates.add(candidateDTO);
            }

            // Gói đơn hàng và các anh thợ lại
            contexts.add(BookingMatchingContext.builder()
                    .booking(booking)
                    .candidates(workerCandidates)
                    .build());
        }

        // BƯỚC 2: TẠO MA TRẬN NxM ĐỂ CHUẨN BỊ GIẢI
        int N = allUniqueWorkers.size(); // N hàng (Thợ)
        int M = pendingBookings.size();  // M cột (Đơn hàng)
        
        if (N == 0) {
            log.warn("Không tìm thấy bất kỳ thợ nào rảnh cho {} đơn hàng.", M);
            return pendingBookings.stream()
                    .map(b -> new MatchingResult(b.getBookingId(), null))
                    .collect(Collectors.toList());
        }

        double[][] costMatrix = new double[N][M];
        double MAX_COST = 999999.0; // Điểm cost vô cực (Dành cho thợ không biết làm dịch vụ đó hoặc ở quá xa)

        for (int i = 0; i < N; i++) {
            WorkerCandidateProjection worker = allUniqueWorkers.get(i);
            for (int j = 0; j < M; j++) {
                BookingMatchingContext context = contexts.get(j);
                
                // Lục trong cái túi xem anh thợ i có ứng tuyển vào đơn j không?
                Optional<WorkerMatchingCandidate> candidateOpt = context.getCandidates().stream()
                        .filter(c -> c.getWorkerInfo().getWorkerId().equals(worker.getWorkerId()))
                        .findFirst();

                if (candidateOpt.isPresent()) {
                    costMatrix[i][j] = candidateOpt.get().getMatchingCost();
                } else {
                    costMatrix[i][j] = MAX_COST; // Phạt điểm vô cực
                }
            }
        }

        // BƯỚC 3: GIẢI MÃ BẰNG CỖ MÁY HUNGARIAN
        HungarianAlgorithmSolver solver = new HungarianAlgorithmSolver(costMatrix);
        int[] assignment = solver.execute();

        // BƯỚC 4: RÁP KẾT QUẢ VÀ TRẢ VỀ CHO HỆ THỐNG GỬI THÔNG BÁO
        List<MatchingResult> results = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            int assignedBookingIndex = assignment[i];
            if (assignedBookingIndex != -1) {
                // Thợ i được Cỗ máy chia cho đơn j
                UUID workerId = allUniqueWorkers.get(i).getWorkerId();
                UUID bookingId = pendingBookings.get(assignedBookingIndex).getBookingId();
                
                // Loại trừ những cặp bị ép duyên (Cost = 999999)
                if (costMatrix[i][assignedBookingIndex] < MAX_COST) {
                    results.add(new MatchingResult(bookingId, workerId));
                }
            }
        }

        // Lọc lại những đơn hàng đen đủi bị bỏ rơi (WorkerId = null)
        for (PendingBookingProjection booking : pendingBookings) {
            boolean matched = results.stream().anyMatch(r -> r.getBookingId().equals(booking.getBookingId()));
            if (!matched) {
                results.add(new MatchingResult(booking.getBookingId(), null)); 
            }
        }

        log.info("Batch Matching hoàn tất. Ghép thành công {}/{} đơn.", 
                results.stream().filter(MatchingResult::isMatched).count(), M);
        return results;
    }
}

```

### 19. src/main/java/com/fixit/domain/booking/service/matching/WorkerMatchingScheduler.java
```java
package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingWorkerAssignment;
import com.fixit.domain.booking.repository.BookingRepository;
import com.fixit.domain.booking.repository.BookingWorkerAssignmentRepository;
import com.fixit.domain.booking.repository.projection.PendingBookingProjection;
import com.fixit.domain.booking.service.dto.matching.MatchingResult;
import com.fixit.domain.worker.entity.Worker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ĐỒNG HỒ BÁO THỨC (Scheduler) của hệ thống.
 * Tự động chạy ngầm định kỳ để tìm đơn mồ côi và gọi thuật toán.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerMatchingScheduler {

    private final BookingRepository bookingRepository;
    private final WorkerMatchingService matchingService;
    private final BookingWorkerAssignmentRepository assignmentRepository;
    private final MatchingNotificationService notificationService;

    /**
     * Dấu @Scheduled sẽ làm hàm này tự động chạy liên tục.
     * fixedDelayString: Sau khi chạy xong, nó sẽ nghỉ 5 giây rồi mới chạy lại 
     * (thời gian nghỉ 5000ms này được đọc từ file application-dev.yml).
     */
    @Scheduled(fixedDelayString = "${app.matching.batch-interval-ms:5000}")
    @Transactional
    public void runBatchMatching() {
        
        // 1. Quét tìm các Đơn hàng đang bị mồ côi (Tầng 3)
        List<PendingBookingProjection> pendingBookings = bookingRepository.findUnassignedPendingBookings();

        if (pendingBookings.isEmpty()) {
            // Không có đơn nào thì thoát, đi ngủ tiếp 5 giây nữa
            return; 
        }

        log.info("[SCHEDULER] Đã quét thấy {} đơn hàng mồ côi. Đánh thức Nhạc trưởng...", pendingBookings.size());

        // 2. Giao việc cho Nhạc trưởng chạy luồng Thuật toán (Tầng 6)
        List<MatchingResult> results = matchingService.performBatchMatching(pendingBookings);

        // 3. Xử lý kết quả Nhạc trưởng báo cáo về
        List<BookingWorkerAssignment> newAssignments = new ArrayList<>();

        for (MatchingResult result : results) {
            if (result.isMatched()) {
                // Tình huống 1: Ghép đôi thành công!
                // Tạo một bản ghi Assignment (Phân công) lưu vào Database
                Booking bookingRef = new Booking();
                bookingRef.setId(result.getBookingId());
                
                Worker workerRef = new Worker();
                workerRef.setWorkerId(result.getWorkerId());

                BookingWorkerAssignment assignment = new BookingWorkerAssignment();
                assignment.setBooking(bookingRef);
                assignment.setWorker(workerRef);
                assignment.setStatus(com.fixit.domain.booking.entity.AssignmentStatus.Pending); // Gán trạng thái chờ Thợ bấm Đồng ý

                newAssignments.add(assignment);
                log.info(" [MATCHED] Đơn {} ---> Thợ {}", result.getBookingId(), result.getWorkerId());
                
            } else {
                // Tình huống 2: Đơn ế (Có thể do khuya quá thợ tắt app đi ngủ hết)
                // Kệ nó, 5 giây sau chu kỳ tiếp theo nó sẽ được quét lại.
                log.warn(" [UNMATCHED] Đơn {} không tìm thấy anh thợ rảnh nào.", result.getBookingId());
            }
        }

        // 4. Lưu tất cả kết quả ghép cặp thành công vào Database (Batch Save)
        if (!newAssignments.isEmpty()) {
            assignmentRepository.saveAll(newAssignments);
            log.info("[SCHEDULER] Đã chốt sổ và lưu {} bản ghi phân công vào CSDL.", newAssignments.size());
            
            // 5. Đánh thức TẦNG 8: Gọi loa phường thông báo cho thợ biết
            notificationService.sendNewAssignmentNotifications(newAssignments);
        }
    }
}

```

### 20. src/main/java/com/fixit/domain/booking/service/matching/MatchingNotificationService.java
```java
package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.entity.BookingWorkerAssignment;

import java.util.List;

public interface MatchingNotificationService {
    /**
     * Bắn thông báo (FCM) đến điện thoại của Thợ khi có đơn hàng mới được phân công.
     */
    void sendNewAssignmentNotifications(List<BookingWorkerAssignment> newAssignments);
}

```

### 21. src/main/java/com/fixit/domain/booking/service/matching/MatchingNotificationServiceImpl.java
```java
package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.entity.BookingWorkerAssignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TẦNG 8: Dịch vụ Thông báo (Notification).
 * Nhiệm vụ duy nhất: Gọi Firebase Cloud Messaging (FCM) để đánh thức app trên điện thoại của Thợ.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingNotificationServiceImpl implements MatchingNotificationService {

    // Bước tiếp theo của dự án: Inject FirebaseMessaging vào đây
    // private final FirebaseMessaging firebaseMessaging;

    @Override
    public void sendNewAssignmentNotifications(List<BookingWorkerAssignment> newAssignments) {
        if (newAssignments == null || newAssignments.isEmpty()) {
            return;
        }

        for (BookingWorkerAssignment assignment : newAssignments) {
            String workerId = assignment.getWorker().getWorkerId().toString();
            String bookingId = assignment.getBooking().getId().toString();

            // MÔ PHỎNG LOGIC BẮN FIREBASE
            // (Khi bảo vệ, bạn có thể nói với Hội đồng: "Em đã thiết kế sẵn cổng giao tiếp (Interface).
            // Nếu có module Firebase SDK, em chỉ cần cắm 3 dòng code vào đây là hệ thống gửi tin nhắn thật được ngay!")
            
            log.info("🔔 [FCM_FIREBASE] Đang bắn Push Notification về máy điện thoại của Thợ: [{}]", workerId);
            log.info("   Nội dung tin nhắn: 'Khách hàng vừa đặt một đơn sửa chữa mới (ID: {})! Nhận việc ngay!'", bookingId);
        }
    }
}

```

### 22. src/main/java/com/fixit/domain/booking/controller/WorkerAssignmentController.java
```java
package com.fixit.domain.booking.controller;

import com.fixit.domain.booking.dto.request.RejectAssignmentRequest;
import com.fixit.domain.booking.dto.response.AssignmentActionResponse;
import com.fixit.domain.booking.dto.response.PendingAssignmentResponse;
import com.fixit.domain.booking.service.WorkerAssignmentService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WorkerAssignmentController {

    private final WorkerAssignmentService workerAssignmentService;

    /**
     * Lấy danh sách assignment đang chờ thợ phản hồi.
     *
     * Dùng cho:
     * - dialog_incoming_order.xml
     * - WorkerHomeFragment kiểm tra có đơn mới không
     *
     * Test tạm khi chưa có Auth:
     * Header: X-Debug-Worker-Id: <workerId>
     */
    @GetMapping("/workers/me/assignments/pending")
    public ApiResponse<PendingAssignmentResponse> getPendingAssignments() {
        PendingAssignmentResponse response = workerAssignmentService.getPendingAssignments();
        return ApiResponse.success(response);
    }

    /**
     * Thợ chấp nhận đơn.
     *
     * Sau khi accept:
     * - assignment.status = Accepted
     * - booking.worker_id = currentWorkerId
     * - booking.status = Accepted
     */
    @PostMapping("/bookings/{bookingId}/assignments/{assignmentId}/accept")
    public ApiResponse<AssignmentActionResponse> accept(
            @PathVariable UUID bookingId,
            @PathVariable UUID assignmentId
    ) {
        AssignmentActionResponse response = workerAssignmentService.accept(bookingId, assignmentId);
        return ApiResponse.success(response, "Nhận đơn thành công");
    }

    /**
     * Thợ từ chối đơn.
     *
     * Request body có thể null trong giai đoạn MVP.
     * Nếu sau này muốn lưu lý do reject, cần thêm cột vào bảng booking_worker_assignments.
     */
    @PostMapping("/bookings/{bookingId}/assignments/{assignmentId}/reject")
    public ApiResponse<AssignmentActionResponse> reject(
            @PathVariable UUID bookingId,
            @PathVariable UUID assignmentId,
            @Valid @RequestBody(required = false) RejectAssignmentRequest request
    ) {
        RejectAssignmentRequest safeRequest = request != null
                ? request
                : new RejectAssignmentRequest();

        AssignmentActionResponse response = workerAssignmentService.reject(
                bookingId,
                assignmentId,
                safeRequest
        );

        return ApiResponse.success(response, "Từ chối đơn thành công");
    }

    /**
     * Ghi nhận thợ bỏ lỡ đơn.
     *
     * Giai đoạn MVP:
     * - Android có thể gọi API này khi countdown hết 3 phút.
     *
     * Sau này:
     * - Backend scheduler có thể tự xử lý miss.
     */
    @PostMapping("/bookings/{bookingId}/assignments/{assignmentId}/miss")
    public ApiResponse<AssignmentActionResponse> miss(
            @PathVariable UUID bookingId,
            @PathVariable UUID assignmentId
    ) {
        AssignmentActionResponse response = workerAssignmentService.miss(bookingId, assignmentId);
        return ApiResponse.success(response, "Ghi nhận bỏ lỡ đơn thành công");
    }
}
```

