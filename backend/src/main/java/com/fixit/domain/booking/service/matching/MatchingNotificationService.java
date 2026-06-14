package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.entity.BookingWorkerAssignment;

import java.util.List;

public interface MatchingNotificationService {
    /**
     * Bắn thông báo (FCM) đến điện thoại của Thợ khi có đơn hàng mới được phân công.
     */
    void sendNewAssignmentNotifications(List<BookingWorkerAssignment> newAssignments);
}
