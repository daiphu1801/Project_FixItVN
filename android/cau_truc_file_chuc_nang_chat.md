# KIẾN TRÚC MÃ NGUỒN CHỨC NĂNG CHAT (FIREBASE) - FIXIT

Tài liệu này giải thích chi tiết ý nghĩa của từng thư mục và tệp trong tính năng Chat của cả hai bên Khách hàng (`customer`) và Thợ (`worker`) trong ứng dụng FixIt.

---

## I. CẤU TRÚC THƯ MỤC KHÁCH HÀNG: com.fixit.feature.customer.chat

Tầng này được viết theo kiến trúc Clean Architecture kết hợp với MVVM, chia rõ nhiệm vụ của từng phần bao gồm **data** (dữ liệu), **domain** (nghiệp vụ cốt lõi), và **presentation** (giao diện người dùng).

### 1. Thư mục `domain` (Tầng nghiệp vụ cốt lõi)
Tầng này chứa các quy tắc nghiệp vụ thuần Java/Android, độc lập hoàn toàn với các thư viện bên ngoài hay cơ sở dữ liệu.

*   **Thư mục `model`**: Định nghĩa các thực thể dữ liệu nghiệp vụ của tính năng chat.
    *   `ChatMessage.java`: Đại diện cho một tin nhắn cụ thể trong cuộc hội thoại. Chứa các thuộc tính:
        *   `messageId`: ID duy nhất của tin nhắn.
        *   `content`: Nội dung tin nhắn.
        *   `timestamp`: Thời gian gửi (giờ hiển thị dạng HH:mm).
        *   `isSentByMe`: Cờ xác định tin nhắn do chính mình gửi (`true`) hay người khác gửi (`false`).
    *   `ChatPreview.java`: Đại diện cho một dòng hội thoại hiển thị ở danh sách tin nhắn. Chứa các thuộc tính:
        *   `workerId` / `workerName`: ID và Tên của người đối thoại (dùng chung cho cả thợ và khách).
        *   `lastMessage`: Nội dung tin nhắn cuối cùng được gửi.
        *   `lastMessageTime`: Thời gian của tin nhắn cuối cùng.
        *   `isOnline`: Trạng thái hoạt động (online/offline).
        *   `isUnread`: Cờ báo có tin nhắn mới chưa đọc hay không.

*   **Thư mục `repository`**:
    *   `ChatRepository.java`: Định nghĩa interface (khung giao tiếp) chứa các phương thức:
        *   `sendMessage(...)`: Gửi tin nhắn mới.
        *   `listenMessages(...)`: Lắng nghe tin nhắn mới thời gian thực.
        *   `listenConversations(...)`: Lắng nghe danh sách cuộc hội thoại thời gian thực.
        *   `markAsRead(...)`: Đánh dấu phòng chat đã đọc.

*   **Thư mục `usecase`**: Các luồng nghiệp vụ chạy riêng lẻ.
    *   `GetConversationsUseCase.java`: Thực thi việc lấy/lắng nghe danh sách phòng chat.
    *   `GetMessagesUseCase.java`: Thực thi việc lấy tin nhắn trong một phòng chat cụ thể.
    *   `MarkAsReadUseCase.java`: Thực thi nghiệp vụ cập nhật trạng thái đã đọc tin nhắn.
    *   `SendMessageUseCase.java`: Thực thi nghiệp vụ gửi tin nhắn đi.

### 2. Thư mục `data` (Tầng xử lý dữ liệu)
Tầng này thực hiện cài đặt thực tế của tầng Domain bằng các thư viện cụ thể.

*   **Thư mục `repository`**:
    *   `ChatRepositoryImpl.java`: Triển khai chi tiết các hàm trong `ChatRepository`. Đây là nơi thực hiện các truy vấn trực tiếp đến **Firebase Firestore** trên hai Collection chính:
        *   `conversations`: Quản lý các phòng chat (lưu trữ thông tin nhắn tin gần nhất, trạng thái chưa đọc của từng bên qua thuộc tính map `unreadFor`).
        *   `messages` (Subcollection của `conversations`): Lưu trữ lịch sử tin nhắn chi tiết.

### 3. Thư mục `di` (Dependency Injection)
*   `ChatModule.java`: Sử dụng Dagger Hilt để tiêm (inject) lớp triển khai `ChatRepositoryImpl` khi hệ thống yêu cầu một đối tượng có kiểu giao diện `ChatRepository`.

### 4. Thư mục `presentation` (Tầng giao diện Khách hàng)
Tầng này quản lý các thành phần giao diện hiển thị cho người dùng và vòng đời của chúng.

*   `ChatCustomerFragment.java`: Giao diện phòng chat 1-1 chi tiết (hiển thị danh sách tin nhắn dạng bong bóng, ô nhập tin nhắn, nút gửi). Màn hình này được thiết kế tổng quát để **tái sử dụng chung** cho cả phía Thợ (`worker`) khi vào giao diện chat chi tiết.
*   `CustomerListMsgFragment.java`: Giao diện danh sách các cuộc hội thoại của Khách hàng.
*   `ChatMessageAdapter.java`: Bộ điều phối RecyclerView hiển thị các tin nhắn với 2 loại giao diện: tin nhắn gửi đi (bên phải, màu xanh) và tin nhắn nhận được (bên trái, màu trắng).
*   `ChatPreviewAdapter.java`: Bộ điều phối RecyclerView hiển thị danh sách các phòng chat phía Khách hàng.
*   `ChatViewModel.java`: Nhận sự kiện từ UI chat chi tiết, gọi UseCases tương ứng và cập nhật trạng thái (tin nhắn mới, báo lỗi) thông qua LiveData cho UI lắng nghe.
*   `ConversationsViewModel.java`: Lắng nghe danh sách phòng chat từ UseCase và cung cấp dữ liệu LiveData cho màn hình danh sách hội thoại.

---

## II. CẤU TRÚC THƯ MỤC THỢ: com.fixit.feature.worker.chat

Phía Thợ chỉ tập trung vào tầng hiển thị giao diện vì các cấu trúc dữ liệu (`data`), nghiệp vụ (`domain`), tiêm phụ thuộc (`di`) và thậm chí là màn hình chat chi tiết (`ChatCustomerFragment`) được **chia sẻ và tái sử dụng toàn bộ** từ gói của Khách hàng (`customer.chat`).

*   `WorkerListMsgFragment.java`: Giao diện hiển thị danh sách các hội thoại của Thợ (tương tự như màn hình danh sách của khách hàng nhưng sử dụng layout và adapter riêng của thợ là `WorkerChatPreviewAdapter` nhằm tùy biến giao diện phù hợp với thương hiệu app thợ).
*   `WorkerChatFragment.java`: Fragment rỗng chưa có mã xử lý (dư thừa từ dự án trước, sẽ được loại bỏ trong kế hoạch dọn dẹp).
