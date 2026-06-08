package com.fixit.domain.auth.entity;

public enum DeviceOs {
    Android("Android"),
    iOS("iOS"),
    Web("Web");

    private final String dbValue;

    DeviceOs(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static DeviceOs fromDbValue(String dbValue) {
        for (DeviceOs deviceOs : values()) {
            if (deviceOs.dbValue.equals(dbValue)) {
                return deviceOs;
            }
        }
        throw new IllegalArgumentException("Unknown device OS: " + dbValue);
    }
}
