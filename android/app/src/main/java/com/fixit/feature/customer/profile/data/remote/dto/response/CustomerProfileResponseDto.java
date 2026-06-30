package com.fixit.feature.customer.profile.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

// CÚ PHÁP: public class [Tên_Class] {
// Ý NGHĨA: Khai báo cái khuôn (DTO) bên Android để hứng dữ liệu JSON do Backend trả về.
public class CustomerProfileResponseDto {

    // CÚ PHÁP: @SerializedName("[Tên_key_trong_JSON]")
    // Ý NGHĨA: Báo cho bộ dịch GSON biết: "Hãy tìm cái chữ 'id' trong cục JSON mà Backend gửi về, rồi nhét giá trị của nó vào biến id ở dưới đây".
    @SerializedName("id")
    private String id;

    // Ý NGHĨA: Tìm chữ 'fullName' trong JSON nhét vào biến fullName.
    // LƯU Ý: Tên key ("fullName") BẮT BUỘC PHẢI KHỚP 100% với tên biến trong File 5 (DTO Backend).
    @SerializedName("fullName")
    private String fullName;

    // ----------------------------------------------------
    // CÁC HÀM GETTER VÀ SETTER
    // Bên Android, chúng ta ít xài thư viện Lombok (để app nhẹ hơn), nên ta phải gõ tay các hàm này.
    // ----------------------------------------------------

    // CÚ PHÁP: public [Kiểu_trả_về] get[Tên_Biến_Viết_Hoa]()
    public String getId() {
        return id;
    }

    // CÚ PHÁP: public void set[Tên_Biến_Viết_Hoa]([Kiểu] [Tên_Biến])
    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
// CÚ PHÁP: }
}
