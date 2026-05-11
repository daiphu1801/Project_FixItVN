package com.fixit.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("accessToken")
    private String accessToken;
    @SerializedName("refreshToken")
    private String refreshToken;
    @SerializedName("user")
    private UserInfo user;

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

        public String getRole() { return role; }
        public String getFullName() { return fullName; }
    }
}
