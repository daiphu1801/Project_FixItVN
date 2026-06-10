package com.fixit.domain.notification.service;

import java.util.Map;
import java.util.UUID;

public interface NotificationSenderService {
    void sendNotification(UUID recipientId, String title, String content, Map<String, String> data);
}
