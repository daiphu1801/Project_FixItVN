package com.fixit.feature.auth.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class AuthRequest {
    public static class Login {
        @SerializedName("phone")
        private String phone;
        @SerializedName("password")
        private String password;

        public Login(String phone, String password) {
            this.phone = phone;
            this.password = password;
        }
    }

    public static class Register {
        @SerializedName("phone")
        private String phone;
        @SerializedName("password")
        private String password;
        @SerializedName("fullName")
        private String fullName;
        @SerializedName("role")
        private String role;

        public Register(String phone, String password, String fullName, String role) {
            this.phone = phone;
            this.password = password;
            this.fullName = fullName;
            this.role = role;
        }
    }
}
