package com.fixit.feature.customer.profile.data.remote.dto.request;

import com.google.gson.annotations.SerializedName;

// CÚ PHÁP: public class [Tên_Class] {
// Ý NGHĨA: Khai báo cái hộp rỗng (DTO) để điện thoại nhét dữ liệu vào trước khi gửi lên Backend.
public class CustomerProfileRequestDto {

    // Ý NGHĨA: Báo cho GSON biết: "Khi dịch cái hộp này ra JSON gửi đi, hãy dán cái nhãn 'fullName' lên dữ liệu này nhé".
    // Nhờ cái nhãn này, "cô lễ tân" Backend mới nhận diện đúng để bỏ vào kho.
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("gender")
    private String gender;

    @SerializedName("dob")
    private String dob;

    // ----------------------------------------------------
    // CÁC HÀM KHỞI TẠO VÀ GETTER / SETTER
    // ----------------------------------------------------

    public CustomerProfileRequestDto(String fullName, String email, String gender, String dob) {
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
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
}
