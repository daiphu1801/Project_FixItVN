# TỔNG HỢP SOURCE CODE HỆ THỐNG MATCHING

### 1. src\main\resources\application-dev.yml

`yaml
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

  # Cáº¥u hÃ¬nh Multipart Upload (dÃ nh cho API Upload KYC, Review, Chat)
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

logging:
  level:
    com.fixit: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE # Hiá»‡n param cá»§a SQL

# Cáº¥u hÃ¬nh tÃ¹y chá»‰nh cho FixItVN
app:
  cors:
    allowed-origins: "http://localhost:3000,http://localhost:5173" # DÃ nh cho Admin Dashboard Web
  assignment:
    scheduler-delay-ms: 30000
  # Cáº¥u hÃ¬nh JWT (Ghi Ä‘Ã¨ hoáº·c thÃªm so vá»›i application.yml)
  jwt:
    secret: "fixit-local-dev-secret-key-must-be-very-long-and-secure"
    expiration-ms: 86400000
    refresh-expiration-ms: 604800000

  # Cáº¥u hÃ¬nh Upload (Cloudinary)
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

  # Cáº¥u hÃ¬nh Payment (SePay Webhook Secret)
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
  # Google Maps â€” Distance Matrix API
  # DÃ¹ng Ä‘á»ƒ láº¥y ETA (thá»i gian Ä‘áº¿n thá»±c táº¿ cÃ³ xÃ©t táº¯c Ä‘Æ°á»ng)
  # Báº­t "Distance Matrix API" táº¡i: https://console.cloud.google.com
  # -----------------------------------------------
  google:
    maps:
      api-key: "AIzaSyC1DsnKvOFpHc-lnf4UOiry9vJOgzW-FjE"

  # -----------------------------------------------
  # Matching â€” Cáº¥u hÃ¬nh thuáº­t toÃ¡n ghÃ©p cáº·p thá»£
  # -----------------------------------------------
  matching:
    # Khoáº£ng thá»i gian giá»¯a má»—i láº§n cháº¡y batch (mili giÃ¢y)
    batch-interval-ms: 5000
    # Láº¥y tá»‘i Ä‘a K thá»£ gáº§n nháº¥t cho má»—i Ä‘Æ¡n (trÆ°á»›c khi gá»i Google Maps)
    max-workers-per-booking: 5
    # BÃ¡n kÃ­nh lá»c thá»£ báº±ng Haversine (km)
    candidate-radius-km: 10.0
    # Há»‡ sá»‘ trá»ng sá»‘ cho cÃ´ng thá»©c Cost (cÃ ng cao cÃ ng quan trá»ng)
    cost-weight-eta: 1.0           # Trá»ng sá»‘ ETA (giÃ¢y)
    cost-weight-distance: 0.001    # Trá»ng sá»‘ khoáº£ng cÃ¡ch (mÃ©t â†’ nhÃ¢n nhá» Ä‘á»ƒ cÃ¹ng thang Ä‘o)
    cost-weight-cancel-rate: 50.0  # Trá»ng sá»‘ tá»· lá»‡ huá»· Ä‘Æ¡n
    cost-weight-reputation: 20.0   # Trá»ng sá»‘ Ä‘iá»ƒm uy tÃ­n (trá»« Ä‘i)
``n

### 2. src\main\java\com\fixit\global\config\WorkerMatchingProperties.java

`java
package com.fixit.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean tá»± Ä‘á»™ng Ä‘á»c toÃ n bá»™ cáº¥u hÃ¬nh "app.matching" tá»« application-dev.yml.
 *
 * CÃ¡ch hoáº¡t Ä‘á»™ng:
 * - @ConfigurationProperties(prefix = "app.matching") â†’ Spring Boot tá»± map
 *   cÃ¡c key trong yml vÃ o cÃ¡c field tÆ°Æ¡ng á»©ng cá»§a class nÃ y.
 * - VÃ­ dá»¥: app.matching.batch-interval-ms â†’ batchIntervalMs
 *
 * Æ¯u Ä‘iá»ƒm so vá»›i @Value:
 * - Táº­p trung táº¥t cáº£ config matching vÃ o 1 chá»—
 * - Dá»… thay Ä‘á»•i tham sá»‘ mÃ  khÃ´ng cáº§n sá»­a code
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.matching")
public class WorkerMatchingProperties {

    /**
     * Khoáº£ng thá»i gian giá»¯a má»—i láº§n Scheduler cháº¡y (mili giÃ¢y).
     * Máº·c Ä‘á»‹nh 5000ms = 5 giÃ¢y náº¿u khÃ´ng cáº¥u hÃ¬nh trong yml.
     */
    private long batchIntervalMs = 5000;

    /**
     * Sá»‘ thá»£ á»©ng viÃªn tá»‘i Ä‘a láº¥y cho má»—i Ä‘Æ¡n (K trong "top K thá»£ gáº§n nháº¥t").
     * Giá»›i háº¡n nÃ y giáº£m sá»‘ lÆ°á»£ng call Google Maps API.
     */
    private int maxWorkersPerBooking = 5;

    /**
     * BÃ¡n kÃ­nh tÃ¬m kiáº¿m thá»£ báº±ng Haversine (km).
     * Chá»‰ nhá»¯ng thá»£ trong bÃ¡n kÃ­nh nÃ y má»›i Ä‘Æ°á»£c Ä‘Æ°a vÃ o á»©ng viÃªn.
     */
    private double candidateRadiusKm = 10.0;

    // ===== Há»‡ sá»‘ trá»ng sá»‘ trong cÃ´ng thá»©c Cost =====
    // Cost = ETAÃ—Î± + DistanceÃ—Î² + CancelRateÃ—Î³ - ReputationÃ—Î´

    /** Î± â€” Trá»ng sá»‘ ETA (Ä‘Æ¡n vá»‹: giÃ¢y). CÃ ng lá»›n â†’ Æ°u tiÃªn thá»£ Ä‘áº¿n nhanh hÆ¡n. */
    private double costWeightEta = 1.0;

    /** Î² â€” Trá»ng sá»‘ khoáº£ng cÃ¡ch (Ä‘Æ¡n vá»‹: mÃ©t). NhÃ¢n vá»›i 0.001 Ä‘á»ƒ cÃ¹ng thang Ä‘o vá»›i ETA. */
    private double costWeightDistance = 0.001;

