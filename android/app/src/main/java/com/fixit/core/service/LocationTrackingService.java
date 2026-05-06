package com.fixit.core.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.fixit.core.socket.SocketManager;
import com.fixit.utils.Constants;

/**
 * LocationTrackingService – Android Foreground Service.
 * Mục đích:
 *   - Bắt tọa độ GPS liên tục mỗi 5 giây khi Thợ bật chế độ "Online".
 *   - Gửi tọa độ (Lat/Lng) qua SocketManager → Backend → Redis (GEOADD).
 *   - Chạy ở Foreground để hệ điều hành không tắt khi app bị minimize.
 */
public class LocationTrackingService extends Service {

    private static final String CHANNEL_ID   = "location_tracking_channel";
    private static final int    NOTIFICATION_ID = 1001;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (result == null) return;
                double lat = result.getLastLocation().getLatitude();
                double lng = result.getLastLocation().getLongitude();
                // Gửi toạ độ lên Backend (Redis) qua Socket.IO
                SocketManager.getInstance().sendLocation(lat, lng);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, buildNotification());
        startLocationUpdates();
        return START_STICKY; // Tự khởi động lại nếu bị hệ điều hành tắt
    }

    private void startLocationUpdates() {
        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, Constants.GPS_UPDATE_INTERVAL_MS)
                .setMinUpdateDistanceMeters(Constants.GPS_MIN_DISTANCE_M)
                .build();
        try {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            stopSelf();
        }
    }

    private Notification buildNotification() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Theo dõi vị trí",
                NotificationManager.IMPORTANCE_LOW
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("FixIt VN")
                .setContentText("Đang chia sẻ vị trí – Bạn đang trong chế độ Sẵn sàng nhận việc")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
