package com.fixit.core.socket;

// TODO: Uncomment khi tích hợp Socket.IO
// Cần thêm dependency vào build.gradle trước:
//   implementation 'io.socket:socket.io-client:2.1.0'
//
// import android.util.Log;
// import com.fixit.utils.Constants;
// import io.socket.client.IO;
// import io.socket.client.Socket;
// import java.net.URISyntaxException;
//
// /**
//  * SocketManager – Singleton quản lý kết nối Socket.IO với Backend.
//  * Dùng cho 2 mục đích:
//  *   1. Real-time Chat (Worker <-> Customer)
//  *   2. Gửi toạ độ GPS lên Backend → Backend lưu vào Redis (GEOADD)
//  */
// public class SocketManager {
//     private static final String TAG = "SocketManager";
//     private static SocketManager instance;
//     private Socket socket;
//
//     private SocketManager() {}
//
//     public static synchronized SocketManager getInstance() {
//         if (instance == null) {
//             instance = new SocketManager();
//         }
//         return instance;
//     }
//
//     public void connect(String accessToken) {
//         try {
//             IO.Options options = new IO.Options();
//             options.extraHeaders = java.util.Collections.singletonMap(
//                 "Authorization", java.util.Collections.singletonList("Bearer " + accessToken)
//             );
//             socket = IO.socket(Constants.BASE_URL, options);
//             socket.connect();
//             Log.d(TAG, "Socket connected");
//         } catch (URISyntaxException e) {
//             Log.e(TAG, "Socket connection error: " + e.getMessage());
//         }
//     }
//
//     /**
//      * Gửi toạ độ GPS lên backend (Backend sẽ lưu vào Redis để Matching)
//      * Event: "update_location" | Payload: { latitude, longitude }
//      */
//     public void sendLocation(double latitude, double longitude) {
//         if (socket != null && socket.connected()) {
//             org.json.JSONObject payload = new org.json.JSONObject();
//             try {
//                 payload.put("latitude", latitude);
//                 payload.put("longitude", longitude);
//                 socket.emit("update_location", payload);
//             } catch (org.json.JSONException e) {
//                 Log.e(TAG, "sendLocation error: " + e.getMessage());
//             }
//         }
//     }
//
//     public Socket getSocket() {
//         return socket;
//     }
//
//     public void disconnect() {
//         if (socket != null) {
//             socket.disconnect();
//             Log.d(TAG, "Socket disconnected");
//         }
//     }
// }

/**
 * Placeholder class - SocketManager sẽ được kích hoạt khi tích hợp Socket.IO.
 * Xem comment phía trên để biết cách bật lại.
 */
public class SocketManager {
    private static SocketManager instance;

    private SocketManager() {}

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    // Placeholder methods - sẽ có implementation khi Socket.IO được thêm vào
    public void connect(String accessToken) { /* TODO */ }
    public void sendLocation(double latitude, double longitude) { /* TODO */ }
    public void disconnect() { /* TODO */ }
}
