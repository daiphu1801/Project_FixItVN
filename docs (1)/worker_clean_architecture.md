# Worker Clean Architecture Status

Tài liệu này ghi lại trạng thái refactor Java cho nhóm feature Worker.

## 1. Phạm vi đã refactor

Các core flows đã được tách theo `presentation/domain/data/di`:

- `feature.worker.availability`: trạng thái online/offline dùng chung giữa Home và Job.
- `feature.worker.home`: danh sách lịch hẹn hôm nay.
- `feature.worker.job`: thông tin tổng quan nhận việc, khu vực, rating, debt gate.
- `feature.worker.orders`: danh sách đơn, lọc tab, trạng thái job, phụ phí, invoice/VietQR.
- `feature.worker.wallet`: số dư ví và lịch sử giao dịch.
- `feature.worker.profile`: logout dùng lại `auth.domain.usecase.LogoutUseCase`.

## 2. Luồng dependency chuẩn

```text
Fragment
  -> ViewModel
  -> UseCase
  -> Domain Repository Interface
  -> Data Repository Implementation
  -> Mock/API/Cache
```

Quy tắc:

- Fragment không chứa mock list lớn.
- ViewModel không gọi Retrofit, DTO hoặc repository implementation trực tiếp.
- Use case là API nghiệp vụ mà ViewModel gọi.
- Repository implementation là nơi duy nhất chứa mock data trong giai đoạn chưa có backend thật.
- Hilt module của từng feature bind domain repository sang implementation.

## 3. Mapping hiện tại

| Flow | Presentation | Domain | Data |
| --- | --- | --- | --- |
| Online/offline | `WorkerStatusViewModel` | `WorkerAvailabilityRepository`, `GetWorkerAvailabilityUseCase`, `ToggleWorkerAvailabilityUseCase` | `WorkerAvailabilityRepositoryImpl` |
| Home appointments | `WorkerHomeFragment`, `WorkerHomeViewModel` | `WorkerHomeRepository`, `GetTodayAppointmentsUseCase` | `WorkerHomeRepositoryImpl` |
| Job summary | `WorkerJobFragment`, `WorkerJobViewModel` | `WorkerJobRepository`, `GetWorkerJobSummaryUseCase` | `WorkerJobRepositoryImpl` |
| Orders | `WorkerOrdersViewModel`, orders fragments | `WorkerOrdersRepository`, order use cases, `WorkerOrder`, `JobStatus`, `ExtraCostItem` | `WorkerOrdersRepositoryImpl` |
| Wallet | `WorkerWalletFragment`, `WorkerWalletViewModel` | `WorkerWalletRepository`, wallet use cases, `WalletBalance`, `WalletTransaction` | `WorkerWalletRepositoryImpl` |
| Logout | `WorkerProfileViewModel` | `LogoutUseCase` from auth domain | `AuthRepositoryImpl` clears `SessionStorage` |

## 4. Những phần chưa refactor sâu

- `worker.kyc`: còn UI/skeleton; chưa có VNPT eKYC/API repository/usecase.
- `worker.chat`: còn UI/skeleton; chưa có socket/message repository/usecase.
- `worker.stats`: vẫn còn mock chart/appointment trong presentation.
- `worker.profile` edit/change password/specialization: vẫn còn UI/mock, chưa có repository/usecase riêng.
- API thật cho orders/wallet/job/home chưa tích hợp; data layer hiện vẫn dùng mock repository.

## 5. Build verification

Lệnh kiểm tra:

```powershell
cd android
.\gradlew.bat --no-daemon :app:assembleDebug
```

Trạng thái sau refactor worker:

```text
BUILD SUCCESSFUL
```

## 6. Next implementation order

1. Refactor `worker.stats` để mock chart và appointments đi qua repository/usecase.
2. Refactor `worker.profile` edit/change password/specialization.
3. Tích hợp API wallet thật: balance, transactions, deposit, withdraw, bank accounts.
4. Tích hợp API order lifecycle thật.
5. Thiết kế luồng `worker.kyc` theo VNPT eKYC/backend contract.
6. Tách chat sang socket/message repository khi backend realtime sẵn sàng.
