package com.fixit.domain.booking.scheduler;

import com.fixit.domain.booking.service.WorkerAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerAssignmentScheduler {

    private final WorkerAssignmentService workerAssignmentService;

    /**
     * Tự động chuyển các assignment Pending quá hạn sang Missed.
     *
     * fixedDelayString có default 30000 ms nếu chưa cấu hình trong application.yml.
     */
    @Scheduled(fixedDelayString = "${app.assignment.scheduler-delay-ms:30000}")
    public void markExpiredAssignmentsAsMissed() {
        int handledCount = workerAssignmentService.markExpiredAssignmentsAsMissed();

        if (handledCount > 0) {
            log.info("Auto marked {} expired assignments as Missed", handledCount);
        }
    }
}