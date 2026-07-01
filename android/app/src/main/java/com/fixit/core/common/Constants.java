package com.fixit.core.common;

public class Constants {
    // Network
    // Dùng 10.0.2.2 cho máy ảo, hoặc IP máy tính cho máy thật/LDPlayer
    // public static final String BASE_URL = "http://172.16.1.2:8080/";
    //public static final String BASE_URL = "http://10.0.2.2:8080/";


    public static final String BASE_URL = "https://finley-unvituperated-saccharinely.ngrok-free.dev/";

    // DEV ONLY: dùng khi backend chưa lấy worker từ JWT thật.
    // Đổi thành worker_id thật trong bảng workers.
    public static final String DEBUG_WORKER_ID = "11111111-1111-1111-1111-111111111111";

    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;

    // Shared Preferences
    public static final String PREF_NAME = "fixit_prefs";
    public static final String PREF_ACCESS_TOKEN = "access_token";
    public static final String PREF_REFRESH_TOKEN = "refresh_token";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_ROLE = "user_role"; // "CUSTOMER" | "WORKER"
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_USER_PHONE = "user_phone";
    public static final String PREF_IS_ONLINE = "worker_is_online";

    // User Roles
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_WORKER = "WORKER";

    // GPS Tracking
    public static final long GPS_UPDATE_INTERVAL_MS = 5000L; // 5 giây gửi 1 lần lên backend (Redis)
    public static final float GPS_MIN_DISTANCE_M = 10f; // Tối thiểu di chuyển 10m mới gửi

    // Proof of Work (Upload ảnh trước/sau sửa chữa)
    public static final int MAX_IMAGE_SIZE_MB = 5;
    public static final String UPLOAD_FOLDER_PROOF = "proof-of-work";

    // Booking timeout
    public static final long BOOKING_TIMEOUT_MS = 3 * 60 * 1000L; // 3 phút timeout nhận việc

    // Wallet (bảo hiểm 96 giờ)
    public static final long ESCROW_HOLD_HOURS = 96;
}
