package com.fixit.domain.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

// CÚ PHÁP: @[Tên_Annotation]
// Ý NGHĨA: Annotation của Lombok. Tự động sinh code Getter/Setter, Constructor, toString... cho class này.
@Data

// CÚ PHÁP: [Phạm vi truy cập] class [Tên_Class] {
// Ý NGHĨA: Khai báo class DTO nhận dữ liệu địa chỉ từ Android.
public class CustomerAddressRequest {

    // CÚ PHÁP: @[Tên_Annotation](<[Thuộc_tính] = [Giá_trị], [Thuộc_tính] =
    // "[Giá_trị]">)
    // Ý NGHĨA: Giới hạn độ dài nhãn địa chỉ. Không bắt buộc nhập (vì không có
    // @NotBlank), nhưng nếu nhập thì không được quá 50 ký tự.
    @Size(max = 50, message = "Nhãn địa chỉ tối đa 50 ký tự")

    // CÚ PHÁP: [Phạm vi truy cập] [Kiểu dữ liệu] [Tên_biến];
    // Ý NGHĨA: Biến lưu nhãn địa chỉ (VD: "Nhà", "Công ty").
    private String label;

    // CÚ PHÁP: @[Tên_Annotation](<[Thuộc_tính] = "[Giá_trị]">)
    // Ý NGHĨA: Bắt buộc chuỗi này không được rỗng. Khách hàng không được để trống
    // địa chỉ chi tiết.
    @NotBlank(message = "Địa chỉ không được để trống")

    // Ý NGHĨA: Biến lưu tên đường, phường, quận...
    private String address;

    // CÚ PHÁP: @[Tên_Annotation](<[Thuộc_tính] = "[Giá_trị]">)
    // Ý NGHĨA: @NotNull dùng cho kiểu số/đối tượng (khác với @NotBlank dùng cho
    // chuỗi). Bắt buộc App phải gửi lên tọa độ vĩ độ.
    @NotNull(message = "Thiếu tọa độ vĩ độ (latitude)")

    // CÚ PHÁP: [Phạm vi truy cập] [Kiểu dữ liệu] [Tên_biến];
    // Ý NGHĨA: Biến lưu vĩ độ GPS. Dùng BigDecimal để đảm bảo độ chính xác cao.
    private BigDecimal latitude;

    // Ý NGHĨA: Bắt buộc App phải gửi lên kinh độ.
    @NotNull(message = "Thiếu tọa độ kinh độ (longitude)")
    private BigDecimal longitude;

    // Ý NGHĨA: Cờ đánh dấu xem người dùng có tick chọn "Đặt làm địa chỉ mặc định"
    // hay không.
    // Kiểu Boolean (có chữ B viết hoa) có thể chứa 3 giá trị: true, false, hoặc
    // null.
    // Nếu Android không gửi trường này lên, biến này sẽ mang giá trị null (để
    // Backend tự quyết định xử lý).
    private Boolean defaultAddress;

    // CÚ PHÁP: }
    // Ý NGHĨA: Đóng class.
}
