package com.fixit.feature.customer.profile.domain.model;

// CÚ PHÁP: public class [Tên_Class]
// Ý NGHĨA: Khai báo Mô hình Dữ liệu (Domain Model). 
// Tầng Domain là tầng "Sang chảnh" và "Thuần khiết" nhất. Bạn sẽ KHÔNG bao giờ thấy 
// các annotation như @SerializedName hay @Column ở đây. Nó chỉ là Java thuần túy.
public class CustomerProfile {

    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String dob;
    private String avatarUrl;

    // ----------------------------------------------------
    // HÀM KHỞI TẠO
    // ----------------------------------------------------
    public CustomerProfile(String id, String fullName, String email, String phoneNumber, String gender, String dob, String avatarUrl) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dob = dob;
        this.avatarUrl = avatarUrl;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
