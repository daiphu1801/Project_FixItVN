package com.fixit.domain.customer.repository;

import com.fixit.domain.customer.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * FILE SỐ 2: CustomerAddressRepository
 * ====================================
 * Quản lý các thao tác Database cho bảng "customer_addresses".
 * Khác với Customer (1 User chỉ có 1 Customer), bảng này 1 Customer có thể có NHIỀU địa chỉ.
 */
@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, UUID> {

    /**
     * 1. Tìm TẤT CẢ địa chỉ của một khách hàng cụ thể.
     * 
     * Dùng khi khách hàng mở màn hình "Danh sách địa chỉ".
     * Spring sẽ dịch thành: SELECT * FROM customer_addresses WHERE customer_id = ?
     * Trả về List vì có thể có nhiều địa chỉ.
     */
    List<CustomerAddress> findByCustomer_CustomerId(UUID customerId);

    /**
     * 2. Tìm MỘT địa chỉ cụ thể của MỘT khách hàng cụ thể.
     * 
     * Dùng khi khách hàng muốn Sửa/Xóa 1 địa chỉ.
     * Tại sao không dùng findById(id) có sẵn? 
     * -> Đề phòng bảo mật! Nếu dùng findById, user A có thể truyền ID địa chỉ của user B để xóa trộm.
     * -> Phải check cả "id địa chỉ" VÀ "id chủ sở hữu" (customer_id).
     * Spring dịch: SELECT * FROM customer_addresses WHERE id = ? AND customer_id = ?
     */
    Optional<CustomerAddress> findByIdAndCustomer_CustomerId(UUID addressId, UUID customerId);

    /**
     * 3. Tìm địa chỉ MẶC ĐỊNH của một khách hàng.
     * 
     * Dùng khi khách hàng tạo Đơn đặt thợ mới, hệ thống tự động điền địa chỉ mặc định.
     * Spring dịch: SELECT * FROM customer_addresses WHERE customer_id = ? AND is_default = true
     */
    Optional<CustomerAddress> findByCustomer_CustomerIdAndDefaultAddressTrue(UUID customerId);
}
