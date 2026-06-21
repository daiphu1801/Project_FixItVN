package com.fixit.domain.service_categories.service;

import java.util.List;
import com.fixit.domain.service_categories.dto.response.ServiceCategoryResponse;
import com.fixit.domain.service_categories.dto.response.ServiceItemResponse;

/**
 * SERVICE INTERFACE: ServiceCategoryService
 * =========================================
 * Đây là "BẢN HỢP ĐỒNG" (Interface) quy định các tính năng (nghiệp vụ) 
 * mà hệ thống sẽ cung cấp cho người dùng (hoặc cho App Android).
 *
 * TẠI SAO CẦN FILE NÀY MÀ KHÔNG VIẾT LOGIC LUÔN?
 * - Trong lập trình chuyên nghiệp (Clean Architecture), người ta thường tách biệt:
 *   1. "Cái gì cần làm" (Interface - Bản hợp đồng)
 *   2. "Làm như thế nào" (Class Impl - Thợ thực thi hợp đồng)
 * 
 * - File này (Interface) chỉ đóng vai trò là "Bản cam kết", nó liệt kê ra 3 chức năng chính.
 *   Tầng Controller (người giao việc) chỉ cần nhìn vào bản hợp đồng này để biết
 *   có thể gọi những hàm nào, mà không cần quan tâm bên trong code chạy ra sao.
 */
public interface ServiceCategoryService {
    
    /**
     * Chức năng 1: Lấy danh sách TẤT CẢ các nhóm dịch vụ cha.
     * (Dùng cho màn hình trang chủ của App Android)
     * Trả về một List chứa các DTO (ServiceCategoryResponse).
     */
    List<ServiceCategoryResponse> getAllCategories();
    
    /**
     * Chức năng 2: Lấy thông tin chi tiết của MỘT nhóm dịch vụ dựa vào ID.
     */
    ServiceCategoryResponse getCategoryById(Integer id);
    
    /**
     * Chức năng 3: Lấy danh sách các dịch vụ con (kèm giá) của MỘT nhóm cha.
     * (Ví dụ truyền categoryId = 1 (Sửa điện) -> Trả về Thay bóng đèn, Kéo dây điện...)
     */
    List<ServiceItemResponse> getItemsByCategoryId(Integer categoryId);
    
}
