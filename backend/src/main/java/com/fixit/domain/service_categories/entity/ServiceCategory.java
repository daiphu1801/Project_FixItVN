package com.fixit.domain.service_categories.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ENTITY: ServiceCategory
 * ========================
 * Ánh xạ (map) sang bảng "service_categories" trong database.
 * Đây là NHÓM DỊCH VỤ CHA — mức tổng quát nhất.
 *
 * Ví dụ dữ liệu trong DB:
 *  id=1, service_name="Sửa điện"
 *  id=2, service_name="Sửa nước"
 *  id=3, service_name="Sửa điều hòa"
 *
 * Quan hệ với ServiceItem: 1 ServiceCategory có NHIỀU ServiceItem (1-N)
 */
@Entity                             // Đánh dấu đây là class ánh xạ với bảng DB
@Table(name = "service_categories") // Tên bảng tương ứng trong DB
@Getter                             // Lombok: tự tạo tất cả getter (getId, getServiceName...)
@Setter                             // Lombok: tự tạo tất cả setter
@Builder                            // Lombok: cho phép dùng pattern Builder khi tạo object
@NoArgsConstructor                  // Lombok: tạo constructor không tham số (JPA bắt buộc cần)
@AllArgsConstructor                 // Lombok: tạo constructor đầy đủ tham số
public class ServiceCategory {

    @Id                                                     // Đánh dấu đây là khóa chính (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // DB tự tăng id (AUTO_INCREMENT)
    private Integer id;

    @Column(name = "service_name", nullable = false)        // Ánh xạ cột "service_name", không được null
    private String serviceName;

    @Column(name = "icon_url")
    private String iconUrl;
}
