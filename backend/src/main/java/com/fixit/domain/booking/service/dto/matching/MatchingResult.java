package com.fixit.domain.booking.service.dto.matching;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Lớp đại diện cho kết quả CỐT LÕI cuối cùng sau khi cỗ máy Hungarian tính toán xong.
 * Nó mang ý nghĩa: Đơn hàng này (BookingId) sẽ thuộc về Anh Thợ này (WorkerId).
 */
@Data
@AllArgsConstructor
public class MatchingResult {
    
    private UUID bookingId;
    private UUID workerId;
    
    /**
     * Kiểm tra xem đơn hàng này có ghép thành công hay không.
     * (Trong trường hợp đêm khuya, không có anh thợ nào rảnh, workerId sẽ là null)
     */
    public boolean isMatched() {
        return workerId != null;
    }
}
