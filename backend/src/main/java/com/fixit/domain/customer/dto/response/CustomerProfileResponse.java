package com.fixit.domain.customer.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

// CÚ PHÁP: @[Tên_Annotation]
// Ý NGHĨA: Annotation của Lombok. Tự động sinh code Getter/Setter cho class này.
@Data

// CÚ PHÁP: @[Tên_Annotation]
// Ý NGHĨA: Tạo công cụ Builder giúp Backend nhét dữ liệu vào hộp DTO này dễ dàng hơn.
// Ví dụ: CustomerProfileResponse.builder().id(...).fullName(...).build();
@Builder

// CÚ PHÁP: [Phạm vi truy cập] class [Tên_Class] {
// Ý NGHĨA: Khai báo class DTO làm khuôn mẫu trả dữ liệu Profile về cho Android.
public class CustomerProfileResponse {

    // CÚ PHÁP: [Phạm vi truy cập] [Kiểu dữ liệu] [Tên_biến];
    // Ý NGHĨA: Biến chứa ID của khách hàng. Cần trả về ID để Android biết đang xử lý dữ liệu của ai.
    private UUID id;

    // Ý NGHĨA: Biến chứa họ tên của khách hàng để Android hiển thị lên màn hình Profile.
    private String fullName;

// CÚ PHÁP: }
// Ý NGHĨA: Đóng class.
}
