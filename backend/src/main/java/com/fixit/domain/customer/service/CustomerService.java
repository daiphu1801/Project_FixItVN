package com.fixit.domain.customer.service;

import com.fixit.domain.customer.dto.request.CustomerAddressRequest;
import com.fixit.domain.customer.dto.request.CustomerProfileRequest;
import com.fixit.domain.customer.dto.response.CustomerAddressResponse;
import com.fixit.domain.customer.dto.response.CustomerProfileResponse;

import java.util.List;
import java.util.UUID;

// CÚ PHÁP: [Phạm vi truy cập] interface [Tên_Interface] {
// Ý NGHĨA: Khai báo một bản hợp đồng (Interface). Bản hợp đồng này chỉ liệt kê TÊN CÁC HÀNH ĐỘNG (không có code xử lý bên trong).
// Lớp Impl (Implementation) phía sau sẽ phải ký hợp đồng này và viết code thực thi cho từng hành động.
public interface CustomerService {

    // ---------------------------------------------------------
    // NHÓM 1: QUẢN LÝ THÔNG TIN CÁ NHÂN (PROFILE)
    // ---------------------------------------------------------

    // Ý NGHĨA: Hành động "Lấy thông tin cá nhân".
    // Đưa vào ID của User (từ lúc đăng nhập), trả về một "thùng xốp" CustomerProfileResponse.
    CustomerProfileResponse getProfile(UUID userId);

    // Ý NGHĨA: Hành động "Cập nhật thông tin cá nhân".
    // Đưa vào ID của User và dữ liệu người dùng gõ trên mạng (CustomerProfileRequest). Trả về thông tin mới sau khi cập nhật.
    CustomerProfileResponse updateProfile(UUID userId, CustomerProfileRequest request);

    // ---------------------------------------------------------
    // NHÓM 2: QUẢN LÝ SỔ ĐỊA CHỈ (ADDRESS)
    // ---------------------------------------------------------

    // Ý NGHĨA: Lấy toàn bộ danh sách địa chỉ của khách hàng này.
    List<CustomerAddressResponse> getCustomerAddresses(UUID userId);

    // Ý NGHĨA: Thêm một địa chỉ mới.
    CustomerAddressResponse addAddress(UUID userId, CustomerAddressRequest request);

    // Ý NGHĨA: Sửa một địa chỉ đã có. Cần biết ID của địa chỉ cần sửa (addressId).
    CustomerAddressResponse updateAddress(UUID userId, UUID addressId, CustomerAddressRequest request);

    // Ý NGHĨA: Xóa một địa chỉ. Cần biết ID của địa chỉ cần xóa. Hàm này không cần trả về gì (void).
    void deleteAddress(UUID userId, UUID addressId);

    // Ý NGHĨA: Set một địa chỉ làm địa chỉ mặc định để gọi thợ.
    CustomerAddressResponse setDefaultAddress(UUID userId, UUID addressId);

// CÚ PHÁP: }
// Ý NGHĨA: Kết thúc bản hợp đồng.
}
