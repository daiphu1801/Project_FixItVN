package com.fixit.domain.service_categories.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fixit.domain.service_categories.dto.response.ServiceCategoryResponse;
import com.fixit.domain.service_categories.dto.response.ServiceItemResponse;
import com.fixit.domain.service_categories.entity.ServiceCategory;
import com.fixit.domain.service_categories.entity.ServiceItem;
import com.fixit.domain.service_categories.repository.ServiceCategoryRepository;
import com.fixit.domain.service_categories.repository.ServiceItemRepository;
import com.fixit.domain.service_categories.service.ServiceCategoryService;

import lombok.RequiredArgsConstructor;

/**
 * SERVICE IMPLEMENTATION: ServiceCategoryServiceImpl
 * ===================================================
 * Đây là "Bộ não" xử lý logic của module này. Nó đóng vai trò là "Người thực thi" 
 * bản hợp đồng ServiceCategoryService.
 * 
 * Quy trình chuẩn của một hàm trong Service:
 * 1. Gọi Thủ kho (Repository) để lấy dữ liệu thô (Entity) từ DB lên.
 * 2. Xử lý logic (Kiểm tra lỗi, tính toán...).
 * 3. Chuyển đổi (Map) dữ liệu thô (Entity) sang gói hàng đẹp đẽ (DTO).
 * 4. Trả DTO về cho Controller.
 */
@Service // Báo cho Spring biết đây là một hạt nhân xử lý logic (Service)
@RequiredArgsConstructor // Lombok: Tự động tạo Constructor để "Tiêm" (Inject) các Repository vào
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    // --- TIÊM (INJECT) THỦ KHO VÀO ---
    // Nhờ @RequiredArgsConstructor, Spring sẽ tự động tìm 2 ông Thủ kho này 
    // và nhét vào class này để Service có thể gọi DB.
    private final ServiceCategoryRepository categoryRepository;
    private final ServiceItemRepository itemRepository;

    // ========================================================
    // HÀM 1: Lấy tất cả nhóm dịch vụ
    // ========================================================
    @Override
    public List<ServiceCategoryResponse> getAllCategories() {
        // Bước 1: Bảo Thủ kho lấy TẤT CẢ các Entity (Bảng cha) từ DB lên
        List<ServiceCategory> categories = categoryRepository.findAll();
        
        // Bước 2 & 3: Xử lý và Convert (Dùng cú pháp Stream API của Java 8+)
        return categories.stream() // Mở dòng chảy dữ liệu
                // Map (Chuyển đổi): Biến từng cái Entity -> Thành từng cái DTO
                .map(category -> ServiceCategoryResponse.builder()
                        .id(category.getId())                     // Rút ID từ Entity nhét vào DTO
                        .serviceName(category.getServiceName())   // Rút Name từ Entity nhét vào DTO
                        .iconUrl(category.getIconUrl())           // Rút Icon URL
                        .build())
                // Thu thập lại thành một List mới (List các DTO)
                .collect(Collectors.toList());
    }

    // ========================================================
    // HÀM 2: Lấy 1 nhóm dịch vụ theo ID
    // ========================================================
    @Override
    public ServiceCategoryResponse getCategoryById(Integer id) {
        // Bước 1 & 2: Bảo Thủ kho tìm theo ID. Nếu không thấy thì ném LỖI (Exception)
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục dịch vụ với ID: " + id));

        // Bước 3: Đóng gói Entity tìm được thành DTO
        return ServiceCategoryResponse.builder()
                .id(category.getId())
                .serviceName(category.getServiceName())
                .iconUrl(category.getIconUrl())
                .build();
    }

    // ========================================================
    // HÀM 3: Lấy các dịch vụ con theo ID nhóm cha
    // ========================================================
    @Override
    public List<ServiceItemResponse> getItemsByCategoryId(Integer categoryId) {
        // Bước 1: Gọi hàm "Phép thuật" ngầm sinh SQL mà ta đã tìm hiểu ở File 6
        // Lấy ra tất cả Dịch vụ con (Entity) có mã nhóm cha = categoryId
        List<ServiceItem> items = itemRepository.findByServiceCategoryId(categoryId);

        // Bước 2 & 3: Dùng Stream để biến List<Entity> thành List<DTO>
        return items.stream()
                .map(item -> ServiceItemResponse.builder()
                        .id(item.getId())
                        .serviceCategoryId(item.getServiceCategory().getId()) // Phải .getServiceCategory() trước rồi mới .getId()
                        .itemName(item.getItemName())
                        .suggestedPrice(item.getSuggestedPrice())
                        .build())
                .collect(Collectors.toList());
    }
}
