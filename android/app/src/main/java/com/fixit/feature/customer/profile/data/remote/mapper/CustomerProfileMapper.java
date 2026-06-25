package com.fixit.feature.customer.profile.data.remote.mapper;

import com.fixit.feature.customer.profile.data.remote.dto.response.CustomerAddressResponseDto;
import com.fixit.feature.customer.profile.data.remote.dto.response.CustomerProfileResponseDto;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import com.fixit.feature.customer.profile.domain.model.CustomerProfile;

import java.util.ArrayList;
import java.util.List;

// CÚ PHÁP: public class [Tên_Class]
// Ý NGHĨA: Đây chính là CÁI MÁY ÉP (Mapper). Nó không chứa dữ liệu, nó chỉ làm duy nhất một hành động: 
// Nhận vào cái Thùng xốp (DTO) và ép ra cái Khuôn chuẩn (Domain Model).
public class CustomerProfileMapper {

    // ----------------------------------------------------
    // MÁY ÉP 1: Ép Hồ sơ (Profile)
    // ----------------------------------------------------
    public static CustomerProfile toDomain(CustomerProfileResponseDto dto) {
        if (dto == null) return null;
        
        // Rút dữ liệu từ DTO ra, đổ vào Hàm khởi tạo của Domain Model (File 14)
        return new CustomerProfile(
                dto.getId(), 
                dto.getFullName()
        );
    }

    // ----------------------------------------------------
    // MÁY ÉP 2: Ép Một Địa chỉ (Address)
    // ----------------------------------------------------
    public static CustomerAddress toDomain(CustomerAddressResponseDto dto) {
        if (dto == null) return null;
        
        // Rút 6 cái dữ liệu từ DTO ra, đổ vào File 15
        return new CustomerAddress(
                dto.getId(),
                dto.getLabel(),
                dto.getAddress(),
                dto.getLatitude(),
                dto.getLongitude(),
                dto.getDefaultAddress()
        );
    }

    // ----------------------------------------------------
    // MÁY ÉP 3: Ép Toàn bộ Danh sách Địa chỉ (List Address)
    // ----------------------------------------------------
    public static List<CustomerAddress> toAddressDomainList(List<CustomerAddressResponseDto> dtoList) {
        if (dtoList == null) return new ArrayList<>();
        
        List<CustomerAddress> result = new ArrayList<>();
        // Vòng lặp: Lấy từng cái DTO trong danh sách ném vào Máy ép 2 ở trên
        for (CustomerAddressResponseDto dto : dtoList) {
            result.add(toDomain(dto));
        }
        return result;
    }
}
