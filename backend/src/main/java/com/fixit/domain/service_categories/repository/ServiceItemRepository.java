package com.fixit.domain.service_categories.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fixit.domain.service_categories.entity.ServiceItem;

/**
 * REPOSITORY: ServiceItemRepository
 * =====================================
 * Giống như file trước, đây cũng là "Ông Thủ Kho", nhưng chuyên quản lý 
 * bảng "service_items" (bảng chứa các dịch vụ con kèm giá tiền).
 * 
 * Nó cũng được thừa kế toàn bộ sức mạnh (các hàm có sẵn như findAll, save...) 
 * từ JpaRepository.
 */
@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Integer> {
    
    /**
     * PHÉP THUẬT NÂNG CAO: Query Method Naming (Đặt tên hàm sinh ra SQL)
     * =================================================================
     * Đây là một tính năng cực kỳ mạnh mẽ của Spring Data JPA.
     * Bạn KHÔNG CẦN viết lệnh SQL! Bạn chỉ cần đặt tên hàm theo đúng CÚ PHÁP CHUẨN, 
     * Spring sẽ "đọc" tên hàm và tự động dịch nó thành lệnh SQL.
     * 
     * Phân tích tên hàm: findByServiceCategoryId
     * - "findBy"            -> Dịch thành: SELECT * FROM service_items WHERE
     * - "ServiceCategoryId" -> Dịch thành: service_category_id = ?
     * 
     * => Tổng hợp lại, hàm này sẽ ngầm chạy câu lệnh SQL:
     * SELECT * FROM service_items WHERE service_category_id = [categoryId truyền vào];
     * 
     * Tác dụng: Trả về một danh sách (List) tất cả các dịch vụ con 
     * thuộc về một nhóm cha (dựa vào ID nhóm cha).
     * 
     * Ví dụ: Gọi findByServiceCategoryId(1) 
     * -> Lấy ra tất cả các dịch vụ con thuộc nhóm có ID = 1 (Nhóm Sửa điện).
     */
    List<ServiceItem> findByServiceCategoryId(Integer categoryId);
}
