# Kiến trúc và Thiết kế Giao diện (UI) - App Thợ (Worker)

Tài liệu này tổng hợp lại các quyết định thiết kế, cấu trúc file XML và nguyên lý triển khai giao diện cho ứng dụng FixIt VN (Phần dành cho Thợ), dựa trên chuẩn thiết kế **Stitch Design System** và **Kiến trúc Android hiện đại**.

---

## 1. Triết lý Thiết kế (Design System)
Ứng dụng tuân thủ nghiêm ngặt các quy chuẩn hình ảnh để tạo ra trải nghiệm "Premium" và thân thiện với người dùng (Thợ):
*   **Màu chủ đạo (Primary Color):** `#42c2ff` (Xanh dương tươi sáng, tạo cảm giác tin cậy, năng động).
*   **Màu nền (Background):** `#F5F7FA` (Xám rất nhạt) làm nổi bật các khối thẻ (Card) màu trắng bên trên, tạo chiều sâu (3D) cho giao diện.
*   **Bo góc (Corner Radius):**
    *   `16dp` cho các khối lớn (CardView tổng quan, Bottom Sheet).
    *   `12dp` cho các thẻ nhỏ, hình ảnh.
    *   `8dp` cho các Nút bấm (Button).
*   **Đổ bóng (Elevation):** Sử dụng rất nhẹ (1dp - 4dp) để phân tách các lớp (Layers) mà vẫn giữ được phong cách thiết kế "Clean" (Sạch sẽ, tối giản).

---

## 2. Danh sách các file Layout đã triển khai
Thay vì viết một file XML khổng lồ, toàn bộ màn hình được "chẻ nhỏ" thành các Component con (Mô hình Lego). Tổng cộng có 11 file XML chia làm 3 nhóm chính:

### Nhóm 1: Các màn hình chính (Fragments)
Là "cái khung" (Container) ghép các khối Lego lại với nhau.
*   `fragment_worker_home.xml`: Trang chủ (Ghép từ Header, Toggle trạng thái, Đơn đang làm, Thống kê, Biểu đồ doanh thu).
*   `fragment_worker_orders.xml`: Danh sách Đơn hàng (Chứa TabLayout: Chờ làm / Đang làm / Lịch sử).
*   `fragment_worker_order_detail.xml`: Chi tiết đơn hàng đang thực hiện (Có khu vực cho Bản đồ, tính năng chụp ảnh Trước/Sau khi sửa, thêm phụ phí).
*   `fragment_worker_wallet.xml`: Tab Ví điện tử (Hiển thị thẻ mô phỏng Visa chứa số dư khả dụng to, rõ).
*   `fragment_worker_chat.xml`: *(Placeholder)* Giao diện chờ cho tính năng nhắn tin (Nhóm khác sẽ tích hợp Socket.io vào đây).

### Nhóm 2: Hộp thoại Nghiệp vụ (Dialogs / Bottom Sheets)
*   `dialog_incoming_order.xml`: (UC-W01) Hộp thoại báo động đỏ đếm ngược 3 phút khi có khách gọi.
*   `bottom_sheet_cancel_reason.xml`: (UC-W09) Bảng trượt từ dưới lên chứa danh sách lý do hủy chuyến.
*   `dialog_add_fee.xml`: (UC-W03) Popup điền số tiền vật tư/phụ phí phát sinh.

### Nhóm 3: Thành phần tái sử dụng (Includes & Items)
Được nhúng vào các Fragment thông qua thẻ `<include>` hoặc dùng cho danh sách `RecyclerView`.
*   `layout_worker_topbar.xml`: Thanh tiêu đề điều hướng (dùng chung cho mọi màn hình).
*   `layout_empty_state.xml`: Layout báo trống (Ví dụ: "Chưa có đơn hàng nào").
*   `item_worker_order_card.xml`: Thẻ thông tin chi tiết 1 Đơn hàng.
*   `item_wallet_transaction.xml`: 1 dòng Lịch sử giao dịch ví.
*   Các file cắt nhỏ của màn Home: `layout_worker_home_header`, `layout_worker_home_toggle`, `layout_worker_home_active_order`, v.v.

