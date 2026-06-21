package com.fixit.feature.customer.profile.domain.model;

// CÚ PHÁP: public class [Tên_Class]
// Ý NGHĨA: Khai báo Mô hình Dữ liệu (Domain Model). 
// Tầng Domain là tầng "Sang chảnh" và "Thuần khiết" nhất. Bạn sẽ KHÔNG bao giờ thấy 
// các annotation như @SerializedName hay @Column ở đây. Nó chỉ là Java thuần túy.
public class CustomerProfile {

    private String id;
    private String fullName;

    // ----------------------------------------------------
    // HÀM KHỞI TẠO
    // ----------------------------------------------------
    public CustomerProfile(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
