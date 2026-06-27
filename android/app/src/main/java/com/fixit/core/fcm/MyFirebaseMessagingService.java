package com.fixit.core.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.fixit.R;
import com.fixit.core.storage.SessionStorage;
import com.fixit.feature.auth.presentation.AuthActivity;
import com.fixit.feature.customer.presentation.CustomerActivity;
import com.fixit.feature.notification.domain.usecase.RegisterDeviceTokenUseCase;
import com.fixit.feature.worker.presentation.WorkerActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFcmService";
    private static final String CHANNEL_ID = "booking_updates_channel";
    private static final String CHANNEL_NAME = "Thông báo đơn hàng";

    @Inject
    RegisterDeviceTokenUseCase registerDeviceTokenUseCase;

    @Inject
    SessionStorage sessionStorage;

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed FCM Token: " + token);

        // Nếu người dùng đã đăng nhập, tự động đồng bộ hóa token lên Backend
        if (sessionStorage.getAccessToken() != null && !sessionStorage.getAccessToken().trim().isEmpty()) {
            registerDeviceTokenUseCase.execute(token, "Android", result -> {
                if (result.isSuccess()) {
                    Log.d(TAG, "Đã đồng bộ hóa token mới lên Backend thành công.");
                } else {
                    Log.e(TAG, "Đồng bộ hóa token mới thất bại: " + result.getError().getMessage());
                }
            });
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Received message from: " + remoteMessage.getFrom());

        String title = null;
        String body = null;

        // 1. Lấy thông tin từ phần notification payload (nếu có)
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // 2. Lấy thông tin từ phần data payload (luôn ưu tiên dữ liệu tùy chỉnh)
        Map<String, String> data = remoteMessage.getData();
        if (data.containsKey("title")) {
            title = data.get("title");
        }
        if (data.containsKey("body")) {
            body = data.get("body");
        }

        if (title == null) {
            title = "Thông báo mới từ FixIt";
        }
        if (body == null) {
            body = "Bạn có một bản cập nhật mới từ ứng dụng.";
        }

        Log.d(TAG, "Notification Title: " + title + ", Body: " + body);

        // 3. Hiển thị thông báo trên Notification Drawer
        sendNotification(title, body, data);

        // 4. Phát quảng bá Local Broadcast để các màn hình đang mở (Foreground) có thể tự động tải lại dữ liệu
        sendBookingUpdateBroadcast(data);
    }

    private void sendNotification(String title, String body, Map<String, String> data) {
        // Lựa chọn Activity đích dựa trên Role của người dùng hiện tại
        Class<?> destinationActivity = AuthActivity.class;
        String role = sessionStorage.getUserRole();
        if (role != null) {
            if ("CUSTOMER".equalsIgnoreCase(role)) {
                destinationActivity = CustomerActivity.class;
            } else if ("WORKER".equalsIgnoreCase(role)) {
                destinationActivity = WorkerActivity.class;
            }
        }

        Intent intent = new Intent(this, destinationActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Truyền các tham số booking nếu có để UI có thể xử lý điều hướng sâu
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }

        int pendingIntentFlags = PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 
                (int) System.currentTimeMillis(), 
                intent, 
                pendingIntentFlags
        );

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            // Android 8.0 trở lên yêu cầu Notification Channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationManager.createNotificationChannel(channel);
            }

            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }

    private void sendBookingUpdateBroadcast(Map<String, String> data) {
        Intent intent = new Intent("com.fixit.BOOKING_UPDATE");
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        sendBroadcast(intent);
    }
}