---

## 3. Lợi ích to lớn của việc "Chia nhỏ" Layout
Triển khai thiết kế thành nhiều file nhỏ mang lại các giá trị cốt lõi:

1.  **Code ngắn gọn, Sạch sẽ (Clean Code):** File `fragment_worker_home.xml` giảm từ ~1000 dòng xuống chỉ còn ~50 dòng code. Giúp thao tác cuộn, đọc hiểu cực kỳ nhanh chóng.
2.  **Tái sử dụng siêu việt (Reusability):** Thiết kế `layout_worker_topbar.xml` một lần nhưng dùng cho 5 màn hình khác nhau. Nếu cần thêm nút "Thông báo" lên Topbar, chỉ cần sửa 1 file duy nhất, 5 màn hình tự động cập nhật.
3.  **Hợp tác nhóm dễ dàng (Parallel Development):** Người A code màn Home, người B code màn Đơn hàng, người C làm thẻ Card. Sẽ không bị xung đột code (Merge Conflict) khi dùng Git.
4.  **Tăng tốc độ Render:** Android Studio preview các file nhỏ nhanh hơn rất nhiều so với việc tải 1 file có cấu trúc cây DOM quá sâu.
5.  **ViewBinding rõ ràng:** Cấu trúc phân cấp ViewBinding (vd: `binding.layoutToggle.switchOnlineStatus`) giúp quản lý biến rành mạch, không bị "rác" namespace.

---

## 4. Tác hại cực lớn (Thảm họa) nếu gộp chung vào 1 file XML
Nếu đi ngược lại chuẩn mực, gộp tất cả mọi thiết kế (thanh tab, thẻ đơn hàng, popups...) vào trực tiếp 1 file XML duy nhất, hệ thống sẽ đối mặt với 3 rủi ro chí mạng:

### ❌ 4.1. Phình to không kiểm soát (Spaghetti Code)
*   Một file XML có thể dài tới **1500 - 3000 dòng code**. 
*   IDE (Android Studio) sẽ phản hồi cực kỳ chậm nheo khi bạn chuyển sang tab Design. 
*   Việc tìm kiếm một ID như `btnCancel` giữa rừng code trở thành một cực hình.

### ❌ 4.2. Bảo trì tồi tệ (Nightmare Maintenance)
*   Để hiển thị danh sách đơn hàng cho 3 Tab (Chờ làm / Đang làm / Lịch sử), bạn sẽ phải Copy-Paste đoạn code thẻ Card Đơn hàng (`item_worker_order_card`) ra làm 3 lần giống y hệt nhau.
*   Ngày mai, Sếp yêu cầu: *"Đổi màu chữ Trạng thái đơn hàng từ Xanh sang Đỏ"*.
    *   **Thực tế chia nhỏ:** Bạn chỉ sửa 1 file `item_...`. Mất 10 giây.
    *   **Nếu gộp chung:** Bạn phải đi dò tìm đủ 3 chỗ trong file 1500 dòng để đổi. Sửa thiếu sót 1 chỗ là sinh ra Bug hiển thị (Inconsistent UI).

### ❌ 4.3. Vi phạm nguyên lý bắt buộc của Android (RecyclerView)
*   **Đây là rào cản kỹ thuật cứng:** Bạn **KHÔNG THỂ** gộp thiết kế của phần tử danh sách (List Item) vào thẳng màn hình cha.
*   Bất kỳ danh sách cuộn nào (Danh sách đơn, lịch sử ví...) trong Android đều dùng `RecyclerView`. Thành phần này **bắt buộc** phải nhận vào một layout rời (`item_...xml`) để nó có thể thực hiện thuật toán Tái sử dụng View (Recycling) bộ nhớ. Việc gộp code khiến tính năng danh sách cuộn mất hoàn toàn tác dụng hoặc bị crash.
