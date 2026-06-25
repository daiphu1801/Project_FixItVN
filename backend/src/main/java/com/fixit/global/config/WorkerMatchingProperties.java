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
