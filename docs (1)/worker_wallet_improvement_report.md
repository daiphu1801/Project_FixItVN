# Báo Cáo Cải Tiến Luồng Hoạt Động & Giao Diện Ví Thợ (Worker Wallet)

Tài liệu này báo cáo chi tiết về việc tái cấu trúc luồng hoạt động, cải tiến thiết kế giao diện (UI/UX) và triển khai các cơ chế nghiệp vụ mới cho tính năng Ví Thợ trên ứng dụng **FixIt**.

---

## I. Tổng Quan Yêu Cầu Cải Tiến

Dựa trên việc đối chiếu với các ứng dụng hàng đầu trong ngành (ví dụ: Vua Thợ) và phản hồi từ trải nghiệm người dùng, luồng ví cũ gặp phải các hạn chế sau:
1. **Thiếu nút hành động trực quan**: Nút "Nạp tiền" chỉ hiển thị khi ví ghi nợ có nợ phát sinh. Người dùng mới hoặc người dùng không có nợ sẽ không tìm thấy cách nạp tiền vào ví.
2. **Thiếu số liệu thống kê thu nhập**: Người dùng không theo dõi được hiệu quả công việc trực tiếp trên màn hình ví của mình.
3. **Cơ chế kiểm tra nạp tiền thủ công**: Khi thợ quét mã VietQR để nạp tiền qua cổng thanh toán (ví dụ: SePay), ứng dụng không tự động kiểm tra trạng thái giao dịch mà bắt buộc thợ phải thoát ra hoặc tải lại thủ công.
4. **Liên kết dữ liệu tạm giữ cứng**: Nút "Xem khiếu nại" từ thẻ Ví tạm giữ bị gán cứng (`ORD004`) thay vì liên kết trực tiếp tới đơn hàng thực tế đang bị tạm giữ.

---

## II. Các Cải Tiến Đã Thực Hiện

### 1. Tái Cấu Trúc Giao Diện (UI/UX)
- **Hàng Quick Actions (Hành động nhanh)**: Thiết kế một hàng nút ngang nổi bật nằm ngay bên dưới các thẻ ví chính, bao gồm 3 tác vụ cốt lõi:
  - **Nạp tiền**: Cho phép thợ chủ động nạp tiền bất cứ lúc nào, hỗ trợ cả trường hợp chưa phát sinh nợ phí.
  - **Rút tiền**: Rút tiền từ Ví khả dụng về ngân hàng liên kết.
  - **Liên kết**: Quản lý thông tin và tài khoản ngân hàng nhận tiền.
- **Thống kê thu nhập**: Thêm một thẻ hiển thị thông số doanh thu của thợ bao gồm **Doanh thu tuần này** và **Doanh thu tháng này**. Dữ liệu được tính toán thời gian thực từ phía Server thông qua các giao dịch và đơn hàng đã hoàn thành.
- **Swipe-to-Refresh (Kéo để làm mới)**: Bao bọc toàn bộ giao diện bằng `SwipeRefreshLayout` để thợ dễ dàng vuốt làm mới số dư ví và lịch sử giao dịch.

### 2. Tự Động Hóa & Liên Kết Dữ Liệu Thực Tế
- **Cơ chế Polling Nạp tiền tự động**: Ở màn hình hiển thị QR nạp tiền, ứng dụng tự động kích hoạt tiến trình Polling với tần suất **10 giây/lần** để kiểm tra trạng thái thanh toán từ Server. Khi thợ chuyển khoản thành công và webhook của bên thứ 3 (ví dụ: SePay) báo về Server, màn hình ví sẽ lập tức nhận biết trạng thái `SUCCESS` và tự động chuyển về màn hình chính hoặc thông báo thành công mà thợ không cần thao tác thêm.
- **Liên Kết Đơn Hàng Tạm Giữ Thực Tế**: Ứng dụng tự động duyệt danh sách giao dịch tạm giữ (`Holding`), tìm ra mã đơn hàng (`bookingId`) bị tạm giữ gần nhất để truyền sang màn hình chi tiết khiếu nại (`workerComplaintFragment`) khi thợ nhấn "Xem khiếu nại".

