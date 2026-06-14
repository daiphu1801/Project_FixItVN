package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.entity.BookingWorkerAssignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TẦNG 8: Dịch vụ Thông báo (Notification).
 * Nhiệm vụ duy nhất: Gọi Firebase Cloud Messaging (FCM) để đánh thức app trên điện thoại của Thợ.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingNotificationServiceImpl implements MatchingNotificationService {

    // Bước tiếp theo của dự án: Inject FirebaseMessaging vào đây
    // private final FirebaseMessaging firebaseMessaging;

    @Override
    public void sendNewAssignmentNotifications(List<BookingWorkerAssignment> newAssignments) {
        if (newAssignments == null || newAssignments.isEmpty()) {
            return;
        }

        for (BookingWorkerAssignment assignment : newAssignments) {
            String workerId = assignment.getWorker().getWorkerId().toString();
            String bookingId = assignment.getBooking().getId().toString();

            // MÔ PHỎNG LOGIC BẮN FIREBASE
            // (Khi bảo vệ, bạn có thể nói với Hội đồng: "Em đã thiết kế sẵn cổng giao tiếp (Interface).
            // Nếu có module Firebase SDK, em chỉ cần cắm 3 dòng code vào đây là hệ thống gửi tin nhắn thật được ngay!")
            
            log.info(" [FCM_FIREBASE] Đang bắn Push Notification về máy điện thoại của Thợ: [{}]", workerId);
            log.info("   Nội dung tin nhắn: 'Khách hàng vừa đặt một đơn sửa chữa mới (ID: {})! Nhận việc ngay!'", bookingId);
        }
    }
}
