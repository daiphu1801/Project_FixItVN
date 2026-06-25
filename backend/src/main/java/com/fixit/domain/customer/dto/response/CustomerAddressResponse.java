package com.fixit.domain.customer.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

// CÚ PHÁP: @[Tên_Annotation]
// Ý NGHĨA: Annotation của Lombok. Tự động sinh code Getter/Setter cho class này.
@Data

// CÚ PHÁP: @[Tên_Annotation]
// Ý NGHĨA: Dùng để Backend có thể dễ dàng khởi tạo: CustomerAddressResponse.builder().id(...).label(...).build()
@Builder

// CÚ PHÁP: [Phạm vi truy cập] class [Tên_Class] {
// Ý NGHĨA: Khai báo class DTO làm khuôn mẫu để trả về thông tin chi tiết của MỘT địa chỉ.
public class CustomerAddressResponse {

    // CÚ PHÁP: [Phạm vi truy cập] [Kiểu dữ liệu] [Tên_biến];
    // Ý NGHĨA: ID của địa chỉ này. Rất quan trọng! Android phải nhận và giữ ID này thì sau này mới có thể gửi lệnh Sửa/Xóa địa chỉ này được.
    private UUID id;

    // Ý NGHĨA: Nhãn địa chỉ để Android hiển thị to lên đầu (VD: "Nhà", "Công ty").
    private String label;

    // Ý NGHĨA: Chuỗi văn bản chi tiết về địa chỉ (VD: "123 Nguyễn Văn Cừ...").
    private String address;

    // Ý NGHĨA: Tọa độ vĩ độ GPS. Android sẽ dùng cái này để chấm một điểm (Marker) lên Google Map.
    private BigDecimal latitude;

    // Ý NGHĨA: Tọa độ kinh độ GPS.
    private BigDecimal longitude;

    // Ý NGHĨA: Cờ (true/false) báo hiệu cho Android biết để vẽ biểu tượng "Mặc định" lên giao diện UI.
    private Boolean defaultAddress;

// CÚ PHÁP: }
// Ý NGHĨA: Đóng class.
}
