package com.fixit.feature.auth.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("accessToken")
    private String accessToken;
    @SerializedName("refreshToken")
    private String refreshToken;
    @SerializedName("user")
    private UserInfo user;

    public AuthResponse(String accessToken, String refreshToken, UserInfo user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public UserInfo getUser() { return user; }

    public static class UserInfo {
        @SerializedName("id")
        private String id;
        @SerializedName("phone")
        private String phone;
        @SerializedName("fullName")
        private String fullName;
        @SerializedName("role")
        private String role;

        public UserInfo(String id, String phone, String fullName, String role) {
            this.id = id;
            this.phone = phone;
            this.fullName = fullName;
            this.role = role;
        }

        public String getId() { return id; }
        public String getPhone() { return phone; }
        public String getRole() { return role; }
        public String getFullName() { return fullName; }
    }
}

