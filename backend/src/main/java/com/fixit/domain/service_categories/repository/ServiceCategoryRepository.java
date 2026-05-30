package com.fixit.domain.service_categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fixit.domain.service_categories.entity.ServiceCategory;

/**
 * REPOSITORY: ServiceCategoryRepository
 * =====================================
 * Đây là nơi trực tiếp GIAO TIẾP VỚI DATABASE (phát lệnh SQL).
 * Nhiệm vụ chính: Thực hiện các thao tác CRUD (Create, Read, Update, Delete) 
 * trên bảng "service_categories".
 *
 * TẠI SAO LẠI LÀ INTERFACE MÀ KHÔNG CÓ CODE BÊN TRONG?
 * → Vì Spring Data JPA quá thông minh! 
 * Chỉ cần extends (kế thừa) JpaRepository, Spring sẽ TỰ ĐỘNG sinh ra 
 * tất cả các câu lệnh SQL cơ bản cho bạn. Bạn không cần tự viết code!
 *
 * Các hàm đã được cho sẵn (miễn phí) nhờ JpaRepository:
 * - findAll()       -> SELECT * FROM service_categories;
 * - findById(id)    -> SELECT * FROM service_categories WHERE id = ?;
 * - save(entity)    -> INSERT INTO... hoặc UPDATE...
 * - deleteById(id)  -> DELETE FROM service_categories WHERE id = ?;
 *
 * Tham số của JpaRepository<ServiceCategory, Integer>:
 * - ServiceCategory: Entity (bảng) mà Repository này quản lý.
 * - Integer: Kiểu dữ liệu của khóa chính (@Id) trong Entity đó.
 */
@Repository // Báo cho Spring biết đây là một "Thủ kho" quản lý kho dữ liệu
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Integer> {
    
    // Hiện tại để trống, vì JpaRepository đã cung cấp đủ các hàm cơ bản rồi.
    // Nếu sau này cần hàm đặc biệt (VD: tìm theo tên), ta sẽ viết thêm vào đây.

}
