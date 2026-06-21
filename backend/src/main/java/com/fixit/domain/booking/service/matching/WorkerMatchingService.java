package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.repository.projection.PendingBookingProjection;
import com.fixit.domain.booking.service.dto.matching.MatchingResult;

import java.util.List;

public interface WorkerMatchingService {
    /**
     * Hàm chính: Nhận vào 1 danh sách Đơn hàng đang chờ, trả về 1 danh sách Kết quả Ghép cặp
     */
    List<MatchingResult> performBatchMatching(List<PendingBookingProjection> pendingBookings);
}
