package com.fixit.feature.auth.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class AuthRequest {
    public static class Login {
        @SerializedName("identifier")
        private String identifier;

        @SerializedName("password")
        private String password;

        public Login(String identifier, String password) {
            this.identifier = identifier;
            this.password = password;
        }
    }

    public static class Register {
        @SerializedName("phone")
        private String phone;

        @SerializedName("email")
        private String email;

        @SerializedName("password")
        private String password;

        @SerializedName("fullName")
        private String fullName;

        @SerializedName("role")
        private String role;

        public Register(String phone, String email, String password, String fullName, String role) {
            this.phone = phone;
            this.email = email;
            this.password = password;
            this.fullName = fullName;
            this.role = role;
        }
    }
}
