package com.fixit.domain.notification.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FcmServiceImpl implements FcmService {

    @Override
    public void sendNotification(String token, String title, String body, Map<String, String> data) {
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("FirebaseApp is not initialized. Skipping sending FCM notification to token: {}", token);
            return;
        }

        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message.Builder messageBuilder = Message.builder()
                    .setToken(token)
                    .setNotification(notification);

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            Message message = messageBuilder.build();
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent FCM message: {}", response);

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message to token: {}", token, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while sending FCM message to token: {}", token, e);
        }
    }

    @Override
    public void sendMulticastNotification(List<String> tokens, String title, String body, Map<String, String> data) {
        if (FirebaseApp.getApps().isEmpty()) {
            log.warn("FirebaseApp is not initialized. Skipping sending FCM multicast notification to {} tokens",
                    tokens.size());
            return;
        }

        if (CollectionUtils.isEmpty(tokens)) {
            log.warn("Device token list is empty. Skipping multicast FCM notification.");
            return;
        }

        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(notification);

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            MulticastMessage message = messageBuilder.build();
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

            log.info("FCM Multicast sent: {} success, {} failure out of {} total messages",
                    response.getSuccessCount(), response.getFailureCount(), tokens.size());

            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        log.warn("FCM failed for token: {} with error: {}",
                                tokens.get(i), responses.get(i).getException().getMessage());
                    }
                }
            }
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM multicast messages", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while sending FCM multicast messages", e);
        }
    }
}
