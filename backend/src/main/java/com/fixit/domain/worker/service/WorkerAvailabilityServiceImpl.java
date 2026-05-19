package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.request.WorkerLocationUpdateRequest;
import com.fixit.domain.worker.dto.request.WorkerStatusUpdateRequest;
import com.fixit.domain.worker.dto.response.WorkerHomeResponse;
import com.fixit.domain.worker.mapper.WorkerHomeMapper;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.domain.worker.repository.projection.WorkerHomeProjection;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerAvailabilityServiceImpl implements WorkerAvailabilityService {

    private final WorkerRepository workerRepository;
    private final CurrentWorkerResolver currentWorkerResolver;
    private final WorkerHomeMapper workerHomeMapper;

    @Override
    @Transactional(readOnly = true)
    public WorkerHomeResponse getHome() {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        return getWorkerHomeResponse(workerId);
    }

    @Override
    @Transactional
    public WorkerHomeResponse updateStatus(WorkerStatusUpdateRequest request) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        WorkerHomeProjection currentHome = workerRepository.findWorkerHomeByWorkerId(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thợ hiện tại"));

        if ("Rejected".equalsIgnoreCase(currentHome.getVerificationStatus())) {
            throw new IllegalStateException("Hồ sơ thợ đã bị từ chối, không thể bật nhận việc");
        }

        if (Boolean.TRUE.equals(request.getAvailable())
                && currentHome.getDebtBalance() != null
                && currentHome.getDebtBalance().signum() > 0) {
            throw new IllegalStateException("Thợ đang có khoản nợ, không thể bật nhận việc");
        }

        int updatedRows = workerRepository.updateAvailability(workerId, request.getAvailable());

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Không thể cập nhật trạng thái thợ");
        }

        return getWorkerHomeResponse(workerId);
    }

    @Override
    @Transactional
    public WorkerHomeResponse updateLocation(WorkerLocationUpdateRequest request) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        int updatedRows = workerRepository.updateLocation(
                workerId,
                request.getLatitude(),
                request.getLongitude()
        );

        if (updatedRows == 0) {
            throw new IllegalArgumentException("Không thể cập nhật vị trí thợ");
        }

        return getWorkerHomeResponse(workerId);
    }

    private WorkerHomeResponse getWorkerHomeResponse(UUID workerId) {
        WorkerHomeProjection projection = workerRepository.findWorkerHomeByWorkerId(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thợ hiện tại"));

        return workerHomeMapper.toResponse(projection);
    }
}