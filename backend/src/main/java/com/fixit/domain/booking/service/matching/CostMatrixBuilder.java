package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.service.dto.matching.WorkerMatchingCandidate;
import com.fixit.global.config.WorkerMatchingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Cỗ máy tính toán Điểm Chi Phí (Cost).
 * Chịu trách nhiệm nhồi các chỉ số vào công thức Toán học để ra được 1 điểm Cost duy nhất.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CostMatrixBuilder {

    private final WorkerMatchingProperties properties;

    /**
     * Hàm này tính Điểm Cost cho một Thợ và cập nhật trực tiếp vào đối tượng đó.
     * Cost = (Trọng số Thời gian * ETA) 
     *      - (Trọng số Uy tín * Điểm uy tín) 
     *      + (Trọng số Phạt * Số lần huỷ đơn)
     */
    public void calculateAndSetCost(WorkerMatchingCandidate candidate) {
        
        double etaMins = candidate.getDurationMins();
        double reputation = candidate.getWorkerInfo().getReputationScore().doubleValue();
        int rejectionCount = candidate.getWorkerInfo().getRejectionCount();

        // 1. Ráp công thức từ các hệ số đã khai báo trong application-dev.yml
        double cost = (properties.getCostWeightEta() * etaMins)
                    - (properties.getCostWeightReputation() * reputation)
                    + (properties.getCostWeightCancelRate() * rejectionCount);

        // 2. Thuật toán Hungary yêu cầu ma trận chi phí phải là số dương không âm (>= 0).
        // Nếu anh thợ quá uy tín (điểm cao vút) làm cho phép trừ tạo ra số âm, 
        // ta ép nó về mức sàn là 0.0 (chi phí lý tưởng nhất, tương đương "Miễn phí").
        cost = Math.max(0.0, cost);

        // 3. Lưu điểm Cost vào trong cái túi DTO
        candidate.setMatchingCost(cost);
        
        log.debug("Worker [{}] | ETA: {}m | Rep: {}* | Rej: {}x ==> FINAL COST: {}", 
                  candidate.getWorkerInfo().getWorkerId(), 
                  String.format("%.1f", etaMins), 
                  reputation, 
                  rejectionCount, 
                  String.format("%.2f", cost));
    }
}