---

## III. Chi Tiết Tri Nhập Code (Implementation Details)

### 1. Spring Boot Backend

#### DTO & Service Mapping
- Cập nhật **`WorkerWalletResponse.java`** để bổ sung hai thuộc tính thống kê:
  ```java
  private BigDecimal incomeThisWeek;
  private BigDecimal incomeThisMonth;
  ```
- Cập nhật **`WorkerWalletServiceImpl.java`**:
  - Tích hợp repository chuyên biệt `WorkerHomeQueryRepository` để tái sử dụng câu truy vấn tính toán thống kê hiệu suất thực tế của thợ (`findStatsOverview(workerId)`).
  - Trích xuất tổng thu nhập tuần và tháng từ kết quả trả về của repository và map trực tiếp vào DTO gửi về ứng dụng Android.

### 2. Android Mobile Client

#### Thư viện & Cấu hình Gradle
- Thêm dependency của SwipeRefreshLayout vào `app/build.gradle`:
  ```groovy
  implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
  ```

#### Domain & Data Layer (DTO / Models / Repository)
- **`WorkerWalletResponse.java`**: Khai báo các trường tương ứng để đón nhận dữ liệu doanh số tuần/tháng từ API.
- **`WalletBalance.java`** & **`WalletTransaction.java`**:
  - Thêm các trường lưu trữ thống kê thu nhập.
  - Thêm thuộc tính `bookingId` vào giao dịch ví để phục vụ điều hướng đơn hàng bị giữ tiền.
- **`WorkerWalletRepository.java`** & **`WorkerWalletRepositoryImpl.java`**:
  - Khai báo và cài đặt phương thức `getDepositDetail(transactionId)` gọi API lấy thông tin chi tiết của giao dịch nạp tiền.
  - Map chính xác trường `bookingId` từ DTO API trả về vào domain model của ứng dụng.

#### Presentation Layer (ViewModels / Fragments / Layouts)
- **`WorkerWalletViewModel.java`**:
  - Khai báo LiveData `incomeThisWeek`, `incomeThisMonth` và `heldBookingId`.
  - Khi lấy số dư ví, đồng thời tải lịch sử giao dịch tạm giữ để tìm ra đơn hàng có tranh chấp thực tế và gán vào `heldBookingId`.
- **`WorkerDepositViewModel.java`**:
  - Triển khai cơ chế Polling sử dụng `android.os.Handler` kết hợp với `GetDepositDetailUseCase`.
  - Polling tự động chạy khi giao dịch nạp tiền được khởi tạo với trạng thái `PENDING`, và tự động hủy bỏ khi nhận được trạng thái cuối (`SUCCESS`, `FAILED`, `CANCELLED`) hoặc khi ViewModel bị hủy.
- **`fragment_worker_wallet.xml`**:
  - Thiết kế lại cấu trúc layout, thêm các thành phần Quick Action, thẻ thống kê thu nhập và bọc toàn bộ bằng `SwipeRefreshLayout`.
- **`WorkerWalletFragment.java`**:
  - Lắng nghe hành vi vuốt làm mới của thợ để cập nhật nhanh số dư.
  - Kết nối sự kiện click của các nút hành động nhanh với các UseCase điều hướng màn hình.
  - Thực hiện lấy mã đơn hàng thực tế từ `heldBookingId` để truyền sang fragment khiếu nại.

---

## IV. Kiểm Thử Biên Dịch (Compilation Verification)

Dự án Android đã được biên dịch cục bộ bằng Gradle:
- **Lệnh thực thi**: `.\gradlew.bat compileDebugSources`
- **Kết quả**: **BUILD SUCCESSFUL**
- **Trạng thái**: Không có lỗi cú pháp hoặc lỗi liên kết, các dependency và class Dagger Hilt hoạt động ổn định.

---
*Báo cáo được hoàn thành vào ngày 12 tháng 06 năm 2026 bởi Antigravity.*