    /** Î³ â€” Trá»ng sá»‘ tá»· lá»‡ huá»· Ä‘Æ¡n. CÃ ng cao â†’ pháº¡t náº·ng thá»£ hay huá»·. */
    private double costWeightCancelRate = 50.0;

    /** Î´ â€” Trá»ng sá»‘ Ä‘iá»ƒm uy tÃ­n (bá»‹ trá»« khá»i cost). CÃ ng cao â†’ thÆ°á»Ÿng nhiá»u cho thá»£ giá»i. */
    private double costWeightReputation = 20.0;
}

``n

### 3. src\main\java\com\fixit\infrastructure\maps\dto\GoogleMapsResponse.java

`java
package com.fixit.infrastructure.maps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * DTO há»©ng dá»¯ liá»‡u tráº£ vá» tá»« Google Distance Matrix API.
 * Google tráº£ vá» cáº¥u trÃºc ráº¥t sÃ¢u (nhiá»u máº£ng lá»“ng nhau), 
 * nÃªn class nÃ y pháº£i cáº¥u trÃºc y há»‡t Ä‘á»ƒ Spring tá»± Ä‘á»™ng parse (bÃ³c tÃ¡ch) JSON.
 */
@Data
public class GoogleMapsResponse {
    
    // Status chung cá»§a toÃ n bá»™ request (VD: "OK", "INVALID_REQUEST")
    private String status;
    
    // Danh sÃ¡ch cÃ¡c hÃ ng káº¿t quáº£ (Má»—i hÃ ng á»©ng vá»›i 1 Ä‘iá»ƒm xuáº¥t phÃ¡t)
    private List<Row> rows;

    @Data
    public static class Row {
        // Danh sÃ¡ch cÃ¡c cá»™t (Má»—i cá»™t á»©ng vá»›i 1 Ä‘iá»ƒm Ä‘áº¿n)
        private List<Element> elements;
    }

    @Data
    public static class Element {
        // Status cá»§a riÃªng cáº·p Ä‘iá»ƒm nÃ y (VD: "OK", "ZERO_RESULTS")
        private String status;
        
        // Thá»i gian Ä‘i xe thá»±c táº¿ (CÃ³ xÃ©t káº¹t xe náº¿u cÃ³ thÃ´ng tin)
        private Duration duration;
        
        // Khoáº£ng cÃ¡ch theo Ä‘Æ°á»ng xe cháº¡y
        private Distance distance;
    }

    @Data
    public static class Duration {
        @JsonProperty("text")
        private String text; // VÃ­ dá»¥: "15 mins" (DÃ¹ng Ä‘á»ƒ hiá»ƒn thá»‹)
        
        @JsonProperty("value")
        private long value;  // VÃ­ dá»¥: 900 (GiÃ¢y) - DÃ¹ng Ä‘á»ƒ tÃ­nh toÃ¡n thuáº­t toÃ¡n
    }

    @Data
    public static class Distance {
        @JsonProperty("text")
        private String text; // VÃ­ dá»¥: "5.5 km"
        
        @JsonProperty("value")
        private long value;  // VÃ­ dá»¥: 5500 (MÃ©t) - DÃ¹ng Ä‘á»ƒ tÃ­nh toÃ¡n
    }
}

``n

### 4. src\main\java\com\fixit\infrastructure\maps\GoogleMapsClient.java

`java
package com.fixit.infrastructure.maps;

import com.fixit.global.config.GoogleMapsConfig;
import com.fixit.infrastructure.maps.dto.GoogleMapsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Client chá»‹u trÃ¡ch nhiá»‡m giao tiáº¿p trá»±c tiáº¿p vá»›i Google Maps API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleMapsClient {

    private final RestTemplate googleMapsRestTemplate;
    private final GoogleMapsConfig googleMapsConfig;

    // ÄÆ°á»ng dáº«n gá»‘c cá»§a API Distance Matrix
    private static final String DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    /**
     * Láº¥y khoáº£ng cÃ¡ch vÃ  thá»i gian Ä‘i Ä‘Æ°á»ng giá»¯a 2 Ä‘iá»ƒm.
     * 
     * @param originLat Tá»a Ä‘á»™ vÄ© Ä‘á»™ Ä‘iá»ƒm xuáº¥t phÃ¡t (Thá»£)
     * @param originLng Tá»a Ä‘á»™ kinh Ä‘á»™ Ä‘iá»ƒm xuáº¥t phÃ¡t (Thá»£)
     * @param destLat Tá»a Ä‘á»™ vÄ© Ä‘á»™ Ä‘iá»ƒm Ä‘áº¿n (KhÃ¡ch)
     * @param destLng Tá»a Ä‘á»™ kinh Ä‘á»™ Ä‘iá»ƒm Ä‘áº¿n (KhÃ¡ch)
     * @return GoogleMapsResponse chá»©a duration (thá»i gian) vÃ  distance (khoáº£ng cÃ¡ch)
     */
    public GoogleMapsResponse getDistanceMatrix(double originLat, double originLng, double destLat, double destLng) {
        
        // Cáº¥u trÃºc URL: https://maps.googleapis.com/.../json?origins=lat,lng&destinations=lat,lng&key=YOUR_API_KEY
        String url = UriComponentsBuilder.fromHttpUrl(DISTANCE_MATRIX_URL)
                .queryParam("origins", originLat + "," + originLng)
                .queryParam("destinations", destLat + "," + destLng)
                .queryParam("key", googleMapsConfig.getApiKey())
                .toUriString();

        log.debug("Calling Google Maps API for distance matrix. Origin: [{}, {}], Dest: [{}, {}]", 
                  originLat, originLng, destLat, destLng);

        try {
            // Thá»±c hiá»‡n gá»­i HTTP GET request lÃªn Google
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
            // Tráº£ vá» null náº¿u bá»‹ lá»—i máº¡ng/timeout Ä‘á»ƒ thuáº­t toÃ¡n khÃ´ng bá»‹ crash
            return null;
        }
    }
}

``n

### 5. src\main\java\com\fixit\domain\booking\repository\projection\PendingBookingProjection.java

