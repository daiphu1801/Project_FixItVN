package com.fixit.domain.notification.service;

import com.fixit.domain.notification.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    Page<NotificationResponse> getNotifications(UUID userId, Pageable pageable);
    long getUnreadCount(UUID userId);
    void markAsRead(UUID userId, UUID notificationId);
    void markAllAsRead(UUID userId);
}
