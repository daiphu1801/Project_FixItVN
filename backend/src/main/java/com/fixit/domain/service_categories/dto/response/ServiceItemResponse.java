package com.fixit.domain.service_categories.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object): ServiceItemResponse
 * =================================================
 * Đây là "khuôn" dữ liệu JSON trả về cho App Android khi lấy DỊCH VỤ CON + GIÁ TIỀN.
 *
 * JSON trả về sẽ có dạng:
 * {
 *   "id": 1,
 *   "itemName": "Thay bóng đèn",
 *   "suggestedPrice": 50000.00,
 *   "serviceCategoryId": 1
 * }
 *
 * SO SÁNH với ServiceCategoryResponse:
 *  - ServiceCategoryResponse: dữ liệu NHÓM cha (id + tên nhóm)
 *  - ServiceItemResponse:     dữ liệu DỊCH VỤ con (id + tên dịch vụ + giá + thuộc nhóm nào)
 *
 * TẠI SAO serviceCategoryId lại có mặt trong response?
 *  → App Android cần biết item này thuộc nhóm nào để hiển thị đúng màn hình.
 */
@Getter
@Builder
public class ServiceItemResponse {

    private Integer id;                 // ID dịch vụ (VD: 1)
    private String itemName;            // Tên dịch vụ (VD: "Thay bóng đèn")
    private BigDecimal suggestedPrice;  // Giá gợi ý (VD: 50000.00) — dùng BigDecimal cho chính xác
    private Integer serviceCategoryId;  // ID của nhóm cha (VD: 1 = "Sửa điện")
}