`java
package com.fixit.domain.booking.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Projection dÃ¹ng Ä‘á»ƒ láº¥y thÃ´ng tin ÄÆ¡n hÃ ng (Booking) Ä‘ang chá» ghÃ©p thá»£.
 * Thay vÃ¬ láº¥y toÃ n bá»™ báº£ng Booking (cÃ³ chá»©a nhiá»u trÆ°á»ng khÃ´ng cáº§n thiáº¿t nhÆ° mÃ´ táº£ lá»—i, khÃ¡ch hÃ ng...), 
 * chÃºng ta chá»‰ láº¥y Ä‘Ãºng 4 trÆ°á»ng nÃ y Ä‘á»ƒ tá»‘i Æ°u hÃ³a bá»™ nhá»› (RAM).
 */
public interface PendingBookingProjection {
    
    // ID cá»§a ÄÆ¡n Ä‘áº·t hÃ ng
    UUID getBookingId();

    // ID cá»§a Dá»‹ch vá»¥ (Äá»ƒ lá»c ra Ä‘Ãºng thá»£ cÃ³ chuyÃªn mÃ´n)
    Integer getServiceId();

    // Tá»a Ä‘á»™ cá»§a KhÃ¡ch hÃ ng (ÄÃ­ch Ä‘áº¿n)
    BigDecimal getDestinationLat();
    BigDecimal getDestinationLng();
}

``n

### 6. src\main\java\com\fixit\domain\worker\repository\projection\WorkerCandidateProjection.java

`java
package com.fixit.domain.worker.repository.projection;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Projection dÃ¹ng Ä‘á»ƒ láº¥y thÃ´ng tin Thá»£ á»©ng viÃªn phá»¥c vá»¥ cho thuáº­t toÃ¡n GhÃ©p cáº·p (Matching).
 * Thay vÃ¬ láº¥y toÃ n bá»™ báº£ng Worker (cÃ³ chá»©a nhiá»u trÆ°á»ng khÃ´ng cáº§n thiáº¿t nhÆ° CMND, kinh nghiá»‡m...), 
 * chÃºng ta chá»‰ láº¥y Ä‘Ãºng 6 trÆ°á»ng nÃ y Ä‘á»ƒ tá»‘i Æ°u hÃ³a bá»™ nhá»› (RAM) vÃ  tá»‘c Ä‘á»™ truy váº¥n CSDL.
 */
public interface WorkerCandidateProjection {
    
    // ID cá»§a Thá»£
    UUID getWorkerId();

    // Tá»a Ä‘á»™ hiá»‡n táº¡i cá»§a Thá»£
    BigDecimal getLatitude();
    BigDecimal getLongitude();

    // Äiá»ƒm uy tÃ­n (DÃ¹ng Ä‘á»ƒ thÆ°á»Ÿng/giáº£m Cost trong thuáº­t toÃ¡n)
    BigDecimal getReputationScore();

    // Sá»‘ láº§n tá»« chá»‘i Ä‘Æ¡n (DÃ¹ng Ä‘á»ƒ pháº¡t/tÄƒng Cost)
    Integer getRejectionCount();

    // Thá»i gian bá»‹ cáº¥m Æ°u tiÃªn (Náº¿u thá»£ huá»· Ä‘Æ¡n quÃ¡ nhiá»u bá»‹ dÃ­nh soft-ban)
    OffsetDateTime getRejectedPriorityUntil();
}

``n

### 7. src\main\java\com\fixit\domain\booking\repository\BookingRepository.java

`java
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

  // Sá»¬A Láº I TRONG: BookingRepository.java
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
   * Láº¥y danh sÃ¡ch cÃ¡c Ä‘Æ¡n hÃ ng Ä‘ang chá» thá»£ (Tráº¡ng thÃ¡i Pending)
   * mÃ  HIá»†N Táº I KHÃ”NG CÃ“ THá»¢ NÃ€O ÄANG ÄÆ¯á»¢C GÃN CHá»œ TRáº¢ Lá»œI.
   * (Tá»©c lÃ  khÃ´ng cÃ³ báº£n ghi nÃ o trong booking_worker_assignments Ä‘ang á»Ÿ tráº¡ng
   * thÃ¡i Pending).
   *
   * Æ¯u tiÃªn Ä‘Æ¡n hÃ ng cÅ© Ä‘Æ°á»£c táº¡o trÆ°á»›c (ORDER BY created_at ASC).
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
``n

### 8. src\main\java\com\fixit\domain\worker\repository\WorkerRepository.java

`java
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
     * Láº¥y danh sÃ¡ch Thá»£ á»©ng viÃªn gáº§n nháº¥t báº±ng cÃ´ng thá»©c ToÃ¡n há»c Haversine.
     * Tráº£ vá» WorkerCandidateProjection thay vÃ¬ toÃ n bá»™ Entity Ä‘á»ƒ tá»‘i Æ°u RAM.
     * 
     * CÃ´ng thá»©c Haversine:
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
``n

### 9. src\main\java\com\fixit\domain\booking\entity\BookingWorkerAssignment.java

`java
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

``n

### 10. src\main\java\com\fixit\domain\booking\repository\BookingWorkerAssignmentRepository.java

`java
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
     * Láº¥y assignment Ä‘á»ƒ xá»­ lÃ½ accept/reject/miss.
     *
     * PESSIMISTIC_WRITE giÃºp khÃ³a dÃ²ng dá»¯ liá»‡u trong transaction.
     * Má»¥c tiÃªu: trÃ¡nh viá»‡c cÃ¹ng má»™t assignment bá»‹ xá»­ lÃ½ Ä‘á»“ng thá»i.
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
     * TÃ¬m cÃ¡c assignment Pending Ä‘Ã£ quÃ¡ thá»i gian pháº£n há»“i.
     *
     * Scheduler dÃ¹ng method nÃ y Ä‘á»ƒ tá»± Ä‘á»™ng chuyá»ƒn Pending -> Missed.
     * PESSIMISTIC_WRITE giÃºp giáº£m race condition vá»›i accept/reject/miss.
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
     * Sau khi má»™t thá»£ accept, cÃ¡c assignment pending cÃ²n láº¡i cá»§a cÃ¹ng booking
     * khÃ´ng cÃ²n há»£p lá»‡ ná»¯a.
     *
     * Giai Ä‘oáº¡n MVP: mark lÃ  Missed.
     * Náº¿u muá»‘n chÃ­nh xÃ¡c hÆ¡n, sau nÃ y cÃ³ thá»ƒ thÃªm status Cancelled/Expired.
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
``n

### 11. src\main\java\com\fixit\domain\booking\service\dto\matching\WorkerMatchingCandidate.java

