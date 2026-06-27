package com.fixit.feature.customer.profile.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

// CÚ PHÁP: public class [Tên_Class] {
// Ý NGHĨA: Khai báo cái khuôn (DTO) bên Android để hứng dữ liệu JSON do Backend trả về.
public class CustomerProfileResponseDto {

    // CÚ PHÁP: @SerializedName("[Tên_key_trong_JSON]")
    // Ý NGHĨA: Báo cho bộ dịch GSON biết: "Hãy tìm cái chữ 'id' trong cục JSON mà Backend gửi về, rồi nhét giá trị của nó vào biến id ở dưới đây".
    @SerializedName("id")
    private String id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("gender")
    private String gender;

    @SerializedName("dob")
    private String dob;

    // ----------------------------------------------------
    // CÁC HÀM GETTER VÀ SETTER
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
// CÚ PHÁP: }
}
