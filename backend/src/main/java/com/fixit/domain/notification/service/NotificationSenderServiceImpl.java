package com.fixit.domain.notification.service;

import com.fixit.domain.notification.entity.Notification;
import com.fixit.domain.auth.entity.User;
import com.fixit.domain.notification.entity.UserDevice;
import com.fixit.domain.notification.repository.NotificationRepository;
import com.fixit.domain.notification.repository.UserDeviceRepository;
import com.fixit.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSenderServiceImpl implements NotificationSenderService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final FcmService fcmService;

    @Override
    @Transactional
    public void sendNotification(UUID recipientId, String title, String content, Map<String, String> data) {
        log.info("Preparing to send notification to recipient user: {}", recipientId);

        User user = userRepository.findById(recipientId).orElse(null);
        if (user == null) {
            log.error("Cannot send notification: User not found with ID: {}", recipientId);
            return;
        }

        // 1. Save notification record to Database for history log
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .content(content)
                .read(false)
                .build();
        
        notificationRepository.save(notification);
        log.debug("Notification saved to DB for user: {}", recipientId);

        // 2. Fetch all registered device tokens for the user
        List<UserDevice> devices = userDeviceRepository.findByUserId(recipientId);
        if (devices.isEmpty()) {
            log.info("No registered devices found for user: {}. Skipping FCM push.", recipientId);
            return;
        }

        List<String> tokens = devices.stream()
                .map(UserDevice::getDeviceToken)
                .filter(token -> token != null && !token.trim().isEmpty())
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            log.info("FCM tokens are blank for user: {}. Skipping FCM push.", recipientId);
            return;
        }

        // 3. Send multicast push notification to all user's devices
        log.info("Sending FCM push to {} device(s) for user: {}", tokens.size(), recipientId);
        fcmService.sendMulticastNotification(tokens, title, content, data);
    }
}
