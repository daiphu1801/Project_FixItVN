package com.fixit.domain.customer.controller;

import com.fixit.domain.customer.dto.request.CustomerAddressRequest;
import com.fixit.domain.customer.dto.request.CustomerProfileRequest;
import com.fixit.domain.customer.dto.response.CustomerAddressResponse;
import com.fixit.domain.customer.dto.response.CustomerProfileResponse;
import com.fixit.domain.customer.service.CustomerService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.fixit.global.util.SecurityUtil;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

// CÚ PHÁP: @[Tên_Annotation]
// Ý NGHĨA: Báo cho Spring Boot biết đây là "Cô lễ tân" (Controller).
// Cô lễ tân này chuyên đứng trực ở cửa để nhận Request từ App Android gửi tới qua mạng Internet.
@RestController

// CÚ PHÁP: @[Tên_Annotation]("[Đường_dẫn_API]")
// Ý NGHĨA: Đặt địa chỉ IP/đường dẫn chung cho toàn bộ các API trong file này.
// Tất cả các request bắt đầu bằng /api/v1/customers/me sẽ được chuyển vào đây.
@RequestMapping("/api/v1/customers/me")

// Ý NGHĨA: Yêu cầu Spring Boot tự động "thuê" anh Service (Bộ não) đưa cho cô Lễ tân này sai vặt.
@RequiredArgsConstructor
public class CustomerController {

    // Lễ tân (Controller) bắt buộc phải có Bộ não (Service) để hỏi cách xử lý.
    private final CustomerService customerService;

    // ---------------------------------------------------------
    // NHÓM 1: PROFILE
    // ---------------------------------------------------------

    // CÚ PHÁP: @[Tên_Annotation]("[Đường_dẫn_phụ]")
    // Ý NGHĨA: Khai báo API lấy thông tin. @GetMapping hứng request có method là GET (chỉ lấy dữ liệu, không sửa).
    @GetMapping("/profile")
    public ApiResponse<CustomerProfileResponse> getProfile(Principal principal) {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(customerService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ApiResponse<CustomerProfileResponse> updateProfile(
            Principal principal, 
            @Valid @RequestBody CustomerProfileRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(customerService.updateProfile(userId, request));
    }

    // ---------------------------------------------------------
    // NHÓM 2: ADDRESS
    // ---------------------------------------------------------

    @GetMapping("/addresses")
    public ApiResponse<List<CustomerAddressResponse>> getAddresses(Principal principal) {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(customerService.getCustomerAddresses(userId));
    }

    @PostMapping("/addresses")
    public ApiResponse<CustomerAddressResponse> addAddress(
            Principal principal, 
            @Valid @RequestBody CustomerAddressRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(customerService.addAddress(userId, request));
    }

    @PutMapping("/addresses/{addressId}")
    public ApiResponse<CustomerAddressResponse> updateAddress(
            Principal principal,
            @PathVariable UUID addressId,
            @Valid @RequestBody CustomerAddressRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(customerService.updateAddress(userId, addressId, request));
    }

    @DeleteMapping("/addresses/{addressId}")
    public ApiResponse<Void> deleteAddress(
            Principal principal,
            @PathVariable UUID addressId) {
        UUID userId = SecurityUtil.getCurrentUserId();
        customerService.deleteAddress(userId, addressId);
        return ApiResponse.success(null, "Xóa địa chỉ thành công");
    }

    @PatchMapping("/addresses/{addressId}/default")
    public ApiResponse<CustomerAddressResponse> setDefaultAddress(
            Principal principal,
            @PathVariable UUID addressId) {
        UUID userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.success(customerService.setDefaultAddress(userId, addressId));
    }
}