`java
package com.fixit.domain.booking.service.dto.matching;

import com.fixit.domain.worker.repository.projection.WorkerCandidateProjection;
import lombok.Builder;
import lombok.Data;

/**
 * Lá»›p Ä‘áº¡i diá»‡n cho má»™t á»¨ng cá»­ viÃªn (Thá»£) Ä‘ang Ä‘Æ°á»£c xem xÃ©t cho má»™t ÄÆ¡n hÃ ng.
 * Lá»›p nÃ y káº¿t há»£p dá»¯ liá»‡u gá»‘c tá»« DB vÃ  cÃ¡c chá»‰ sá»‘ vá»«a Ä‘Æ°á»£c tÃ­nh toÃ¡n (Khoáº£ng cÃ¡ch, Thá»i gian, Cost).
 */
@Data
@Builder
public class WorkerMatchingCandidate {
    
    // 1. ThÃ´ng tin gá»‘c cá»§a Thá»£ láº¥y tá»« Database (Táº§ng 3)
    private WorkerCandidateProjection workerInfo;
    
    // 2. Khoáº£ng cÃ¡ch Ä‘á»‹a lÃ½ thá»±c táº¿ (Km)
    private double distanceKm;
    
    // 3. Thá»i gian Ä‘i Ä‘Æ°á»ng dá»± kiáº¿n (PhÃºt)
    private double durationMins;
    
    // 4. ÄIá»‚M CHI PHÃ (COST) CUá»I CÃ™NG 
    // ÄÃ¢y lÃ  con sá»‘ quan trá»ng nháº¥t sáº½ Ä‘Æ°á»£c Ä‘Æ°a vÃ o Thuáº­t toÃ¡n Hungarian.
    // Cost cÃ ng tháº¥p = Thá»£ cÃ ng phÃ¹ há»£p.
    private double matchingCost; 
    
    // 5. Cá» Ä‘Ã¡nh dáº¥u xem thÃ´ng sá»‘ nÃ y láº¥y tá»« Google Maps hay cÃ´ng thá»©c Haversine dá»± phÃ²ng
    private boolean isGoogleMapsUsed;
}

``n

### 12. src\main\java\com\fixit\domain\booking\service\dto\matching\BookingMatchingContext.java

`java
package com.fixit.domain.booking.service.dto.matching;

import com.fixit.domain.booking.repository.projection.PendingBookingProjection;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Ngá»¯ cáº£nh ghÃ©p cáº·p (Matching Context) cho má»™t ÄÆ¡n hÃ ng cá»¥ thá»ƒ.
 * NÃ³ gom ÄÆ¡n hÃ ng vÃ  Danh sÃ¡ch Thá»£ á»©ng viÃªn láº¡i thÃ nh má»™t gÃ³i dá»¯ liá»‡u hoÃ n chá»‰nh.
 */
@Data
@Builder
public class BookingMatchingContext {
    
    // 1. ThÃ´ng tin gá»‘c cá»§a ÄÆ¡n hÃ ng (Láº¥y tá»« Database)
    private PendingBookingProjection booking;
    
    // 2. Danh sÃ¡ch cÃ¡c Thá»£ (Tá»‘i Ä‘a 5 ngÆ°á»i) Ä‘Ã£ Ä‘Æ°á»£c tÃ­nh toÃ¡n Cost cho riÃªng Ä‘Æ¡n hÃ ng nÃ y
    private List<WorkerMatchingCandidate> candidates;
}

``n

### 13. src\main\java\com\fixit\domain\booking\service\dto\matching\MatchingResult.java

`java
package com.fixit.domain.booking.service.dto.matching;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Lá»›p Ä‘áº¡i diá»‡n cho káº¿t quáº£ Cá»T LÃ•I cuá»‘i cÃ¹ng sau khi cá»— mÃ¡y Hungarian tÃ­nh toÃ¡n xong.
 * NÃ³ mang Ã½ nghÄ©a: ÄÆ¡n hÃ ng nÃ y (BookingId) sáº½ thuá»™c vá» Anh Thá»£ nÃ y (WorkerId).
 */
@Data
@AllArgsConstructor
public class MatchingResult {
    
    private UUID bookingId;
    private UUID workerId;
    
    /**
     * Kiá»ƒm tra xem Ä‘Æ¡n hÃ ng nÃ y cÃ³ ghÃ©p thÃ nh cÃ´ng hay khÃ´ng.
     * (Trong trÆ°á»ng há»£p Ä‘Ãªm khuya, khÃ´ng cÃ³ anh thá»£ nÃ o ráº£nh, workerId sáº½ lÃ  null)
     */
    public boolean isMatched() {
        return workerId != null;
    }
}

``n

### 14. src\main\java\com\fixit\global\util\HaversineUtil.java

`java
package com.fixit.global.util;

/**
 * CÃ´ng cá»¥ ToÃ¡n há»c tÃ­nh khoáº£ng cÃ¡ch bá» máº·t quáº£ Ä‘á»‹a cáº§u (Haversine Formula).
 * File nÃ y Ä‘Ã³ng vai trÃ² lÃ  PHÆ¯Æ NG ÃN Dá»° PHÃ’NG (Fallback) cho Google Maps.
 */
public class HaversineUtil {
    
    // BÃ¡n kÃ­nh trung bÃ¬nh cá»§a TrÃ¡i Äáº¥t (Km)
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * TÃ­nh khoáº£ng cÃ¡ch Ä‘Æ°á»ng chim bay (Km) giá»¯a 2 tá»a Ä‘á»™ (VÄ© Ä‘á»™, Kinh Ä‘á»™).
     */
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double originLat = Math.toRadians(lat1);
        double destinationLat = Math.toRadians(lat2);

        // CÃ´ng thá»©c lÆ°á»£ng giÃ¡c phá»©c táº¡p cá»§a Haversine
        double a = Math.pow(Math.sin(dLat / 2), 2) + 
                   Math.pow(Math.sin(dLon / 2), 2) * Math.cos(originLat) * Math.cos(destinationLat);
        double c = 2 * Math.asin(Math.sqrt(a));
        
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Dá»° PHÃ’NG THá»œI GIAN ÄI ÄÆ¯á»œNG
     * Náº¿u máº¡ng Ä‘á»©t, Google khÃ´ng tráº£ lá»i. Ta giáº£ Ä‘á»‹nh tá»‘c Ä‘á»™ xe mÃ¡y trong 
     * thÃ nh phá»‘ trung bÃ¬nh lÃ  30km/h Ä‘á»ƒ Æ°á»›c lÆ°á»£ng thá»i gian Ä‘i Ä‘áº¿n nhÃ  khÃ¡ch.
     */
    public static double estimateDurationMins(double distanceKm) {
        double speedKmh = 30.0;
        return (distanceKm / speedKmh) * 60.0; // Äá»•i ra phÃºt
    }
}

