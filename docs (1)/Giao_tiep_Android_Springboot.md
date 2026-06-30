# Giao tiếp Android Clean Architecture với Spring Boot Backend

Tài liệu này mô tả cách app Android Java của FixIt VN giao tiếp với Spring Boot backend sau khi refactor theo feature Clean Architecture.

## 1. Data flow chuẩn

Ví dụ flow rút tiền ví thợ:

```text
WorkerWalletFragment
        |
        v
WorkerWalletViewModel
        |
        v
GetWalletBalanceUseCase / GetWalletTransactionsUseCase
        |
        v
WorkerWalletRepository interface
        |
        v
WorkerWalletRepositoryImpl
        |
        v
WorkerWalletApi retrofit interface
        |
        v
Spring Boot REST API
```

Quy tắc:
- Fragment chỉ xử lý UI event và observe LiveData/state.
- ViewModel chỉ gọi use case.
- Use case gọi domain repository interface.
- Data repository quyết định lấy data từ mock, API, cache hoặc database.
- DTO chỉ nằm trong `data/remote/dto`.
- Mapper chuyển DTO sang domain model trước khi trả lên ViewModel.

## 2. Network foundation

Network dùng chung nằm trong:

```text
com.fixit.core.network
```

Các thành phần chính:
- `NetworkModule`: provide `Retrofit`, `OkHttpClient`, `HttpLoggingInterceptor`.
- `AuthInterceptor`: đọc access token từ `SessionStorage` và gắn header:

```text
Authorization: Bearer <access_token>
```

Session/token nằm trong:

```text
com.fixit.core.storage
```

`TokenUtils` chỉ còn vai trò tương thích ngược. Code mới nên dùng `SessionStorage` hoặc repository/usecase tương ứng.

## 3. Vị trí API theo feature

Mỗi feature tự sở hữu API interface của mình:

```text
feature/auth/data/remote/api/AuthApi.java
feature/worker/wallet/data/remote/api/WorkerWalletApi.java      # thêm khi tích hợp API thật
feature/worker/orders/data/remote/api/WorkerOrdersApi.java      # thêm khi tích hợp API thật
feature/worker/kyc/data/remote/api/WorkerKycApi.java            # thêm khi tích hợp VNPT/backend
feature/customer/booking/data/remote/api/CustomerBookingApi.java # thêm khi tích hợp booking
```

Không đặt API feature vào `core.network`. `core.network` chỉ cấu hình hạ tầng dùng chung.

## 4. Ví dụ API contract Android

Ví dụ cho ví thợ sau này:

```java
public interface WorkerWalletApi {
    @GET("/api/v1/workers/me/wallet")
    Call<WalletResponse> getWallet();

    @GET("/api/v1/workers/me/wallet/transactions")
    Call<List<WalletTransactionResponse>> getTransactions();

    @POST("/api/v1/workers/me/wallet/withdraw")
    Call<BaseResponse> requestWithdraw(@Body WithdrawRequest request);
}
```

DTO nằm trong:

```text
feature/worker/wallet/data/remote/dto
```

Mapper nằm trong:

```text
feature/worker/wallet/data/remote/mapper
```

Domain model nằm trong:

```text
feature/worker/wallet/domain/model
```

## 5. Trạng thái hiện tại

Đã tích hợp theo kiến trúc mới:
- `auth`: có `AuthApi`, DTO, mapper, repository impl, use cases.
- `worker.orders`: repository/usecase đã sẵn sàng; hiện data vẫn là mock trong `WorkerOrdersRepositoryImpl`.
- `worker.wallet`: repository/usecase đã sẵn sàng; hiện data vẫn là mock trong `WorkerWalletRepositoryImpl`.
- `worker.job`: repository/usecase đã sẵn sàng; hiện data vẫn là mock trong `WorkerJobRepositoryImpl`.
- `worker.home`: repository/usecase đã sẵn sàng; hiện data vẫn là mock trong `WorkerHomeRepositoryImpl`.
- `worker.availability`: trạng thái online/offline đã tách repository/usecase.

Chưa tích hợp API thật:
- Worker wallet withdraw/deposit/bank account.
- Worker orders lifecycle API.
- Worker KYC/VNPT eKYC.
- Chat/socket.
- Customer booking/history/profile.

## 6. Quy trình thay mock bằng API thật

Khi backend có endpoint ổn định:

1. Tạo `FeatureApi` trong `feature/<feature>/data/remote/api`.
2. Tạo request/response DTO trong `data/remote/dto`.
3. Tạo mapper DTO sang domain model trong `data/remote/mapper`.
4. Inject API vào `RepositoryImpl` qua Hilt module của feature.
5. Thay mock trong `RepositoryImpl` bằng Retrofit call.
6. Giữ nguyên ViewModel/Fragment nếu domain contract không đổi.
7. Chạy:

```powershell
cd android
.\gradlew.bat --no-daemon :app:assembleDebug
```

## 7. Mapping nhanh với backend endpoints trong docs nghiệp vụ

- Auth:
  - `POST /auth/login`
  - `POST /auth/register`
  - `POST /auth/logout`
  - `POST /auth/refresh-token`
- Worker:
  - `PATCH /workers/me/status`
  - `PATCH /workers/me/location`
  - `GET /workers/me/schedule`
  - `GET /workers/me/history`
  - `GET /workers/me/stats`
  - `GET /workers/me/wallet`
  - `GET /workers/me/wallet/transactions`
  - `POST /workers/me/wallet/deposit`
  - `POST /workers/me/wallet/withdraw`
  - `POST /workers/kyc`
  - `GET /workers/kyc/status`
- Customer:
  - booking/search/history/profile endpoints sẽ map vào `feature.customer.*`.
