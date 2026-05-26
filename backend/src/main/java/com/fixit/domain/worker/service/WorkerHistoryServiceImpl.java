package com.fixit.domain.worker.service;

import com.fixit.domain.worker.dto.response.WorkerHistoryResponse;
import com.fixit.domain.worker.repository.projection.WorkerHistoryItemProjection;
import com.fixit.domain.worker.repository.query.WorkerHistoryQueryRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerHistoryServiceImpl implements WorkerHistoryService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final CurrentWorkerResolver currentWorkerResolver;
    private final WorkerHistoryQueryRepository workerHistoryQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public WorkerHistoryResponse getMyHistory(String status, Integer page, Integer size) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        int safePage = normalizePage(page);
        int safeSize = normalizeSize(size);
        int offset = safePage * safeSize;

        String normalizedStatus = normalizeStatus(status);

        List<WorkerHistoryItemProjection> projections =
                workerHistoryQueryRepository.findHistoryByWorkerId(
                        workerId,
                        normalizedStatus,
                        safeSize,
                        offset
                );

        long totalItems = workerHistoryQueryRepository.countHistoryByWorkerId(
                workerId,
                normalizedStatus
        );

        int totalPages = safeSize == 0
                ? 0
                : (int) Math.ceil((double) totalItems / safeSize);

        List<WorkerHistoryResponse.HistoryItem> items = projections.stream()
                .map(this::toHistoryItem)
                .toList();

        return WorkerHistoryResponse.builder()
                .page(safePage)
                .size(safeSize)
                .totalItems(totalItems)
                .totalPages(totalPages)
                .empty(items.isEmpty())
                .statusFilter(normalizedStatus.isBlank() ? null : normalizedStatus)
                .items(items)
                .build();
    }

    private WorkerHistoryResponse.HistoryItem toHistoryItem(WorkerHistoryItemProjection p) {
        return WorkerHistoryResponse.HistoryItem.builder()
                .bookingId(p.getBookingId())
                .serviceName(p.getServiceName())
                .customerName(p.getCustomerName())
                .address(p.getAddress())
                .status(p.getStatus())
                .statusText(toStatusText(p.getStatus()))
                .scheduledTime(p.getScheduledTime())
                .finishedAt(p.getFinishedAt())
                .finalPrice(defaultMoney(p.getFinalPrice()))
                .paymentMethod(p.getPaymentMethod())
                .issueDescription(p.getIssueDescription())
                .build();
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 0) {
            return DEFAULT_PAGE;
        }

        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }

        return Math.min(size, MAX_SIZE);
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "";
        }

        String trimmedStatus = status.trim();

        if ("completed".equalsIgnoreCase(trimmedStatus)) {
            return "Completed";
        }

        if ("cancelled".equalsIgnoreCase(trimmedStatus)
                || "canceled".equalsIgnoreCase(trimmedStatus)) {
            return "Cancelled";
        }

        throw new IllegalArgumentException("Trạng thái lịch sử không hợp lệ. Chỉ hỗ trợ Completed hoặc Cancelled");
    }

    private String toStatusText(String status) {
        if (status == null) {
            return "Không xác định";
        }

        return switch (status) {
            case "Completed" -> "Hoàn thành";
            case "Cancelled" -> "Đã hủy";
            default -> status;
        };
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}