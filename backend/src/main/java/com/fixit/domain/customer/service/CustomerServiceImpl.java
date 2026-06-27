package com.fixit.domain.customer.service;

import com.fixit.domain.auth.entity.User;
import com.fixit.domain.auth.repository.UserRepository;
import com.fixit.domain.customer.dto.request.CustomerAddressRequest;
import com.fixit.domain.customer.dto.request.CustomerProfileRequest;
import com.fixit.domain.customer.dto.response.CustomerAddressResponse;
import com.fixit.domain.customer.dto.response.CustomerProfileResponse;
import com.fixit.domain.customer.entity.Customer;
import com.fixit.domain.customer.entity.CustomerAddress;
import com.fixit.domain.customer.repository.CustomerAddressRepository;
import com.fixit.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// CÚ PHÁP: @[Tên_Annotation]
// Ý NGHĨA: Đánh dấu class này là Tầng Service (chứa não bộ logic). Spring Boot sẽ tự động quản lý class này.
@Service

// Ý NGHĨA: Annotation của Lombok, tự động sinh Constructor cho các biến có chữ "final".
// Đây là kỹ thuật "Dependency Injection" - Nhờ Spring Boot tự động đưa các "thủ kho" vào để Service dùng.
@RequiredArgsConstructor

// CÚ PHÁP: [Phạm vi truy cập] class [Tên_Class] implements [Tên_Interface] {
// Ý NGHĨA: Khai báo class thực thi. Bắt buộc phải viết code cho toàn bộ 7 hàm đã hứa trong CustomerService.
public class CustomerServiceImpl implements CustomerService {

    // CÚ PHÁP: [Phạm vi truy cập] final [Kiểu dữ liệu] [Tên_biến];
    // Ý NGHĨA: Khai báo 3 "thủ kho" để lấy/lưu dữ liệu. Biến "final" bắt buộc phải được khởi tạo (Lombok đã lo).
    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository addressRepository;
    private final UserRepository userRepository;

    // =========================================================
    // NHÓM 1: QUẢN LÝ PROFILE
    // =========================================================

    // CÚ PHÁP: @Override
    // Ý NGHĨA: Báo hiệu đây là hàm được viết đè (thực thi) từ Interface cha.
    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponse getProfile(UUID userId) {
        // Tìm Customer record. Nếu chưa có (user mới đăng ký), trả về profile tối thiểu từ User entity.
        return customerRepository.findByUser_Id(userId)
                .map(customer -> CustomerProfileResponse.builder()
                        .id(customer.getCustomerId())
                        .fullName(customer.getFullName())
                        .email(customer.getUser() != null ? customer.getUser().getEmail() : null)
                        .phoneNumber(customer.getUser() != null ? customer.getUser().getPhoneNumber() : null)
                        .gender(customer.getGender())
                        .dob(customer.getDob())
                        .build())
                .orElseGet(() -> {
                    // Customer record chưa tồn tại → trả về profile tối thiểu từ User entity
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
                    return CustomerProfileResponse.builder()
                            .id(null)
                            .fullName(null)
                            .email(user.getEmail())
                            .phoneNumber(user.getPhoneNumber())
                            .gender(null)
                            .dob(null)
                            .build();
                });
    }

    @Override
    // CÚ PHÁP: @[Tên_Annotation]
    // Ý NGHĨA: Đảm bảo tính toàn vẹn dữ liệu. Nếu đang chạy mà lỗi giữa chừng, Database sẽ Rollback (quay lại trạng thái cũ).
    @Transactional
    public CustomerProfileResponse updateProfile(UUID userId, CustomerProfileRequest request) {
        // BƯỚC 1: Tìm Customer trong kho.
        Customer customer = customerRepository.findByUser_Id(userId).orElse(null);

        if (customer == null) {
            // NẾU CHƯA CÓ: Nghĩa là khách mới đăng ký, chưa có hồ sơ -> Tạo mới luôn!
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));
            