``n

### 15. src\main\java\com\fixit\domain\booking\service\matching\CostMatrixBuilder.java

`java
package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.service.dto.matching.WorkerMatchingCandidate;
import com.fixit.global.config.WorkerMatchingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Cá»— mÃ¡y tÃ­nh toÃ¡n Äiá»ƒm Chi PhÃ­ (Cost).
 * Chá»‹u trÃ¡ch nhiá»‡m nhá»“i cÃ¡c chá»‰ sá»‘ vÃ o cÃ´ng thá»©c ToÃ¡n há»c Ä‘á»ƒ ra Ä‘Æ°á»£c 1 Ä‘iá»ƒm Cost duy nháº¥t.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CostMatrixBuilder {

    private final WorkerMatchingProperties properties;

    /**
     * HÃ m nÃ y tÃ­nh Äiá»ƒm Cost cho má»™t Thá»£ vÃ  cáº­p nháº­t trá»±c tiáº¿p vÃ o Ä‘á»‘i tÆ°á»£ng Ä‘Ã³.
     * Cost = (Trá»ng sá»‘ Thá»i gian * ETA) 
     *      - (Trá»ng sá»‘ Uy tÃ­n * Äiá»ƒm uy tÃ­n) 
     *      + (Trá»ng sá»‘ Pháº¡t * Sá»‘ láº§n huá»· Ä‘Æ¡n)
     */
    public void calculateAndSetCost(WorkerMatchingCandidate candidate) {
        
        double etaMins = candidate.getDurationMins();
        double reputation = candidate.getWorkerInfo().getReputationScore().doubleValue();
        int rejectionCount = candidate.getWorkerInfo().getRejectionCount();

        // 1. RÃ¡p cÃ´ng thá»©c tá»« cÃ¡c há»‡ sá»‘ Ä‘Ã£ khai bÃ¡o trong application-dev.yml
        double cost = (properties.getCostWeightEta() * etaMins)
                    - (properties.getCostWeightReputation() * reputation)
                    + (properties.getCostWeightCancelRate() * rejectionCount);

        // 2. Thuáº­t toÃ¡n Hungary yÃªu cáº§u ma tráº­n chi phÃ­ pháº£i lÃ  sá»‘ dÆ°Æ¡ng khÃ´ng Ã¢m (>= 0).
        // Náº¿u anh thá»£ quÃ¡ uy tÃ­n (Ä‘iá»ƒm cao vÃºt) lÃ m cho phÃ©p trá»« táº¡o ra sá»‘ Ã¢m, 
        // ta Ã©p nÃ³ vá» má»©c sÃ n lÃ  0.0 (chi phÃ­ lÃ½ tÆ°á»Ÿng nháº¥t, tÆ°Æ¡ng Ä‘Æ°Æ¡ng "Miá»…n phÃ­").
        cost = Math.max(0.0, cost);

        // 3. LÆ°u Ä‘iá»ƒm Cost vÃ o trong cÃ¡i tÃºi DTO
        candidate.setMatchingCost(cost);
        
        log.debug("Worker [{}] | ETA: {}m | Rep: {}* | Rej: {}x ==> FINAL COST: {}", 
                  candidate.getWorkerInfo().getWorkerId(), 
                  String.format("%.1f", etaMins), 
                  reputation, 
                  rejectionCount, 
                  String.format("%.2f", cost));
    }
}

``n

### 16. src\main\java\com\fixit\domain\booking\service\matching\algorithm\HungarianAlgorithmSolver.java

`java
package com.fixit.domain.booking.service.matching.algorithm;

import java.util.Arrays;

/**
 * TRÃ™M CUá»I: Thuáº­t toÃ¡n Hungarian (Kuhn-Munkres) O(N^3).
 * ÄÆ°á»£c dÃ¹ng Ä‘á»ƒ giáº£i quyáº¿t BÃ i toÃ¡n PhÃ¢n cÃ´ng (Assignment Problem).
 * 
 * Äáº§u vÃ o: Má»™t Ma tráº­n chi phÃ­ NxM (N Thá»£, M ÄÆ¡n hÃ ng).
 * Äáº§u ra: Má»™t sÆ¡ Ä‘á»“ ghÃ©p cáº·p sao cho Tá»”NG CHI PHÃ Cá»¦A TOÃ€N Bá»˜ Há»† THá»NG LÃ€ NHá»Ž NHáº¤T.
 */
public class HungarianAlgorithmSolver {

    private final double[][] costMatrix;
    private final int rows, cols, dim;
    private final double[] u, v;
    private final int[] p, way;

