# Hướng dẫn chạy dự án FixIt VN local (Android)

Tài liệu này hướng dẫn mở, build và chạy module Android của FixIt VN trên máy local.

## 1. Yêu cầu môi trường

- Android Studio bản mới.
- Android SDK theo `compileSdk` trong `android/app/build.gradle`.
- JDK 21 tại `C:/Program Files/Java/jdk-21`, hoặc cập nhật lại `org.gradle.java.home` trong `android/gradle.properties` cho đúng máy local.
- Kết nối Internet lần đầu để Gradle tải wrapper/dependencies.

## 2. Mở project

1. Mở Android Studio.
2. Chọn `File > Open...`.
3. Mở thư mục:

```text
F:\Project_personal\FixItVN\android
```

Không mở thư mục gốc `FixItVN` nếu Android Studio yêu cầu project Gradle Android trực tiếp.

## 3. Build bằng terminal

Trong PowerShell:

```powershell
cd F:\Project_personal\FixItVN\android
.\gradlew.bat --no-daemon :app:assembleDebug
```

.\gradlew.bat --stop
.\gradlew.bat --no-daemon :app:assembleDebug

Kết quả mong muốn:

```text
BUILD SUCCESSFUL
```

## 4. Cấu hình backend local

File cấu hình base URL hiện nằm tại:

```text
android/app/src/main/java/com/fixit/core/common/Constants.java
```

Giá trị mặc định:

```java
public static final String BASE_URL = "http://10.0.2.2:8080/";
```

Ghi chú:

- Dùng `http://10.0.2.2:8080/` khi backend chạy trên máy host và app chạy trong Android Emulator.
- Dùng IP LAN như `http://192.168.1.x:8080/` khi chạy trên thiết bị thật.
- Không dùng `localhost` hoặc `127.0.0.1` trong app Android nếu backend chạy trên máy tính, vì khi đó localhost trỏ về chính thiết bị/emulator.

## 5. Package quan trọng khi debug

- Auth UI/ViewModel: `com.fixit.feature.auth.presentation`
- Auth data/API: `com.fixit.feature.auth.data`
- Worker Activity: `com.fixit.feature.worker.presentation.WorkerActivity`
- Worker orders: `com.fixit.feature.worker.orders`
- Worker wallet: `com.fixit.feature.worker.wallet`
- Worker online/offline: `com.fixit.feature.worker.availability`
- Network foundation: `com.fixit.core.network`
- Session/token: `com.fixit.core.storage`
- Constants/build config runtime: `com.fixit.core.common.Constants`

## 6. Lỗi thường gặp

- Lỗi `jlink executable ... does not exist`: kiểm tra `org.gradle.java.home` trong `android/gradle.properties` đang trỏ đúng JDK thật.
- Lỗi Hilt binding: chạy lại `.\gradlew.bat --no-daemon :app:assembleDebug` để xem class nào thiếu binding/module.
- Lỗi không gọi được API: kiểm tra `BASE_URL`, network permission, emulator/device network và backend port.
- Lỗi class navigation không resolve: kiểm tra `AndroidManifest.xml` và các file `nav_*.xml` đang trỏ package `com.fixit.feature.*`, không dùng package cũ `com.fixit.ui.*`.

pass db: FixIt11020.
