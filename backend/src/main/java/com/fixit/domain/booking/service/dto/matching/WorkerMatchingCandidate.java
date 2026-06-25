package com.fixit.domain.booking.service.dto.matching;

import com.fixit.domain.worker.repository.projection.WorkerCandidateProjection;
import lombok.Builder;
import lombok.Data;

/**
 * Lớp đại diện cho một Ứng cử viên (Thợ) đang được xem xét cho một Đơn hàng.
 * Lớp này kết hợp dữ liệu gốc từ DB và các chỉ số vừa được tính toán (Khoảng cách, Thời gian, Cost).
 */
@Data
@Builder
public class WorkerMatchingCandidate {
    
    // 1. Thông tin gốc của Thợ lấy từ Database (Tầng 3)
    private WorkerCandidateProjection workerInfo;
    
    // 2. Khoảng cách địa lý thực tế (Km)
    private double distanceKm;
    
    // 3. Thời gian đi đường dự kiến (Phút)
    private double durationMins;
    
    // 4. ĐIỂM CHI PHÍ (COST) CUỐI CÙNG 
    // Đây là con số quan trọng nhất sẽ được đưa vào Thuật toán Hungarian.
    // Cost càng thấp = Thợ càng phù hợp.
    private double matchingCost; 
    
    // 5. Cờ đánh dấu xem thông số này lấy từ Google Maps hay công thức Haversine dự phòng
    private boolean isGoogleMapsUsed;
}
