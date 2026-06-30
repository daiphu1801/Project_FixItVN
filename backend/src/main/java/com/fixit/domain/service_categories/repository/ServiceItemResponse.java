package com.fixit.domain.service_categories.repository;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter // Tự động sinh ra các phương thức get()
@Builder // Mẫu thiết kế Builder để khởi tạo đối tượng linh hoạt và an toàn
public class ServiceItemResponse {
    private Integer id; // ID của hạng mục dịch vụ
    private Integer categoryID; // ID của danh mục
    private String itemName; // Tên hạng mục dịch vụ
    private BigDecimal suggestPrice; // Giá dự kiến
}
