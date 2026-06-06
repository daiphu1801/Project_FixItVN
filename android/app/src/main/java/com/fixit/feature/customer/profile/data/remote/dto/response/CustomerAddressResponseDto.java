package com.fixit.feature.customer.profile.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

// CÚ PHÁP: public class [Tên_Class] {
// Ý NGHĨA: Khai báo cái hộp rỗng (Response DTO) để Android HỨNG dữ liệu của một địa chỉ từ Backend trả về.
public class CustomerAddressResponseDto {

    // Ý NGHĨA: Báo cho GSON biết: "Khi nhận được cục JSON từ Backend, hãy nhặt cái chữ 'id' nhét vào biến id này".
    // Biến id rất quan trọng, Android phải giữ nó để lỡ người dùng bấm nút Xóa thì còn biết đường mà gửi id lên báo Backend xóa.
    @SerializedName("id")
    private String id;

    @SerializedName("label")
    private String label;

    @SerializedName("address")
    private String address;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    // Cờ đánh dấu xem địa chỉ này có phải mặc định không
    @SerializedName("defaultAddress")
    private Boolean defaultAddress;

    // ----------------------------------------------------
    // LƯU Ý KỸ THUẬT QUAN TRỌNG:
    // Tại sao ở file này (Response) mình lại KHÔNG VIẾT HÀM KHỞI TẠO (Constructor) như file Request?
    // Trả lời: Vì thư viện GSON bên trong Retrofit rất thông minh. Khi nó bắt được JSON từ mạng về, 
    // nó sẽ tự động tàng hình chui vào class này, chế tạo cái hộp và bơm dữ liệu vào luôn mà không cần 
    // hàm Khởi tạo. (Đỡ được một đống code cho chúng ta!).
    // ----------------------------------------------------

    // ----------------------------------------------------
    // CÁC HÀM GETTER VÀ SETTER
    // (Dùng để màn hình Android lôi dữ liệu trong hộp ra vẽ lên giao diện)
    // ----------------------------------------------------
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(Boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