            customer = Customer.builder()
                    .user(user)
                    .fullName(request.getFullName())
                    .gender(request.getGender())
                    .dob(request.getDob())
                    .build();
        } else {
            // NẾU ĐÃ CÓ: Chỉ cần cập nhật dữ liệu mới
            customer.setFullName(request.getFullName());
            customer.setGender(request.getGender());
            customer.setDob(request.getDob());
        }

        // Bổ sung cập nhật email trong User entity
        if (request.getEmail() != null) {
            User user = customer.getUser();
            if (user != null) {
                user.setEmail(request.getEmail());
                userRepository.save(user);
            }
        }

        // BƯỚC 2: Gọi thủ kho lưu vào Database
        Customer savedCustomer = customerRepository.save(customer);

        // BƯỚC 3: Đóng gói vào DTO trả về cho Android
        return CustomerProfileResponse.builder()
                .id(savedCustomer.getCustomerId())
                .fullName(savedCustomer.getFullName())
                .email(savedCustomer.getUser() != null ? savedCustomer.getUser().getEmail() : null)
                .phoneNumber(savedCustomer.getUser() != null ? savedCustomer.getUser().getPhoneNumber() : null)
                .gender(savedCustomer.getGender())
                .dob(savedCustomer.getDob())
                .build();
    }

    // =========================================================
    // NHÓM 2: QUẢN LÝ ADDRESS
    // =========================================================

    @Override
    public List<CustomerAddressResponse> getCustomerAddresses(UUID userId) {
        // Lấy toàn bộ địa chỉ dạng Entity
        List<CustomerAddress> addresses = addressRepository.findByCustomer_CustomerId(userId);

        // Biến đổi danh sách Entity thành danh sách DTO (Bọc từng cái vào thùng xốp)
        return addresses.stream().map(this::mapToAddressResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerAddressResponse addAddress(UUID userId, CustomerAddressRequest request) {
        // Tìm chủ sở hữu
        Customer customer = customerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ khách hàng"));

        // Tạo Entity địa chỉ mới từ dữ liệu Request DTO
        CustomerAddress address = CustomerAddress.builder()
                .customer(customer)
                .label(request.getLabel())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .defaultAddress(request.getDefaultAddress() != null ? request.getDefaultAddress() : false)
                .build();

        // Xử lý logic: Nếu địa chỉ mới được tick là "Mặc định", phải đi gỡ "Mặc định" của các địa chỉ cũ
        if (address.getDefaultAddress()) {
            resetDefaultAddress(userId);
        }

        // Lưu vào DB
        CustomerAddress savedAddress = addressRepository.save(address);

        // Trả về DTO
        return mapToAddressResponse(savedAddress);
    }

    @Override
    @Transactional
    public CustomerAddressResponse updateAddress(UUID userId, UUID addressId, CustomerAddressRequest request) {
        // Tìm địa chỉ cũ (phải khớp cả addressId VÀ userId để tránh xóa trộm)
        CustomerAddress address = addressRepository.findByIdAndCustomer_CustomerId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ hoặc bạn không có quyền sửa"));

        // Cập nhật dữ liệu mới
        address.setLabel(request.getLabel());
        address.setAddress(request.getAddress());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        // Nếu khách chọn làm mặc định mới -> Gỡ mặc định các cái cũ
        Boolean isDefault = request.getDefaultAddress();
        if (isDefault != null && isDefault) {
            resetDefaultAddress(userId);
            address.setDefaultAddress(true);
        } else if (isDefault != null) {
            address.setDefaultAddress(false);
        }

        CustomerAddress savedAddress = addressRepository.save(address);
        return mapToAddressResponse(savedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        // Tương tự, tìm đúng địa chỉ của người này rồi mới xóa
        CustomerAddress address = addressRepository.findByIdAndCustomer_CustomerId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ để xóa"));
        
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public CustomerAddressResponse setDefaultAddress(UUID userId, UUID addressId) {
        // Tìm địa chỉ muốn set mặc định
        CustomerAddress address = addressRepository.findByIdAndCustomer_CustomerId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        // Gỡ mặc định cái cũ, gắn mặc định cái mới
        resetDefaultAddress(userId);
        address.setDefaultAddress(true);

        return mapToAddressResponse(addressRepository.save(address));
    }

    // =========================================================
    // CÁC HÀM PHỤ TRỢ (Helper Methods)
    // =========================================================

    // CÚ PHÁP: private [Kiểu trả về] [Tên_hàm]() {
    // Ý NGHĨA: Hàm dùng nội bộ trong class này. Dùng để gỡ cờ "Mặc định" của địa chỉ cũ.
    private void resetDefaultAddress(UUID customerId) {
        addressRepository.findByCustomer_CustomerIdAndDefaultAddressTrue(customerId)
                .ifPresent(oldDefault -> {
                    oldDefault.setDefaultAddress(false);
                    addressRepository.save(oldDefault);
                });
    }

    // Ý NGHĨA: Hàm nội bộ giúp đóng gói từ Entity thô sang DTO gọn gàng để tránh lặp code.
    private CustomerAddressResponse mapToAddressResponse(CustomerAddress address) {
        return CustomerAddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .address(address.getAddress())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .defaultAddress(address.getDefaultAddress())
                .build();
    }
}
