package com.fixit.domain.customer.controller;

import com.fixit.domain.auth.entity.User;
import com.fixit.domain.customer.dto.request.CustomerAddressRequest;
import com.fixit.domain.customer.dto.request.CustomerProfileRequest;
import com.fixit.domain.customer.dto.response.CustomerAddressResponse;
import com.fixit.domain.customer.dto.response.CustomerProfileResponse;
import com.fixit.domain.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    // Helper: Lấy UUID của user từ JWT đã được xác thực.
    // JwtAuthenticationFilter đặt User entity (UserDetails) làm principal trong Authentication,
    // nên ta cast thẳng ra User entity và lấy id thật (UUID), không dùng getName() vì getName()
    // trả về username = phoneNumber, không phải UUID.
    private UUID getUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }

    // ---------------------------------------------------------
    // NHÓM 1: PROFILE
    // ---------------------------------------------------------

    // CÚ PHÁP: @[Tên_Annotation]("[Đường_dẫn_phụ]")
    // Ý NGHĨA: Khai báo API lấy thông tin. @GetMapping hứng request có method là GET (chỉ lấy dữ liệu, không sửa).
    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileResponse> getProfile(Authentication authentication) {
        UUID userId = getUserId(authentication);
        
        // Gọi Service xử lý và trả kết quả về cho Android với mã HTTP 200 OK.
        return ResponseEntity.ok(customerService.getProfile(userId));
    }

    // Ý NGHĨA: @PutMapping hứng request sửa dữ liệu.
    // @Valid: Bật chế độ "máy quét" DTO lên. Nếu gõ tên rỗng, nó sẽ chặn lại ngay ở cửa, không cho đi tiếp vào Service.
    // @RequestBody: Lấy cục JSON từ điện thoại gửi lên ném vào cái thùng xốp CustomerProfileRequest.
    @PutMapping("/profile")
    public ResponseEntity<CustomerProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody CustomerProfileRequest request) {
        UUID userId = getUserId(authentication);
        return ResponseEntity.ok(customerService.updateProfile(userId, request));
    }

    // ---------------------------------------------------------
    // NHÓM 2: ADDRESS
    // ---------------------------------------------------------

    @GetMapping("/addresses")
    public ResponseEntity<List<CustomerAddressResponse>> getAddresses(Authentication authentication) {
        UUID userId = getUserId(authentication);
        return ResponseEntity.ok(customerService.getCustomerAddresses(userId));
    }

    // @PostMapping: Hứng request tạo mới (Create).
    @PostMapping("/addresses")
    public ResponseEntity<CustomerAddressResponse> addAddress(
            Authentication authentication,
            @Valid @RequestBody CustomerAddressRequest request) {
        UUID userId = getUserId(authentication);
        return ResponseEntity.ok(customerService.addAddress(userId, request));
    }

    // /{addressId}: Lấy biến addressId trực tiếp từ đường dẫn URL.
    // Ví dụ: PUT /addresses/12345 -> addressId sẽ là 12345
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<CustomerAddressResponse> updateAddress(
            Authentication authentication,
            @PathVariable UUID addressId,
            @Valid @RequestBody CustomerAddressRequest request) {
        UUID userId = getUserId(authentication);
        return ResponseEntity.ok(customerService.updateAddress(userId, addressId, request));
    }

    // @DeleteMapping: Hứng request Xóa.
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            Authentication authentication,
            @PathVariable UUID addressId) {
        UUID userId = getUserId(authentication);
        customerService.deleteAddress(userId, addressId);
        // Xóa xong thì trả về mã 204 No Content (Thành công nhưng không có dữ liệu trả về)
        return ResponseEntity.noContent().build();
    }

    // @PatchMapping: Hứng request sửa một phần nhỏ (ở đây là chỉ set cờ mặc định).
    @PatchMapping("/addresses/{addressId}/default")
    public ResponseEntity<CustomerAddressResponse> setDefaultAddress(
            Authentication authentication,
            @PathVariable UUID addressId) {
        UUID userId = getUserId(authentication);
        return ResponseEntity.ok(customerService.setDefaultAddress(userId, addressId));
    }
}


