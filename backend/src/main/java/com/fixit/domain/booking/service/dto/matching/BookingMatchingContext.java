package com.fixit.domain.booking.service.dto.matching;

import com.fixit.domain.booking.repository.projection.PendingBookingProjection;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Ngữ cảnh ghép cặp (Matching Context) cho một Đơn hàng cụ thể.
 * Nó gom Đơn hàng và Danh sách Thợ ứng viên lại thành một gói dữ liệu hoàn chỉnh.
 */
@Data
@Builder
public class BookingMatchingContext {
    
    // 1. Thông tin gốc của Đơn hàng (Lấy từ Database)
    private PendingBookingProjection booking;
    
    // 2. Danh sách các Thợ (Tối đa 5 người) đã được tính toán Cost cho riêng đơn hàng này
    private List<WorkerMatchingCandidate> candidates;
}
