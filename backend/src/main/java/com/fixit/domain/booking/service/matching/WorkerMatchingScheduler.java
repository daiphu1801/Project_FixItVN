package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.entity.Booking;
import com.fixit.domain.booking.entity.BookingWorkerAssignment;
import com.fixit.domain.booking.repository.BookingRepository;
import com.fixit.domain.booking.repository.BookingWorkerAssignmentRepository;
import com.fixit.domain.booking.repository.projection.PendingBookingProjection;
import com.fixit.domain.booking.service.dto.matching.MatchingResult;
import com.fixit.domain.worker.entity.Worker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * ĐỒNG HỒ BÁO THỨC (Scheduler) của hệ thống.
 * Tự động chạy ngầm định kỳ để tìm đơn mồ côi và gọi thuật toán.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkerMatchingScheduler {

    private final BookingRepository bookingRepository;
    private final WorkerMatchingService matchingService;
    private final BookingWorkerAssignmentRepository assignmentRepository;
    private final MatchingNotificationService notificationService;

    /**
     * Dấu @Scheduled sẽ làm hàm này tự động chạy liên tục.
     * fixedDelayString: Sau khi chạy xong, nó sẽ nghỉ 5 giây rồi mới chạy lại 
     * (thời gian nghỉ 5000ms này được đọc từ file application-dev.yml).
     */
    @Scheduled(fixedDelayString = "${app.matching.batch-interval-ms:5000}")
    @Transactional
    public void runBatchMatching() {
        
        // 1. Quét tìm các Đơn hàng đang bị mồ côi (Tầng 3)
        List<PendingBookingProjection> pendingBookings = bookingRepository.findUnassignedPendingBookings();

        if (pendingBookings.isEmpty()) {
            // Không có đơn nào thì thoát, đi ngủ tiếp 5 giây nữa
            return; 
        }

        log.info("[SCHEDULER] Đã quét thấy {} đơn hàng mồ côi. Đánh thức Nhạc trưởng...", pendingBookings.size());

        // 2. Giao việc cho Nhạc trưởng chạy luồng Thuật toán (Tầng 6)
        List<MatchingResult> results = matchingService.performBatchMatching(pendingBookings);

        // 3. Xử lý kết quả Nhạc trưởng báo cáo về
        List<BookingWorkerAssignment> newAssignments = new ArrayList<>();

        for (MatchingResult result : results) {
            if (result.isMatched()) {
                // Tình huống 1: Ghép đôi thành công!
                // Tạo một bản ghi Assignment (Phân công) lưu vào Database
                Booking bookingRef = new Booking();
                bookingRef.setId(result.getBookingId());
                
                Worker workerRef = new Worker();
                workerRef.setWorkerId(result.getWorkerId());

                BookingWorkerAssignment assignment = new BookingWorkerAssignment();
                assignment.setBooking(bookingRef);
                assignment.setWorker(workerRef);
                assignment.setStatus(com.fixit.domain.booking.entity.AssignmentStatus.Pending); // Gán trạng thái chờ Thợ bấm Đồng ý

                newAssignments.add(assignment);
                log.info(" [MATCHED] Đơn {} ---> Thợ {}", result.getBookingId(), result.getWorkerId());
                
            } else {
                // Tình huống 2: Đơn ế (Có thể do khuya quá thợ tắt app đi ngủ hết)
                // Kệ nó, 5 giây sau chu kỳ tiếp theo nó sẽ được quét lại.
                log.warn(" [UNMATCHED] Đơn {} không tìm thấy anh thợ rảnh nào.", result.getBookingId());
            }
        }

        // 4. Lưu tất cả kết quả ghép cặp thành công vào Database (Batch Save)
        if (!newAssignments.isEmpty()) {
            assignmentRepository.saveAll(newAssignments);
            log.info("[SCHEDULER] Đã chốt sổ và lưu {} bản ghi phân công vào CSDL.", newAssignments.size());
            
            // 5. Đánh thức TẦNG 8: Gọi loa phường thông báo cho thợ biết
            notificationService.sendNewAssignmentNotifications(newAssignments);
        }
    }
}
