package com.fixit.feature.auth.domain.model;

public enum UserRole {
    CUSTOMER,
    WORKER;

    public static UserRole from(String role) {
        if ("WORKER".equalsIgnoreCase(role)) {
            return WORKER;
        }
        return CUSTOMER;
    }
}
