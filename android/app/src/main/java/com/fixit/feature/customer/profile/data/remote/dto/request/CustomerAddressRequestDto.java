package com.fixit.feature.customer.profile.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

// CÚ PHÁP: public class [Tên_Class] {
// Ý NGHĨA: Hộp rỗng (Request DTO) để Android đóng gói dữ liệu của MỘT địa chỉ rồi ném LÊN Backend để lưu.
public class CustomerAddressRequestDto {

    // Nhãn địa chỉ (Nhà, Công ty)
    @SerializedName("label")
    private String label;

    // Tên đường chi tiết (123 Nguyễn Văn Cừ)
    @SerializedName("address")
    private String address;

    // Tọa độ GPS. Trên Android ta dùng kiểu Double (số thập phân) vì lát nữa Google Maps trên điện thoại cũng xài kiểu Double.
    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    // Cờ đánh dấu địa chỉ mặc định (true/false)
    @SerializedName("defaultAddress")
    private Boolean defaultAddress;

    // ----------------------------------------------------
    // HÀM KHỞI TẠO (Constructor)
    // Giúp Android gom 5 thông tin này bỏ vào hộp chỉ bằng 1 dòng code duy nhất.
    // ----------------------------------------------------
    public CustomerAddressRequestDto(String label, String address, Double latitude, Double longitude, Boolean defaultAddress) {
        this.label = label;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.defaultAddress = defaultAddress;
    }

    // ----------------------------------------------------
    // CÁC HÀM GETTER VÀ SETTER
    // ----------------------------------------------------
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
