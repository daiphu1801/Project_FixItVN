package com.fixit.domain.customer.repository;

// Import entity Customer mà Repository này quản lý
import com.fixit.domain.customer.entity.Customer;

// Spring Data JPA cung cấp sẵn các hàm CRUD cơ bản (findAll, findById, save, delete...)
import org.springframework.data.jpa.repository.JpaRepository;

// Đánh dấu đây là một Repository để Spring Boot nhận biết và quản lý
import org.springframework.stereotype.Repository;

// UUID là kiểu dữ liệu của khóa chính trong bảng customers
import java.util.UUID;

// Optional giúp tránh NullPointerException khi không tìm thấy dữ liệu
import java.util.Optional;

/**
 * FILE SỐ 1: CustomerRepository
 * ==============================
 * Tầng duy nhất được phép trực tiếp truy xuất bảng "customers" trong Database.
 *
 * Nhờ kế thừa JpaRepository<Customer, UUID>, Spring tự động cung cấp sẵn:
 * - findAll() → SELECT * FROM customers
 * - findById(id) → SELECT * FROM customers WHERE customer_id = ?
 * - save(customer) → INSERT hoặc UPDATE tùy theo object đã tồn tại chưa
 * - deleteById(id) → DELETE FROM customers WHERE customer_id = ?
 * - existsById(id) → Kiểm tra có tồn tại hay không
 *
 * Tham số JpaRepository<Customer, UUID>:
 * - Customer: Entity (bảng) mà Repository này phụ trách
 * - UUID: Kiểu dữ liệu của khóa chính @Id trong Customer.java
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    /**
     * Hàm tùy chỉnh: Tìm Customer dựa vào userId từ JWT token sau khi đăng nhập.
     *
     * Tại sao cần hàm này?
     * - Backend nhận userId từ JWT token
     * - Nhưng JpaRepository chỉ có findById(customerId)
     * - Vì customer_id = user_id (shared key), hàm này thực chất tìm theo cùng một
     * giá trị
     * - Nhưng về mặt ngữ nghĩa, ta tìm "Customer của User có userId này"
     *
     * Cú pháp Spring Data JPA tự hiểu:
     * "findBy" + "User" (tên field trong Customer.java) + "_" + "Id" (field
     * trong User.java)
     *
     * SQL tương đương: SELECT * FROM customers WHERE customer_id = ?
     *
     * Trả về Optional<Customer> để Service có thể kiểm tra .isPresent()
     * thay vì bị NullPointerException nếu không tìm thấy.
     */
    Optional<Customer> findByUser_Id(UUID userId);
}
