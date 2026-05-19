package com.fixit.domain.worker.mapper;

import com.fixit.domain.worker.dto.response.WorkerHomeResponse;
import com.fixit.domain.worker.repository.projection.WorkerHomeProjection;
import org.springframework.stereotype.Component;

@Component
public class WorkerHomeMapper {

    public WorkerHomeResponse toResponse(WorkerHomeProjection projection) {
        return WorkerHomeResponse.builder()
                .workerId(projection.getWorkerId())
                .fullName(projection.getFullName())
                .available(projection.getAvailable())
                .verificationStatus(projection.getVerificationStatus())
                .reputationScore(projection.getReputationScore())
                .latitude(projection.getLatitude())
                .longitude(projection.getLongitude())
                .todayAppointmentCount(projection.getTodayAppointmentCount())
                .pendingAssignmentCount(projection.getPendingAssignmentCount())
                .availableBalance(projection.getAvailableBalance())
                .heldBalance(projection.getHeldBalance())
                .debtBalance(projection.getDebtBalance())
                .build();
    }
}