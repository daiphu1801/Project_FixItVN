package com.fixit.domain.service_categories.controller;

import com.fixit.domain.service_categories.dto.response.ServiceCategoryResponse;
import com.fixit.domain.service_categories.dto.response.ServiceItemResponse;
import com.fixit.domain.service_categories.service.ServiceCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Gắn biển hiệu báo cho Spring Boot biết đây là cổng giao tiếp qua mạng Internet
// Mọi dữ liệu trả về sẽ tự động được "dịch" sang ngôn ngữ JSON để App Android hiểu được.
@RestController
// Đặt địa chỉ gốc cho toàn bộ các API trong nhà máy này
@RequestMapping("/api/service-categories")
@RequiredArgsConstructor
public class CustomerSearchFragment {

    // --- KHỐI 1: CHUẨN BỊ LỰC LƯỢNG (TRUYỀN INTERFACE) ---
    // Cô Lễ tân cần cầm bộ đàm để kết nối với Quản lý xưởng (Tầng Service)
    // [TRUYỀN INTERFACE VÀO]: Bản hợp đồng quy định 3 nhiệm vụ xử lý logic
    // (👉 Truyền vào từ file Interface: I_ServiceCategoryService.java)
    private final ServiceCategoryService categoryService;

    // --- KHỐI 2: API LẤY TRANG CHỦ ---
    // Chức năng: Lấy TẤT CẢ các nhóm dịch vụ cha (VD: Sửa điện, Sửa nước...)
    // App Android sẽ gọi đường link: GET
    // http://localhost:8080/api/service-categories
    @GetMapping
    public ResponseEntity<List<ServiceCategoryResponse>> getAllCategories() {
        // 1. Lễ tân gọi bộ đàm bảo quản lý xưởng (Service) lấy toàn bộ hàng ra
        List<ServiceCategoryResponse> result = categoryService.getAllCategories();

        // 2. Đóng dấu tem "Trạng thái 200 - Thành công" (ResponseEntity.ok)
        // và ném gói hàng JSON về cho App Android.
        return ResponseEntity.ok(result);
    }

    // --- KHỐI 3: API LẤY CHI TIẾT 1 NHÓM ---
    // Chức năng: Lấy thông tin của ĐÚNG 1 nhóm dịch vụ dựa vào ID.
    // App Android sẽ gọi đường link (Ví dụ nhóm số 1): GET
    // http://localhost:8080/api/service-categories/1
    @GetMapping("/{id}")
    public ResponseEntity<ServiceCategoryResponse> getCategoryById(@PathVariable Integer id) {
        // @PathVariable giúp "bóc" con số 1 từ trên đường link URL xuống thành biến
        // 'id'
        ServiceCategoryResponse result = categoryService.getCategoryById(id);

        return ResponseEntity.ok(result);
    }

    // --- KHỐI 4: API LẤY DỊCH VỤ CON (ĐỂ XEM GIÁ) ---
    // Chức năng: Lấy danh sách các dịch vụ chi tiết (kèm giá tiền) thuộc về 1 nhóm
    // cha.
    // App Android sẽ gọi đường link: GET
    // http://localhost:8080/api/service-categories/1/items
    @GetMapping("/{categoryId}/items")
    public ResponseEntity<List<ServiceItemResponse>> getItemsByCategoryId(@PathVariable Integer categoryId) {
        // App Android truyền mã nhóm lên (VD: 1), Lễ tân chuyển số 1 đó cho quản lý
        // xưởng tìm các dịch vụ con
        List<ServiceItemResponse> result = categoryService.getItemsByCategoryId(categoryId);

        // Trả danh sách (VD: "Thay bóng đèn 50k", "Sửa chập điện 200k") về cho điện
        // thoại hiển thị
        return ResponseEntity.ok(result);
    }
}