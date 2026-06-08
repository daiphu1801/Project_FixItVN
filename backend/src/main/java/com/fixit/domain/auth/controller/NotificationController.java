package com.fixit.domain.auth.controller;

import com.fixit.domain.auth.dto.response.NotificationResponse;
import com.fixit.domain.auth.entity.User;
import com.fixit.domain.auth.service.NotificationService;
import com.fixit.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getNotifications(user.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable UUID notificationId) {
        notificationService.markAsRead(user.getId(), notificationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
