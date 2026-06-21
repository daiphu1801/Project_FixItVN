package com.fixit.domain.service_categories.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * ENTITY: ServiceItem
 * ====================
 * Ánh xạ (map) sang bảng "service_items" trong database.
 * Đây là DỊCH VỤ CON CHI TIẾT — mức cụ thể, có kèm giá tiền gợi ý.
 *
 * Ví dụ dữ liệu trong DB:
 *  id=1, item_name="Thay bóng đèn",   suggested_price=50000,  service_category_id=1
 *  id=2, item_name="Sửa chập điện",   suggested_price=200000, service_category_id=1
 *  id=3, item_name="Thông cống",      suggested_price=150000, service_category_id=2
 *
 * QUAN HỆ: NHIỀU ServiceItem thuộc về 1 ServiceCategory (@ManyToOne)
 *   → ServiceItem là bảng CON (có khóa ngoại service_category_id)
 *   → ServiceCategory là bảng CHA
 */
@Entity                         // Đánh dấu đây là class ánh xạ với bảng DB
@Table(name = "service_items")  // Tên bảng tương ứng trong DB
@Getter
@Setter
@Builder
@NoArgsConstructor              // JPA bắt buộc cần constructor không tham số
@AllArgsConstructor
public class ServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB tự tăng id
    private Integer id;

    /**
     * Quan hệ NHIỀU-1 với ServiceCategory:
     *  - Nhiều ServiceItem thuộc về 1 ServiceCategory
     *  - FetchType.LAZY: Chỉ load ServiceCategory khi cần (tối ưu hiệu năng)
     *  - @JoinColumn: Tên cột khóa ngoại trong bảng service_items
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_category_id")   // Cột FK trong bảng service_items
    private ServiceCategory serviceCategory;

    @Column(name = "item_name", nullable = false)           // Tên dịch vụ, không được null
    private String itemName;

    @Column(name = "suggested_price", precision = 12, scale = 2)  // Giá tiền gợi ý
    private BigDecimal suggestedPrice;  // Dùng BigDecimal vì liên quan đến tiền (chính xác tuyệt đối)
}
