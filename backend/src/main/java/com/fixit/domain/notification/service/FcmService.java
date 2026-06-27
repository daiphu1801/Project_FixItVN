package com.fixit.domain.notification.service;

import java.util.List;
import java.util.Map;

public interface FcmService {
    void sendNotification(String token, String title, String body, Map<String, String> data);
    void sendMulticastNotification(List<String> tokens, String title, String body, Map<String, String> data);
}
