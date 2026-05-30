package com.fixit.feature.customer.service.data.remote.mapper;

import com.fixit.feature.customer.service.data.remote.dto.response.ServiceCategoryResponse;
import com.fixit.feature.customer.service.data.remote.dto.response.ServiceItemResponse;
import com.fixit.feature.customer.service.domain.model.ServiceCategory;
import com.fixit.feature.customer.service.domain.model.ServiceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * MAPPER (Người Vận Chuyển)
 * =========================
 * Đây là "nhà máy chế biến" dữ liệu. 
 * Như đã tìm hiểu ở 2 file trước, ta có:
 * - DTO: Thùng hàng thô vừa nhận từ mạng về.
 * - Domain Model: Hộp đựng tinh tế để dùng trong UI.
 * 
 * Chức năng của file này:
 * Bóc dữ liệu từ "Thùng hàng DTO", rồi nhét sang "Hộp đựng Model" một cách tương ứng.
 */
public class ServiceMapper {

    /**
     * HÀM 1: Biến 1 DTO thành 1 Model (Nhóm Dịch Vụ)
     */
    public static ServiceCategory toDomain(ServiceCategoryResponse response) {
        // Tránh lỗi khi Backend trả về dữ liệu rỗng (null)
        if (response == null) return null; 
        
        // Tạo Hộp Model mới và nhét dữ liệu từ DTO sang
        return new ServiceCategory(
                response.getId(),              // Lấy ID từ DTO
                response.getServiceName()      // Lấy serviceName từ DTO, nhét vào biến 'name' của Model
        );
    }

    /**
     * HÀM 2: Biến nguyên 1 Danh Sách DTO thành Danh Sách Model
     * (Dùng khi lấy danh sách hiển thị lên màn hình Trang chủ)
     */
    public static List<ServiceCategory> toCategoryDomainList(List<ServiceCategoryResponse> responses) {
        List<ServiceCategory> result = new ArrayList<>();
        if (responses != null) {
            // Lặp qua từng cái DTO trong danh sách
            for (ServiceCategoryResponse r : responses) {
                // Nhờ Hàm 1 biến thành Model rồi nhét vào danh sách kết quả mới
                result.add(toDomain(r));
            }
        }
        return result; // Trả về danh sách Model sạch sẽ cho UI dùng
    }

    // =================================================================================
    // Phần dưới này là các hàm tương tự, nhưng dành cho "Dịch vụ con" (ServiceItem)
    // =================================================================================

    public static ServiceItem toDomain(ServiceItemResponse response) {
        if (response == null) return null;
        return new ServiceItem(
                response.getId(),
                response.getItemName(),
                response.getSuggestedPrice(),
                response.getServiceCategoryId()
        );
    }

    public static List<ServiceItem> toItemDomainList(List<ServiceItemResponse> responses) {
        List<ServiceItem> result = new ArrayList<>();
        if (responses != null) {
            for (ServiceItemResponse r : responses) {
                result.add(toDomain(r));
            }
        }
        return result;
    }
}