    /**
     * Khá»Ÿi táº¡o Thuáº­t toÃ¡n. Náº¿u sá»‘ lÆ°á»£ng Thá»£ vÃ  ÄÆ¡n hÃ ng khÃ´ng báº±ng nhau (ma tráº­n khÃ´ng vuÃ´ng),
     * Thuáº­t toÃ¡n sáº½ tá»± Ä‘á»™ng táº¡o thÃªm cÃ¡c Thá»£ "áº£o" hoáº·c ÄÆ¡n "áº£o" vá»›i chi phÃ­ = 0 Ä‘á»ƒ cÃ¢n báº±ng.
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
                this.costMatrix[i] = new double[dim]; // Padding (ThÃªm hÃ ng áº£o)
            }
        }
        
        u = new double[dim + 1];
        v = new double[dim + 1];
        p = new int[dim + 1];
        way = new int[dim + 1];
    }

    /**
     * Cháº¡y Thuáº­t toÃ¡n vÃ  tráº£ vá» máº£ng káº¿t quáº£.
     * result[i] = j cÃ³ nghÄ©a lÃ : HÃ ng i (Anh Thá»£ thá»© i) Ä‘Æ°á»£c phÃ¢n cÃ´ng cho Cá»™t j (ÄÆ¡n hÃ ng thá»© j).
     * Náº¿u káº¿t quáº£ tráº£ vá» -1 nghÄ©a lÃ  anh Thá»£ Ä‘Ã³ bá»‹ dÆ° ra (khÃ´ng cÃ³ Ä‘Æ¡n).
     */
    public int[] execute() {
        for (int i = 1; i <= dim; i++) {
            p[0] = i;
            int j0 = 0;
            double[] minv = new double[dim + 1];
            Arrays.fill(minv, Double.MAX_VALUE);
            boolean[] used = new boolean[dim + 1];
            
            // TÃ¬m Ä‘Æ°á»ng tÄƒng luá»“ng (Augmenting path)
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
                
                // Cáº­p nháº­t nhÃ£n (Potentials)
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
            
            // Cáº­p nháº­t láº¡i Ä‘Æ°á»ng Ä‘i
            do {
                int j1 = way[j0];
                p[j0] = p[j1];
                j0 = j1;
            } while (j0 != 0);
        }
        
        // TrÃ­ch xuáº¥t káº¿t quáº£ cuá»‘i cÃ¹ng tá»« máº£ng p[]
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

``n

### 17. src\main\java\com\fixit\domain\booking\service\matching\WorkerMatchingService.java

`java
package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.repository.projection.PendingBookingProjection;
import com.fixit.domain.booking.service.dto.matching.MatchingResult;

import java.util.List;

public interface WorkerMatchingService {
    /**
     * HÃ m chÃ­nh: Nháº­n vÃ o 1 danh sÃ¡ch ÄÆ¡n hÃ ng Ä‘ang chá», tráº£ vá» 1 danh sÃ¡ch Káº¿t quáº£ GhÃ©p cáº·p
     */
    List<MatchingResult> performBatchMatching(List<PendingBookingProjection> pendingBookings);
}

``n

### 18. src\main\java\com\fixit\domain\booking\service\matching\WorkerMatchingServiceImpl.java

`java
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
 * NHáº C TRÆ¯á»žNG (Orchestrator) cá»§a há»‡ thá»‘ng.
 * Káº¿t ná»‘i táº¥t cáº£ cÃ¡c Táº§ng (DB -> Google Maps -> TÃ­nh Cost -> Hungarian Algorithm).
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

        log.info("Báº¯t Ä‘áº§u cháº¡y Batch Matching cho {} Ä‘Æ¡n hÃ ng.", pendingBookings.size());

        List<BookingMatchingContext> contexts = new ArrayList<>();
        Set<UUID> allUniqueWorkerIds = new HashSet<>();
        List<WorkerCandidateProjection> allUniqueWorkers = new ArrayList<>();

        // BÆ¯á»šC 1: Láº¤Y á»¨NG VIÃŠN & ÄO KHOáº¢NG CÃCH (Gá»i DB + Google Maps)
        for (PendingBookingProjection booking : pendingBookings) {
            
            // 1.1 TÃ¬m 5 thá»£ gáº§n nháº¥t (Lá»c thÃ´ báº±ng SQL - Táº§ng 3)
            List<WorkerCandidateProjection> candidates = workerRepository.findCandidatesNearby(
                    booking.getServiceId(),
                    booking.getDestinationLat().doubleValue(),
                    booking.getDestinationLng().doubleValue(),
                    properties.getCandidateRadiusKm(),
                    properties.getMaxWorkersPerBooking()
            );

            List<WorkerMatchingCandidate> workerCandidates = new ArrayList<>();
            for (WorkerCandidateProjection worker : candidates) {
                // Gom táº¥t cáº£ Thá»£ vÃ o 1 máº£ng chung Ä‘á»ƒ lÃ¡t táº¡o Ma tráº­n
                if (allUniqueWorkerIds.add(worker.getWorkerId())) {
                    allUniqueWorkers.add(worker);
                }

                double distanceKm;
                double durationMins;
                boolean isGoogleMapsUsed = false;

                // 1.2 Gá»i Google Maps Ä‘o ETA (Táº§ng 2)
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
                    // CÆ  CHáº¾ FALLBACK (Dá»° PHÃ’NG CHá»NG Sáº¬P Há»† THá»NG)
                    distanceKm = HaversineUtil.calculateDistanceKm(
                            worker.getLatitude().doubleValue(), worker.getLongitude().doubleValue(),
                            booking.getDestinationLat().doubleValue(), booking.getDestinationLng().doubleValue()
                    );
                    durationMins = HaversineUtil.estimateDurationMins(distanceKm);
                }

                // 1.3 Táº¡o cÃ¡i tÃºi Ä‘á»±ng Thá»£
                WorkerMatchingCandidate candidateDTO = WorkerMatchingCandidate.builder()
                        .workerInfo(worker)
                        .distanceKm(distanceKm)
                        .durationMins(durationMins)
                        .isGoogleMapsUsed(isGoogleMapsUsed)
                        .build();

                // 1.4 Cháº¥m Ä‘iá»ƒm Cost (Táº§ng 5)
                costMatrixBuilder.calculateAndSetCost(candidateDTO);
                workerCandidates.add(candidateDTO);
            }

            // GÃ³i Ä‘Æ¡n hÃ ng vÃ  cÃ¡c anh thá»£ láº¡i
            contexts.add(BookingMatchingContext.builder()
                    .booking(booking)
                    .candidates(workerCandidates)
                    .build());
        }

        // BÆ¯á»šC 2: Táº O MA TRáº¬N NxM Äá»‚ CHUáº¨N Bá»Š GIáº¢I
        int N = allUniqueWorkers.size(); // N hÃ ng (Thá»£)
        int M = pendingBookings.size();  // M cá»™t (ÄÆ¡n hÃ ng)
        
        if (N == 0) {
            log.warn("KhÃ´ng tÃ¬m tháº¥y báº¥t ká»³ thá»£ nÃ o ráº£nh cho {} Ä‘Æ¡n hÃ ng.", M);
            return pendingBookings.stream()
                    .map(b -> new MatchingResult(b.getBookingId(), null))
                    .collect(Collectors.toList());
        }

        double[][] costMatrix = new double[N][M];
        double MAX_COST = 999999.0; // Äiá»ƒm cost vÃ´ cá»±c (DÃ nh cho thá»£ khÃ´ng biáº¿t lÃ m dá»‹ch vá»¥ Ä‘Ã³ hoáº·c á»Ÿ quÃ¡ xa)

        for (int i = 0; i < N; i++) {
            WorkerCandidateProjection worker = allUniqueWorkers.get(i);
            for (int j = 0; j < M; j++) {
                BookingMatchingContext context = contexts.get(j);
                
                // Lá»¥c trong cÃ¡i tÃºi xem anh thá»£ i cÃ³ á»©ng tuyá»ƒn vÃ o Ä‘Æ¡n j khÃ´ng?
                Optional<WorkerMatchingCandidate> candidateOpt = context.getCandidates().stream()
                        .filter(c -> c.getWorkerInfo().getWorkerId().equals(worker.getWorkerId()))
                        .findFirst();

                if (candidateOpt.isPresent()) {
                    costMatrix[i][j] = candidateOpt.get().getMatchingCost();
                } else {
                    costMatrix[i][j] = MAX_COST; // Pháº¡t Ä‘iá»ƒm vÃ´ cá»±c
                }
            }
        }

