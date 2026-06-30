# FixIt VN - Architecture & Progress Report

## 1. Tổng quan

FixIt VN là ứng dụng Android Java phục vụ hai vai trò chính:
- `CUSTOMER`: khách hàng đặt dịch vụ sửa chữa.
- `WORKER`: thợ nhận việc, xử lý đơn, quản lý ví và hồ sơ.

Kiến trúc Android hiện tại dùng:
- Java + XML ViewBinding.
- MVVM ở lớp presentation.
- Clean Architecture theo feature: `presentation/domain/data/di`.
- Hilt cho Dependency Injection.
- Retrofit + OkHttp cho network foundation.
- SharedPreferences qua `SessionStorage` cho session/token.

Build check hiện tại:

```powershell
cd android
.\gradlew.bat --no-daemon :app:assembleDebug
```

## 2. Cấu trúc package hiện tại

Code Java chính nằm tại:

```text
android/app/src/main/java/com/fixit/
├── FixitApp.java
├── core/
│   ├── common/       # Constants, Result, AppError, ResultCallback
│   ├── di/           # App-level providers
│   ├── network/      # Retrofit, OkHttp, AuthInterceptor
│   ├── storage/      # SessionStorage, SharedPrefsSessionStorage, TokenUtils legacy
│   ├── ui/           # BaseActivity, BaseFragment, BaseViewModel, UiState
│   ├── location/     # LocationTrackingService
│   ├── socket/       # SocketManager placeholder
│   ├── fcm/          # Firebase Cloud Messaging integration future
│   ├── payment/      # Payment/VietQR/SePay integration future
│   └── ekyc/         # VNPT eKYC integration future
└── feature/
    ├── auth/
    ├── customer/
    ├── notification/
    └── worker/
```

Quy ước mỗi feature:

```text
feature/<name>/
├── presentation/         # Activity, Fragment, ViewModel, UI state/event
├── domain/
│   ├── model/            # Entity/model dùng trong app
│   ├── repository/       # Interface repository
│   └── usecase/          # Business use cases
├── data/
│   ├── remote/api/       # Retrofit API interfaces
│   ├── remote/dto/       # DTO request/response
│   ├── remote/mapper/    # DTO <-> domain mapper
│   └── repository/       # Repository implementation
└── di/                   # Hilt module bind/provide cho feature
```

Nguyên tắc dependency:
- `presentation` chỉ gọi `domain/usecase`.
- `domain` không phụ thuộc Android UI, Retrofit hoặc DTO.
- `data` implement domain repository và là nơi chứa mock/API/cache.
- `core.network` không phụ thuộc feature cụ thể, chỉ dùng abstraction dùng chung như `SessionStorage`.

## 3. Core foundation đã hoàn thành

- `core.common.Result<T>`, `AppError`, `ResultCallback<T>` chuẩn hóa success/error callback.
- `core.storage.SessionStorage` và `SharedPrefsSessionStorage` quản lý token/session.
- `core.network.AuthInterceptor` tự gắn header `Authorization: Bearer <token>` khi có access token.
- `core.network.NetworkModule` provide `Retrofit`, `OkHttpClient`, logging interceptor.
- `core.di.AppModule` provide `SharedPreferences` và `SessionStorage`.
- `TokenUtils` vẫn tồn tại để tương thích ngược, nhưng UI mới không gọi trực tiếp.

Lưu ý hiện tại:
- `org.gradle.java.home` đang pin về `C:/Program Files/Java/jdk-21` trong `android/gradle.properties`.
- Logging interceptor vẫn đang bật `BODY`; khi `BuildConfig.DEBUG` ổn định có thể đổi sang debug-only.

## 4. Auth đã refactor theo Clean Architecture

Package chính:

```text
feature/auth/
├── presentation/     # AuthActivity, Login/Register/Forgot fragments, AuthViewModel
├── domain/           # User, Session, UserRole, AuthRepository, Login/Register/Logout use cases
├── data/             # AuthApi, DTO, AuthMapper, AuthRepositoryImpl
└── di/               # AuthModule
```

Trạng thái hiện tại:
- `AuthViewModel` chỉ phụ thuộc use case.
- `LoginFragment` và `RegisterFragment` không gọi DTO, Retrofit, `TokenUtils` hoặc `SharedPreferences`.
- Mock login đã chuyển xuống `AuthRepositoryImpl`.
- Register vẫn gọi `AuthApi.register`.
- Logout clear session qua `SessionStorage`.

