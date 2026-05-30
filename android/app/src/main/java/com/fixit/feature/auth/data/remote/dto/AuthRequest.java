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
        @SerializedName("identifier")
        private String identifier;
        @SerializedName("password")
        private String password;
        @SerializedName("fullName")
        private String fullName;
        @SerializedName("role")
        private String role;

        public Register(String identifier, String password, String fullName, String role) {
            this.identifier = identifier;
            this.password = password;
            this.fullName = fullName;
            this.role = role;
        }
    }

    public static class GoogleLogin {
        @SerializedName("idToken")
        private String idToken;
        @SerializedName("role")
        private String role;

        public GoogleLogin(String idToken, String role) {
            this.idToken = idToken;
            this.role = role;
        }
    }

    public static class RefreshToken {
        @SerializedName("refreshToken")
        private String refreshToken;

        public RefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    public static class SendOtp {
        @SerializedName("identifier")
        private String identifier;
        @SerializedName("actionType")
        private String actionType;

        public SendOtp(String identifier, String actionType) {
            this.identifier = identifier;
            this.actionType = actionType;
        }
    }

    public static class VerifyOtp {
        @SerializedName("identifier")
        private String identifier;
        @SerializedName("otpCode")
        private String otpCode;
        @SerializedName("actionType")
        private String actionType;

        public VerifyOtp(String identifier, String otpCode, String actionType) {
            this.identifier = identifier;
            this.otpCode = otpCode;
            this.actionType = actionType;
        }
    }

    public static class ResetPassword {
        @SerializedName("identifier")
        private String identifier;
        @SerializedName("otpCode")
        private String otpCode;
        @SerializedName("newPassword")
        private String newPassword;

        public ResetPassword(String identifier, String otpCode, String newPassword) {
            this.identifier = identifier;
            this.otpCode = otpCode;
            this.newPassword = newPassword;
        }
    }

    public static class ChangePassword {
        @SerializedName("oldPassword")
        private String oldPassword;
        @SerializedName("newPassword")
        private String newPassword;

        public ChangePassword(String oldPassword, String newPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }
    }

    public static class UpdateCurrentUser {
        @SerializedName("fullName")
        private String fullName;
        @SerializedName("avatarUrl")
        private String avatarUrl;

        public UpdateCurrentUser(String fullName, String avatarUrl) {
            this.fullName = fullName;
            this.avatarUrl = avatarUrl;
        }
    }
}
