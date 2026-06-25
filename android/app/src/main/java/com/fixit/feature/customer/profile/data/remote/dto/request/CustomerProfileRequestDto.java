package com.fixit.feature.customer.profile.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

// CÚ PHÁP: public class [Tên_Class] {
// Ý NGHĨA: Khai báo cái hộp rỗng (DTO) để điện thoại nhét dữ liệu vào trước khi gửi lên Backend.
public class CustomerProfileRequestDto {

    // Ý NGHĨA: Báo cho GSON biết: "Khi dịch cái hộp này ra JSON gửi đi, hãy dán cái nhãn 'fullName' lên dữ liệu này nhé".
    // Nhờ cái nhãn này, "cô lễ tân" Backend mới nhận diện đúng để bỏ vào kho.
    @SerializedName("fullName")
    private String fullName;

    // ----------------------------------------------------
    // CÁC HÀM KHỞI TẠO VÀ GETTER / SETTER
    // ----------------------------------------------------

    // CÚ PHÁP: public [Tên_Class]([Kiểu] [tham_số]) { ... }
    // Ý NGHĨA: Hàm tạo. Giúp ta tạo nhanh cái hộp và nhét luôn dữ liệu vào.
    // VD: new CustomerProfileRequestDto("Thế Anh");
    public CustomerProfileRequestDto(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