        // BÆ¯á»šC 3: GIáº¢I MÃƒ Báº°NG Cá»– MÃY HUNGARIAN
        HungarianAlgorithmSolver solver = new HungarianAlgorithmSolver(costMatrix);
        int[] assignment = solver.execute();

        // BÆ¯á»šC 4: RÃP Káº¾T QUáº¢ VÃ€ TRáº¢ Vá»€ CHO Há»† THá»NG Gá»¬I THÃ”NG BÃO
        List<MatchingResult> results = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            int assignedBookingIndex = assignment[i];
            if (assignedBookingIndex != -1) {
                // Thá»£ i Ä‘Æ°á»£c Cá»— mÃ¡y chia cho Ä‘Æ¡n j
                UUID workerId = allUniqueWorkers.get(i).getWorkerId();
                UUID bookingId = pendingBookings.get(assignedBookingIndex).getBookingId();
                
                // Loáº¡i trá»« nhá»¯ng cáº·p bá»‹ Ã©p duyÃªn (Cost = 999999)
                if (costMatrix[i][assignedBookingIndex] < MAX_COST) {
                    results.add(new MatchingResult(bookingId, workerId));
                }
            }
        }

        // Lá»c láº¡i nhá»¯ng Ä‘Æ¡n hÃ ng Ä‘en Ä‘á»§i bá»‹ bá» rÆ¡i (WorkerId = null)
        for (PendingBookingProjection booking : pendingBookings) {
            boolean matched = results.stream().anyMatch(r -> r.getBookingId().equals(booking.getBookingId()));
            if (!matched) {
                results.add(new MatchingResult(booking.getBookingId(), null)); 
            }
        }

        log.info("Batch Matching hoÃ n táº¥t. GhÃ©p thÃ nh cÃ´ng {}/{} Ä‘Æ¡n.", 
                results.stream().filter(MatchingResult::isMatched).count(), M);
        return results;
    }
}

``n

### 19. src\main\java\com\fixit\domain\booking\service\matching\WorkerMatchingScheduler.java

`java
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
 * Äá»’NG Há»’ BÃO THá»¨C (Scheduler) cá»§a há»‡ thá»‘ng.
 * Tá»± Ä‘á»™ng cháº¡y ngáº§m Ä‘á»‹nh ká»³ Ä‘á»ƒ tÃ¬m Ä‘Æ¡n má»“ cÃ´i vÃ  gá»i thuáº­t toÃ¡n.
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
     * Dáº¥u @Scheduled sáº½ lÃ m hÃ m nÃ y tá»± Ä‘á»™ng cháº¡y liÃªn tá»¥c.
     * fixedDelayString: Sau khi cháº¡y xong, nÃ³ sáº½ nghá»‰ 5 giÃ¢y rá»“i má»›i cháº¡y láº¡i 
     * (thá»i gian nghá»‰ 5000ms nÃ y Ä‘Æ°á»£c Ä‘á»c tá»« file application-dev.yml).
     */
    @Scheduled(fixedDelayString = "${app.matching.batch-interval-ms:5000}")
    @Transactional
    public void runBatchMatching() {
        
        // 1. QuÃ©t tÃ¬m cÃ¡c ÄÆ¡n hÃ ng Ä‘ang bá»‹ má»“ cÃ´i (Táº§ng 3)
        List<PendingBookingProjection> pendingBookings = bookingRepository.findUnassignedPendingBookings();

        if (pendingBookings.isEmpty()) {
            // KhÃ´ng cÃ³ Ä‘Æ¡n nÃ o thÃ¬ thoÃ¡t, Ä‘i ngá»§ tiáº¿p 5 giÃ¢y ná»¯a
            return; 
        }

        log.info("[SCHEDULER] ÄÃ£ quÃ©t tháº¥y {} Ä‘Æ¡n hÃ ng má»“ cÃ´i. ÄÃ¡nh thá»©c Nháº¡c trÆ°á»Ÿng...", pendingBookings.size());

        // 2. Giao viá»‡c cho Nháº¡c trÆ°á»Ÿng cháº¡y luá»“ng Thuáº­t toÃ¡n (Táº§ng 6)
        List<MatchingResult> results = matchingService.performBatchMatching(pendingBookings);

        // 3. Xá»­ lÃ½ káº¿t quáº£ Nháº¡c trÆ°á»Ÿng bÃ¡o cÃ¡o vá»
        List<BookingWorkerAssignment> newAssignments = new ArrayList<>();

        for (MatchingResult result : results) {
            if (result.isMatched()) {
                // TÃ¬nh huá»‘ng 1: GhÃ©p Ä‘Ã´i thÃ nh cÃ´ng!
                // Táº¡o má»™t báº£n ghi Assignment (PhÃ¢n cÃ´ng) lÆ°u vÃ o Database
                Booking bookingRef = new Booking();
                bookingRef.setId(result.getBookingId());
                
                Worker workerRef = new Worker();
                workerRef.setWorkerId(result.getWorkerId());

                BookingWorkerAssignment assignment = new BookingWorkerAssignment();
                assignment.setBooking(bookingRef);
                assignment.setWorker(workerRef);
                assignment.setStatus(com.fixit.domain.booking.entity.AssignmentStatus.Pending); // GÃ¡n tráº¡ng thÃ¡i chá» Thá»£ báº¥m Äá»“ng Ã½

                newAssignments.add(assignment);
                log.info(" [MATCHED] ÄÆ¡n {} ---> Thá»£ {}", result.getBookingId(), result.getWorkerId());
                
            } else {
                // TÃ¬nh huá»‘ng 2: ÄÆ¡n áº¿ (CÃ³ thá»ƒ do khuya quÃ¡ thá»£ táº¯t app Ä‘i ngá»§ háº¿t)
                // Ká»‡ nÃ³, 5 giÃ¢y sau chu ká»³ tiáº¿p theo nÃ³ sáº½ Ä‘Æ°á»£c quÃ©t láº¡i.
                log.warn(" [UNMATCHED] ÄÆ¡n {} khÃ´ng tÃ¬m tháº¥y anh thá»£ ráº£nh nÃ o.", result.getBookingId());
            }
        }

        // 4. LÆ°u táº¥t cáº£ káº¿t quáº£ ghÃ©p cáº·p thÃ nh cÃ´ng vÃ o Database (Batch Save)
        if (!newAssignments.isEmpty()) {
            assignmentRepository.saveAll(newAssignments);
            log.info("[SCHEDULER] ÄÃ£ chá»‘t sá»• vÃ  lÆ°u {} báº£n ghi phÃ¢n cÃ´ng vÃ o CSDL.", newAssignments.size());
            
            // 5. ÄÃ¡nh thá»©c Táº¦NG 8: Gá»i loa phÆ°á»ng thÃ´ng bÃ¡o cho thá»£ biáº¿t
            notificationService.sendNewAssignmentNotifications(newAssignments);
        }
    }
}

``n

### 20. src\main\java\com\fixit\domain\booking\service\matching\MatchingNotificationService.java

`java
package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.entity.BookingWorkerAssignment;

