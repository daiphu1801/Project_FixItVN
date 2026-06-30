package com.fixit.domain.notification.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class DeviceOsConverter implements AttributeConverter<DeviceOs, String> {

    @Override
    public String convertToDatabaseColumn(DeviceOs attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public DeviceOs convertToEntityAttribute(String dbData) {
        return dbData == null ? null : DeviceOs.fromDbValue(dbData);
    }
}
