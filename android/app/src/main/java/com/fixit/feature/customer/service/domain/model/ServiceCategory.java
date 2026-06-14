package com.fixit.feature.customer.service.domain.model;

/**
 * DOMAIN MODEL - Phía Android
 * ===========================
 * Đây là "Bản vẽ chính thức" của một Nhóm Dịch Vụ sẽ được sử dụng trong toàn bộ phần UI (Giao diện) 
 * của App Android.
 * 
 * TẠI SAO LẠI PHẢI TẠO FILE NÀY TRONG KHI ĐÃ CÓ ServiceCategoryResponse (DTO)?
 * 1. Độc lập với API: 
 *    - DTO là để chơi với Backend. Lỡ Backend đổi tên biến "serviceName" thành "title", 
 *      ta chỉ cần sửa DTO, không cần sửa UI vì UI chỉ chơi với Model này.
 * 2. Linh hoạt:
 *    - Model này ta có thể tự do thêm thắt các biến phụ vụ riêng cho UI (ví dụ: biến boolean isSelected 
 *      để biết người dùng có đang chọn nó không, hay iconUrl, color...) mà không làm bẩn DTO của API.
 */
public class ServiceCategory {
    
    private Integer id;
    private String name; // Chú ý: Ở đây ta gọi ngắn gọn là 'name', không còn là 'serviceName' như Backend
    private String iconUrl;

    // Constructor (Hàm khởi tạo)
    public ServiceCategory(Integer id, String name, String iconUrl) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    // Constructor cũ cho tương thích
    public ServiceCategory(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // Các hàm Getter/Setter cơ bản
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
