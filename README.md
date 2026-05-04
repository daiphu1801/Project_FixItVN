# FixIt VN - Hệ thống Kết nối Thợ sửa chữa & Khách hàng

*Read this in other languages: [English](#english-version), [Tiếng Việt](#vietnamese-version).*

---

<a name="vietnamese-version"></a>
## 🇻🇳 Phiên bản Tiếng Việt

### 📖 Giới thiệu dự án
**FixIt VN** là một nền tảng ứng dụng đột phá nhằm kết nối trực tiếp Khách hàng có nhu cầu sửa chữa (điện, nước, điện lạnh, v.v.) với các Thợ sửa chữa chuyên nghiệp. Nền tảng đảm bảo tính minh bạch về giá cả, an toàn trong thanh toán (giữ tiền bảo hành) và cung cấp hệ thống giải quyết khiếu nại rõ ràng dựa trên bằng chứng công việc.

### 🚀 Tính năng chính

**1. Hệ thống Tài khoản & Xác thực (Account & Auth)**
- Đăng ký/Đăng nhập an toàn với cơ chế bảo mật **JWT** (Access Token & Refresh Token).
- Xác thực số điện thoại qua mã OTP.
- Tích hợp **eKYC** (sử dụng VNPT SDK) để xác thực danh tính thợ, đảm bảo an toàn cho khách hàng.

**2. Dành cho Khách hàng (Customer)**
- Tìm kiếm, xem danh mục dịch vụ và đặt thợ sửa chữa gần nhất.
- Theo dõi tiến độ công việc của thợ theo thời gian thực (Đang di chuyển, Đang khảo sát, Đang sửa, Hoàn thành).
- Thương lượng và chốt "Thẻ báo giá" ngay trong khung Chat.
- Thanh toán linh hoạt (Tiền mặt hoặc Cổng thanh toán VNPay/ZaloPay).
- Đánh giá thợ (bắt buộc chọn lý do nếu đánh giá từ 3 sao trở xuống).

**3. Dành cho Thợ sửa chữa (Worker)**
- Quản lý trạng thái trực tuyến (Bật/Tắt "Sẵn sàng nhận việc") với định vị GPS.
- **Cập nhật tiến độ & Báo cáo:** Bắt buộc chụp ảnh làm bằng chứng trước và sau khi sửa chữa trực tiếp từ camera ứng dụng (chống gian lận).
- **Báo giá thông minh:** Lên báo giá chi tiết, thêm hạng mục sửa chữa và gửi trực tiếp qua Chat cho khách hàng duyệt.
- Quản lý "Ví thợ": Theo dõi dòng tiền, tiền tạm giữ bảo hiểm (96 giờ) và các khoản chiết khấu.

**4. Dành cho Quản trị viên (Admin)**
- Quản lý, kiểm duyệt tài khoản Thợ và đối soát giao dịch ví.
- Quản lý khiếu nại (Complaint & Warranties) dựa trên bằng chứng hình ảnh và lịch sử chat.
- Hệ thống Audit Logs lưu vết toàn bộ thao tác của Admin.

### 🛠 Công nghệ & Kiến trúc Cơ sở dữ liệu
- **Database:** PostgreSQL (Sử dụng UUID làm khóa chính, các kiểu dữ liệu tọa độ `NUMERIC(11, 8)` cho GPS).
- **Authentication:** JWT, Firebase/SMS OTP.
- **eKYC:** VNPT eKYC SDK (iOS v3.5.9).
- **Payments:** VNPay, ZaloPay.
- **Kiến trúc:** Phân tách rõ ràng các thực thể: `Users`, `Workers`, `Customers`, `Worker_Quotations`, `Wallet_Transactions`, `Proof_of_Works`...

### 👥 Đội ngũ phát triển (Nghiệp vụ & Phân tích)
- **Hiếu:** Phụ trách luồng Xác thực (Auth) & Use case chung.
- **Phú:** Đặc tả Nhóm chức năng Thợ (Worker View), DB Thợ.
- **Thế Anh:** Đặc tả tìm kiếm và đặt thợ (Search & Booking), Báo giá, Tài chính ví.
- **Minh Tiến:** Quản lý hóa đơn, Đánh giá thợ, Chat.

---

<a name="english-version"></a>
## 🇬🇧 English Version

### 📖 Project Overview
**FixIt VN** is an innovative platform designed to directly connect Customers needing home repairs (electrical, plumbing, appliances, etc.) with professional Technicians/Workers. The system ensures price transparency, secure payments (escrow logic for warranty), and a robust dispute resolution system based on verifiable work evidence.

### 🚀 Key Features

**1. Account & Authentication**
- Secure Login/Register using **JWT** (Access & Refresh Tokens).
- OTP verification for phone numbers.
- **eKYC** integration (via VNPT SDK) for worker identity verification to ensure customer safety.

**2. For Customers**
- Search services, view categories, and book nearby technicians.
- Real-time job tracking (Moving, Surveying, Fixing, Completed).
- Chat with workers, negotiate, and approve "Quotation Cards" directly in the chat interface.
- Flexible payment gateways (Cash, VNPay, ZaloPay).
- Review & Rating system (mandatory feedback for ratings ≤ 3 stars).

**3. For Technicians (Workers)**
- Online status management (Toggle Availability) with GPS tracking.
- **Progress & Reporting:** Mandatory before/after photo evidence captured directly via the in-app camera to prevent fraud.
- **Smart Quotation:** Create detailed quotes, add service items, and send them directly to the customer via Chat.
- Wallet Management: Track cash flow, escrow funds (held for 96 hours for warranty), and platform commissions.

**4. For Administrators**
- Worker account moderation and wallet transaction reconciliation.
- Dispute & Warranty management using chat history and photo evidence as proof.
- Comprehensive Admin Audit Logs for system transparency.

### 🛠 Tech Stack & Database Architecture
- **Database:** PostgreSQL (Using UUIDs for primary keys, `NUMERIC` types for GPS coordinates).
- **Authentication:** JWT, Firebase/SMS OTP.
- **eKYC:** VNPT eKYC SDK (iOS v3.5.9).
- **Payments:** VNPay, ZaloPay.
- **Architecture:** Clear separation of entities (`Users`, `Workers`, `Customers`, `Worker_Quotations`, `Wallet_Transactions`, `Proof_of_Works`, etc.).

### 👥 Business Analysis & Development Team
- **Hiếu:** Authentication flows & General Use Cases.
- **Phú:** Worker View features & Worker DB Schema.
- **Thế Anh:** Chat Negotiation, Quotations & Wallet Finance.
- **Minh Tiến:** Invoicing, Ratings & Chat Management.