## 5. Worker core flows đã refactor

Các flow worker đã đi theo pattern `presentation/domain/data/di`:

```text
feature/worker/
├── availability/     # Online/offline state dùng chung Home + Job
├── home/             # Appointments hôm nay
├── job/              # Worker job summary, debt gate
├── orders/           # Orders list/detail/status/extra cost/payment QR
├── wallet/           # Wallet balance + transactions
├── profile/          # Logout đã dùng Auth LogoutUseCase
├── kyc/              # Skeleton/UI hiện tại
├── chat/             # Skeleton/UI hiện tại
└── stats/            # UI/mock hiện tại
```

Chi tiết:
- `worker.availability`: `WorkerStatusViewModel` gọi `GetWorkerAvailabilityUseCase` và `ToggleWorkerAvailabilityUseCase`; repository giữ trạng thái online/offline.
- `worker.orders`: mock orders, filter, job status, extra costs và VietQR URL nằm trong `WorkerOrdersRepositoryImpl`; ViewModel chỉ expose LiveData cho UI hiện tại.
- `worker.wallet`: mock balance/transactions nằm trong `WorkerWalletRepositoryImpl`.
- `worker.job`: mock summary/debt nằm trong `WorkerJobRepositoryImpl`.
- `worker.home`: mock appointments chuyển khỏi Fragment xuống `WorkerHomeRepositoryImpl`.
- `worker.profile`: logout dùng `feature.auth.domain.usecase.LogoutUseCase`, navigation về `AuthActivity` chỉ chạy từ observer.

Build sau refactor worker:

```text
BUILD SUCCESSFUL
```

## 6. Customer và Notification

`feature.customer.*` và `feature.notification` đã có skeleton package:
- `presentation`
- `domain/model`
- `domain/repository`
- `domain/usecase`
- `data/remote/api`
- `data/remote/dto`
- `data/remote/mapper`
- `data/repository`
- `di`

Các phần này chưa refactor business sâu vì hiện chưa có logic/API rõ như auth và worker core flows.

## 7. Resource và navigation convention

Resource Android vẫn giữ flat structure theo rule của Android:
- Layout đặt prefix theo role/feature, ví dụ `fragment_worker_home.xml`, `fragment_worker_orders.xml`, `fragment_auth_role.xml`.
- Navigation graph:
  - `res/navigation/nav_auth.xml`
  - `res/navigation/nav_customer.xml`
  - `res/navigation/nav_worker.xml`
- Activity entry:
  - `feature.auth.presentation.AuthActivity`
  - `feature.customer.presentation.CustomerActivity`
  - `feature.worker.presentation.WorkerActivity`

Không chia thư mục vật lý trong `res/layout` vì Android resource compiler không hỗ trợ nested layout folder theo cách thông thường.

## 8. Những phần còn chưa refactor sâu

- `worker.kyc`: mới là UI/skeleton; chưa tách VNPT eKYC API/client/usecase.
- `worker.chat`: chưa có message repository/usecase/socket flow thật.
- `worker.stats`: vẫn còn mock chart/appointment trong presentation.
- `worker.profile`: các màn edit profile, specialization, change password vẫn còn logic UI/mock.
- `customer.*`: chưa Clean Architecture sâu như auth/worker.
- `feature.notification`: mới skeleton, chưa có in-app notification repository.
- Một số text/comment trong code đang bị mojibake encoding; chưa xử lý trong các vòng refactor để tránh thay đổi ngoài phạm vi behavior.

## 9. Next steps khuyến nghị

1. Refactor `worker.stats` để chuyển mock chart/appointment sang repository/usecase.
2. Refactor `worker.profile` edit/change password/specialization theo domain/data.
3. Thiết kế API contract thật cho `worker.kyc`, `worker.chat`, `worker.wallet`.
4. Refactor `customer.booking`, `customer.history`, `customer.profile` khi có user flow/API rõ.
5. Chuẩn hóa logging debug-only trong `NetworkModule`.
6. Xử lý encoding mojibake theo một PR riêng, chỉ sửa text/comment/UI copy.
