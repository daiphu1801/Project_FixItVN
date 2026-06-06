package com.fixit.feature.customer.profile.domain.model;

// CÚ PHÁP: public class [Tên_Class]
// Ý NGHĨA: Căn phòng domain/model/ chứa Khuôn đúc chuẩn cho Địa chỉ (File 15).
// Dữ liệu trong này hoàn toàn sạch sẽ (Java thuần), là "Bức tường thành" bảo vệ Giao diện Android 
// khỏi những thay đổi của Backend và thư viện mạng. Màn hình giao diện chỉ xài class này để in dữ liệu ra.
public class CustomerAddress {

    // Không hề có @SerializedName hay annotation nào ở đây!
    private String id;
    private String label;
    private String address;
    private Double latitude;
    private Double longitude;
    private Boolean defaultAddress;

    // ----------------------------------------------------
    // HÀM KHỞI TẠO (Constructor)
    // Dùng để Nhà máy ép (RepositoryImpl) có thể ép dữ liệu từ DTO đổ vào khuôn này dễ dàng chỉ bằng 1 dòng code.
    // ----------------------------------------------------
    public CustomerAddress(String id, String label, String address, Double latitude, Double longitude, Boolean defaultAddress) {
        this.id = id;
        this.label = label;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.defaultAddress = defaultAddress;
    }

    // ----------------------------------------------------
    // GETTER VÀ SETTER
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
