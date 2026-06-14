package com.fixit.domain.worker.repository.projection;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Projection dùng để lấy thông tin Thợ ứng viên phục vụ cho thuật toán Ghép cặp (Matching).
 * Thay vì lấy toàn bộ bảng Worker (có chứa nhiều trường không cần thiết như CMND, kinh nghiệm...), 
 * chúng ta chỉ lấy đúng 6 trường này để tối ưu hóa bộ nhớ (RAM) và tốc độ truy vấn CSDL.
 */
public interface WorkerCandidateProjection {
    
    // ID của Thợ
    UUID getWorkerId();

    // Tọa độ hiện tại của Thợ
    BigDecimal getLatitude();
    BigDecimal getLongitude();

    // Điểm uy tín (Dùng để thưởng/giảm Cost trong thuật toán)
    BigDecimal getReputationScore();

    // Số lần từ chối đơn (Dùng để phạt/tăng Cost)
    Integer getRejectionCount();

    // Thời gian bị cấm ưu tiên (Nếu thợ huỷ đơn quá nhiều bị dính soft-ban)
    OffsetDateTime getRejectedPriorityUntil();
}