import java.util.List;

public interface MatchingNotificationService {
    /**
     * Báº¯n thÃ´ng bÃ¡o (FCM) Ä‘áº¿n Ä‘iá»‡n thoáº¡i cá»§a Thá»£ khi cÃ³ Ä‘Æ¡n hÃ ng má»›i Ä‘Æ°á»£c phÃ¢n cÃ´ng.
     */
    void sendNewAssignmentNotifications(List<BookingWorkerAssignment> newAssignments);
}

``n

### 21. src\main\java\com\fixit\domain\booking\service\matching\MatchingNotificationServiceImpl.java

`java
package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.entity.BookingWorkerAssignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Táº¦NG 8: Dá»‹ch vá»¥ ThÃ´ng bÃ¡o (Notification).
 * Nhiá»‡m vá»¥ duy nháº¥t: Gá»i Firebase Cloud Messaging (FCM) Ä‘á»ƒ Ä‘Ã¡nh thá»©c app trÃªn Ä‘iá»‡n thoáº¡i cá»§a Thá»£.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingNotificationServiceImpl implements MatchingNotificationService {

    // BÆ°á»›c tiáº¿p theo cá»§a dá»± Ã¡n: Inject FirebaseMessaging vÃ o Ä‘Ã¢y
    // private final FirebaseMessaging firebaseMessaging;

    @Override
    public void sendNewAssignmentNotifications(List<BookingWorkerAssignment> newAssignments) {
        if (newAssignments == null || newAssignments.isEmpty()) {
            return;
        }

        for (BookingWorkerAssignment assignment : newAssignments) {
            String workerId = assignment.getWorker().getWorkerId().toString();
            String bookingId = assignment.getBooking().getId().toString();

            // MÃ” PHá»ŽNG LOGIC Báº®N FIREBASE
            // (Khi báº£o vá»‡, báº¡n cÃ³ thá»ƒ nÃ³i vá»›i Há»™i Ä‘á»“ng: "Em Ä‘Ã£ thiáº¿t káº¿ sáºµn cá»•ng giao tiáº¿p (Interface).
            // Náº¿u cÃ³ module Firebase SDK, em chá»‰ cáº§n cáº¯m 3 dÃ²ng code vÃ o Ä‘Ã¢y lÃ  há»‡ thá»‘ng gá»­i tin nháº¯n tháº­t Ä‘Æ°á»£c ngay!")
            
            log.info("ðŸ”” [FCM_FIREBASE] Äang báº¯n Push Notification vá» mÃ¡y Ä‘iá»‡n thoáº¡i cá»§a Thá»£: [{}]", workerId);
            log.info("   Ná»™i dung tin nháº¯n: 'KhÃ¡ch hÃ ng vá»«a Ä‘áº·t má»™t Ä‘Æ¡n sá»­a chá»¯a má»›i (ID: {})! Nháº­n viá»‡c ngay!'", bookingId);
        }
    }
}

``n

### 22. src\main\java\com\fixit\domain\booking\controller\WorkerAssignmentController.java

`java
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
     * Láº¥y danh sÃ¡ch assignment Ä‘ang chá» thá»£ pháº£n há»“i.
     *
     * DÃ¹ng cho:
     * - dialog_incoming_order.xml
     * - WorkerHomeFragment kiá»ƒm tra cÃ³ Ä‘Æ¡n má»›i khÃ´ng
     *
     * Test táº¡m khi chÆ°a cÃ³ Auth:
     * Header: X-Debug-Worker-Id: <workerId>
     */
    @GetMapping("/workers/me/assignments/pending")
    public ApiResponse<PendingAssignmentResponse> getPendingAssignments() {
        PendingAssignmentResponse response = workerAssignmentService.getPendingAssignments();
        return ApiResponse.success(response);
    }

    /**
     * Thá»£ cháº¥p nháº­n Ä‘Æ¡n.
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
        return ApiResponse.success(response, "Nháº­n Ä‘Æ¡n thÃ nh cÃ´ng");
    }

    /**
     * Thá»£ tá»« chá»‘i Ä‘Æ¡n.
     *
     * Request body cÃ³ thá»ƒ null trong giai Ä‘oáº¡n MVP.
     * Náº¿u sau nÃ y muá»‘n lÆ°u lÃ½ do reject, cáº§n thÃªm cá»™t vÃ o báº£ng booking_worker_assignments.
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

        return ApiResponse.success(response, "Tá»« chá»‘i Ä‘Æ¡n thÃ nh cÃ´ng");
    }

    /**
     * Ghi nháº­n thá»£ bá» lá»¡ Ä‘Æ¡n.
     *
     * Giai Ä‘oáº¡n MVP:
     * - Android cÃ³ thá»ƒ gá»i API nÃ y khi countdown háº¿t 3 phÃºt.
     *
     * Sau nÃ y:
     * - Backend scheduler cÃ³ thá»ƒ tá»± xá»­ lÃ½ miss.
     */
    @PostMapping("/bookings/{bookingId}/assignments/{assignmentId}/miss")
    public ApiResponse<AssignmentActionResponse> miss(
            @PathVariable UUID bookingId,
            @PathVariable UUID assignmentId
    ) {
        AssignmentActionResponse response = workerAssignmentService.miss(bookingId, assignmentId);
        return ApiResponse.success(response, "Ghi nháº­n bá» lá»¡ Ä‘Æ¡n thÃ nh cÃ´ng");
    }
}
``n

