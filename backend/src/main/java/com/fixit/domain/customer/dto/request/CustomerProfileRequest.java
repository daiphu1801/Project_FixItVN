// Khai báo package: Nằm trong thư mục chứa các object hứng dữ liệu gửi lên (request) của domain customer
package com.fixit.domain.customer.dto.request;

// Import thư viện kiểm duyệt: @NotBlank giúp chặn lỗi nếu người dùng không nhập gì hoặc chỉ nhập phím cách
import jakarta.validation.constraints.NotBlank;
// Import thư viện kiểm duyệt: @Size giúp giới hạn độ dài ký tự
import jakarta.validation.constraints.Size;

// Import Lombok: @Data là gói combo bao gồm tự động sinh Getter, Setter, toString, equals, và hashCode
import lombok.Data;

// Khai báo class DTO hứng dữ liệu cập nhật thông tin cá nhân
// Dữ liệu từ App Android (JSON) sẽ được Spring Boot tự động chuyển thành object của class này
@Data
public class CustomerProfileRequest {

    // Bắt buộc khách hàng phải nhập họ tên. Nếu rỗng, báo lỗi "Họ tên không được để trống" trả về cho Android.
    @NotBlank(message = "Họ tên không được để trống")
    
    // Ràng buộc độ dài: Tối đa 100 ký tự (cho khớp với độ dài cột trong Database)
    @Size(max = 100, message = "Họ tên quá dài, tối đa 100 ký tự")
    
    // Biến lưu họ tên khách hàng gửi lên
    private String fullName;

    // LƯU Ý: Hiện tại Database (Customer.java) chỉ mới có cột fullName. 
    // Màn hình Android của bạn đang có thêm Giới tính và Ngày sinh.
    // Tạm thời mình chỉ hứng fullName. Nếu muốn lưu cả giới tính, ngày sinh, 
    // chúng ta sẽ phải quay lại sửa bảng Customer trong Database sau nhé!
}
