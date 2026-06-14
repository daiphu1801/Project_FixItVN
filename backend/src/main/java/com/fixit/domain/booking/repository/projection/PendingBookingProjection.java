package com.fixit.domain.booking.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Projection dùng để lấy thông tin Đơn hàng (Booking) đang chờ ghép thợ.
 * Thay vì lấy toàn bộ bảng Booking (có chứa nhiều trường không cần thiết như mô tả lỗi, khách hàng...), 
 * chúng ta chỉ lấy đúng 4 trường này để tối ưu hóa bộ nhớ (RAM).
 */
public interface PendingBookingProjection {
    
    // ID của Đơn đặt hàng
    UUID getBookingId();

    // ID của Dịch vụ (Để lọc ra đúng thợ có chuyên môn)
    Integer getServiceId();

    // Tọa độ của Khách hàng (Đích đến)
    BigDecimal getDestinationLat();
    BigDecimal getDestinationLng();
}
