package com.fixit.domain.service_categories.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO (Data Transfer Object): ServiceCategoryResponse
 * =====================================================
 * Đây là "khuôn" dữ liệu JSON trả về cho App Android khi lấy NHÓM DỊCH VỤ.
 *
 * TẠI SAO KHÔNG DÙNG THẲNG ENTITY ServiceCategory?
 *  - Entity ServiceCategory gắn với DB, có thể có các field nhạy cảm/không cần thiết.
 *  - DTO giúp kiểm soát chính xác App Android thấy gì.
 *  - Nếu đổi cấu trúc DB, DTO vẫn giữ nguyên → App không bị ảnh hưởng.
 *
 * JSON trả về sẽ có dạng:
 * {
 *   "id": 1,
 *   "serviceName": "Sửa điện"
 * }
 *
 * @Getter  → tự tạo getId(), getServiceName() — cần thiết khi Jackson serialize sang JSON
 * @Builder → cho phép tạo object theo kiểu:
 *            ServiceCategoryResponse.builder().id(1).serviceName("Sửa điện").build()
 */
@Getter
@Builder
public class ServiceCategoryResponse {

    private Integer id;         // ID nhóm dịch vụ (VD: 1)
    private String serviceName; // Tên nhóm dịch vụ (VD: "Sửa điện")
    private String iconUrl;     // Icon URL
}
