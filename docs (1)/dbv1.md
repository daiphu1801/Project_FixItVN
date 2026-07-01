// ==========================================

// 1. NHÓM TÀI KHOẢN & XÁC THỰC (Account & Auth)

// ==========================================

// Bảng lưu thông tin gốc của tất cả người dùng (Khách, Thợ, Admin)

Table Users {

id uuid \[pk\] // Mã định danh duy nhất của người dùng

phone_number varchar(15) \[unique, not null\] // Số điện thoại dùng để
đăng nhập

email varchar(255) \[unique\] // Email liên hệ (có thể rỗng)

password_hash varchar(255) // Mật khẩu đã được mã hóa

role varchar(20) // Phân quyền: Customer (Khách), Worker (Thợ), Admin
(Quản trị)

avatar_url text // Link ảnh đại diện

is_active boolean \[default: true\] // Trạng thái khóa/mở tài khoản

created_at timestamp // Thời gian tạo tài khoản

}

// Bảng quản lý đăng nhập bằng Mạng xã hội (Tier 1)

Table User_Social_Logins {

id uuid \[pk\]

user_id uuid \[ref: \> Users.id\] // Liên kết với tài khoản gốc

provider varchar(50) // Nhà cung cấp (VD: \'Google\', \'Apple\')

provider_id varchar(255) \[not null\] // ID định danh do Google/Apple
cấp

created_at timestamp

}

// Bảng lưu hồ sơ riêng của Thợ

Table Workers {

worker_id uuid \[pk, ref: \> Users.id\] // Mã thợ (liên kết với bảng
Users)

full_name varchar(100) // Họ và tên thật (khớp với CCCD)

identity_card varchar(20) \[unique\] // Số Căn cước công dân

verification_status varchar(50) \[default: \'Pending\'\] // Trạng thái
duyệt hồ sơ: Pending, Approved, Rejected

latitude numeric(10,8) // Tọa độ GPS động (Vĩ độ)

longitude numeric(11,8) // Tọa độ GPS động (Kinh độ)

is_available boolean \[default: false\] // Công tắc Bật/Tắt nhận đơn của
thợ

reputation_score numeric(3,1) \[default: 5.0\] // Điểm đánh giá uy tín
(Tối đa 5.0)

missed_count int \[default: 0\] // Đếm số lần lờ đơn hàng (để hệ thống
tự động phạt)

rejection_count int \[default: 0\] // TIER 1: Đếm số lần thợ chủ động từ
chối đơn

rejected_priority_until timestamp // TIER 1: Thời gian hết hạn phạt giảm
ưu tiên (phạt 24h)

experience_description text // TIER 2: Mô tả kinh nghiệm hiển thị cho
khách xem

service_area varchar(255) // TIER 2: Khu vực hoạt động của thợ (VD: Quận
1, Quận 3)

}

// Bảng lưu hồ sơ định danh điện tử (VNPT eKYC) của thợ

Table Worker_Identity_Cards {

id uuid \[pk\] // Mã bản ghi

worker_id uuid \[unique, ref: \> Workers.worker_id\] // Hồ sơ này của
thợ nào

front_image_url text // Link ảnh CCCD mặt trước

back_image_url text // Link ảnh CCCD mặt sau

vnpt_ekyc_hash varchar(255) // Mã băm/Mã đối soát trả về từ hệ thống
VNPT

status varchar(50) \[default: \'Pending\'\] // Trạng thái xác thực khuôn
mặt & giấy tờ

}

// Bảng lưu hồ sơ riêng của Khách hàng

Table Customers {

customer_id uuid \[pk, ref: \> Users.id\] // Mã khách hàng (liên kết với
bảng Users)

full_name varchar(100) // Tên hiển thị của khách

//loyalty_points int \[default: 0\] // Điểm tích lũy sau mỗi lần hoàn
thành đơn để đổi mã giảm giá

//cancelled_count int \[default: 0\] // Đếm số lần khách \"bom\" thợ để
Admin khóa tài khoản

}

// Bảng lưu sổ địa chỉ quen thuộc của khách (Giúp đặt đơn nhanh 1 chạm)

Table Customer_Addresses {

id uuid \[pk\] // Mã bản ghi

customer_id uuid \[ref: \> Customers.customer_id\] // Thuộc về khách
hàng nào

label varchar(50) // Tên gợi nhớ. VD: \"Nhà riêng\", \"Công ty\",
\"Phòng trọ\"

address text \[not null\] // Địa chỉ chi tiết (dạng chữ)

latitude numeric(10,8) // Vĩ độ của địa chỉ này

longitude numeric(11,8) // Kinh độ của địa chỉ này

is_default boolean \[default: false\] // Cờ đánh dấu đây là địa chỉ mặc
định khi mở app

}

// Bảng quản lý mã xác thực OTP gửi qua SMS

Table Otp_Codes {

id uuid \[pk\] // Mã bản ghi

phone_number varchar(15) // Số điện thoại nhận mã

otp_code varchar(6) // Mã OTP 6 số

action_type varchar(50) // Phân loại mục đích dùng OTP: Register,
Forgot_Password, Withdraw_Money

expires_at timestamp // Thời gian mã hết hạn (thường là sau 2-3 phút)

is_used boolean \[default: false\] // Cờ đánh dấu mã này đã được sử dụng
chưa

}

// Bảng quản lý phiên đăng nhập (Refresh Tokens)

Table Refresh_Tokens {

id uuid \[pk\] // Mã bản ghi

user_id uuid \[ref: \> Users.id\] // Thuộc về người dùng nào

token text \[not null\] // Chuỗi Refresh Token được cấp

expires_at timestamp \[not null\] // Thời gian hết hạn của token

is_revoked boolean \[default: false\] // Cờ đánh dấu token này đã bị thu
hồi/đăng xuất chưa

created_at timestamp // Thời gian tạo

}

// ==========================================

// 2. NHÓM DỊCH VỤ & TÌM KIẾM (Service & Search)

// ==========================================

// Bảng danh mục ngành nghề lớn

Table Service_Categories {

id int \[pk, increment\] // Mã danh mục (tự tăng)

service_name varchar(255) \[not null\] // Tên nghề. VD: \"Sửa điện
lạnh\", \"Sửa ống nước\"

}

// Bảng danh sách bệnh/vật tư chi tiết (Dùng để thợ tick chọn báo giá
nhanh)

Table Service_Items {

id int \[pk, increment\] // Mã hạng mục

service_category_id int \[ref: \> Service_Categories.id\] // Thuộc ngành
nghề nào

item_name varchar(255) \[not null\] // Tên hạng mục. VD: \"Bơm gas điều
hòa\", \"Thay lốc tủ lạnh\"

suggested_price numeric(12,2) // Mức giá tham khảo cho hạng mục này

}

// Bảng Hồ sơ năng lực của thợ (Cho biết thợ biết làm nghề gì)

Table Worker_Services {

worker_id uuid \[ref: \> Workers.worker_id\] // Mã người thợ

service_id int \[ref: \> Service_Categories.id\] // Mã nghề mà thợ này
biết làm

base_price numeric(12,2) // Giá công thợ cơ bản cho nghề này (nếu có)

indexes {

(worker_id, service_id) \[pk\] // Khóa chính kép: 1 thợ không thể đăng
ký 1 nghề 2 lần

}

}

// Bảng thợ quen, thợ yêu thích

Table Favorite_Workers {

customer_id uuid \[ref: \> Customers.customer_id\]

worker_id uuid \[ref: \> Workers.worker_id\]

saved_at timestamp

indexes {

(customer_id, worker_id) \[pk\] // Đảm bảo 1 khách không lưu 1 thợ 2 lần

}

}

// ==========================================

// 3. NHÓM ĐƠN HÀNG, BÁO GIÁ & BẢO HÀNH (Booking & Quotation)

// ==========================================

// Bảng Đơn hàng (Trái tim của hệ thống)

Table Bookings {

id uuid \[pk\] // Mã đơn hàng

customer_id uuid \[ref: \> Customers.customer_id\] // Khách nào đặt

worker_id uuid \[ref: \> Workers.worker_id\] // Thợ nào nhận

service_id int \[ref: \> Service_Categories.id\] // Yêu cầu sửa nghề gì

address text \[not null\] // Địa chỉ nhà khách (dạng chữ)

destination_lat numeric(10,8) // Tọa độ GPS tĩnh (Vĩ độ) của nhà khách

destination_lng numeric(11,8) // Tọa độ GPS tĩnh (Kinh độ) của nhà khách

issue_description text // Lời nhắn/Mô tả bệnh thiết bị từ khách hàng

scheduled_time timestamp // Khung giờ khách mong muốn thợ có mặt

payment_method varchar(50) \[default: \'CASH\'\] // CASH, BANK_TRANSFER,
WALLET

final_price numeric(12,2) // Giá tiền chốt cuối cùng sau khi mặc cả

status varchar(50) \[default: \'Pending\'\] // Pending, Accepted,
Surveying, Waiting_Approval, In_Progress, Completed, Cancelled

created_at timestamp // Thời gian khách ấn nút tạo đơn

}

// Bảng lưu lịch sử điều phối đơn cho thợ (Tier 1)

Table Booking_Worker_Assignments {

id uuid \[pk\] // Mã bản ghi

booking_id uuid \[ref: \> Bookings.id\] // Đơn hàng nào được phát ra

worker_id uuid \[ref: \> Workers.worker_id\] // Phát cho thợ nào

status varchar(50) \[default: \'Pending\'\] // Trạng thái phản hồi:
Pending, Accepted, Rejected, Missed

assigned_at timestamp // Thời điểm hệ thống bắn đơn cho thợ (Bắt đầu đếm
giờ)

responded_at timestamp // Thời điểm thợ thao tác (hoặc hệ thống chốt là
missed)

}

// Bảng Báo giá

Table Worker_Quotations {

id uuid \[pk, default: \`uuid_generate_v4()\`\]

worker_id uuid \[ref: \> Workers.worker_id\] // Khóa ngoại trỏ về thợ
báo giá

booking_id uuid \[ref: \> Bookings.id\] // Khóa ngoại trỏ về đơn hàng

total_proposed_price numeric(12,2) \[not null\]

status varchar(50) \[default: \'Pending\'\]

created_at timestamp \[default: \`CURRENT_TIMESTAMP\`\]

}

// Bảng Chi tiết Báo giá

Table Quotation_Items {

id uuid \[pk, default: \`uuid_generate_v4()\`\]

quotation_id uuid \[ref: \> Worker_Quotations.id\] // Khóa ngoại trỏ về
Thẻ báo giá (Tạo quan hệ 1-N)

service_item_id int \[ref: \> Service_Items.id\] // Khóa ngoại trỏ về
hạng mục bệnh/vật tư

item_name varchar(255) \[not null\]

quantity int \[default: 1\]

unit_price numeric(12,2) \[not null\]

total_price numeric(12,2) \[note: \'Generated column: quantity \*
unit_price\'\]

created_at timestamp \[default: \`CURRENT_TIMESTAMP\`\]

}

// Bảng Bằng chứng thép (Lưu ảnh chụp hiện trường)

Table Proof_Of_Works {

id uuid \[pk\] // Mã bằng chứng

booking_id uuid \[ref: \> Bookings.id\] // Của đơn hàng nào

image_url text \[not null\] // Link ảnh chụp hiện trạng

proof_type varchar(50) // Phân loại ảnh: BEFORE_REPAIR, AFTER_REPAIR

captured_at timestamp \[default: \`CURRENT_TIMESTAMP\`\] // Thời điểm
bấm chụp ảnh

}

// Bảng Lịch sử/Lý do hủy đơn

Table Cancellation_Details {

booking_id uuid \[pk, ref: \> Bookings.id\] // Mã đơn bị hủy

cancelled_by_id uuid \[ref: \> Users.id\] // Ai là người bấm hủy

cancelled_by_role varchar(20) // Khách, Thợ hay Admin

reason_category varchar(50) // Phân loại lỗi hủy (để thống kê)

cancellation_reason text // Lời giải thích chi tiết

reputation_penalty_applied numeric(3,1) \[default: 0\] // Số điểm uy tín
bị trừ do hủy sai

cancelled_at timestamp // Thời gian bấm hủy

}

// Bảng Nhật ký đơn hàng (Track các mốc thời gian)

Table Booking_Histories {

id uuid \[pk\] // Mã nhật ký

booking_id uuid \[ref: \> Bookings.id\] // Của đơn hàng nào

status_update varchar(50) // Trạng thái được cập nhật sang gì

updated_at timestamp // Vào lúc mấy giờ

}

// Bảng Khiếu nại bảo hành

Table Complaint_Warranties {

id uuid \[pk\] // Mã khiếu nại

booking_id uuid \[unique, ref: \> Bookings.id\] // Khiếu nại cho đơn
hàng nào (1 đơn = 1 khiếu nại)

customer_reason text // Lời phàn nàn của khách

worker_response text // Lời giải thích/phản biện của thợ

evidence_image_urls text // TIER 2: Link ảnh bằng chứng bổ sung từ thợ
(dạng chuỗi/JSON array)

status varchar(50) \[default: \'Pending\'\] // Pending,
Worker_Responded, Resolved

deadline_to_respond timestamp // Thời hạn chót thợ phải giải trình

created_at timestamp // Thời gian khách bấm khiếu nại

}

// ==========================================

// 4. NHÓM VÍ THỢ & THANH TOÁN (Wallet & Payment)

// ==========================================

// Bảng Ví ảo của thợ

Table Worker_Wallets {

worker_id uuid \[pk, ref: \> Workers.worker_id\] // Ví của thợ nào (1
thợ có 1 ví)

available_balance numeric(12,2) \[default: 0\] // Tiền khả dụng (Có thể
rút ngay về ngân hàng)

held_balance numeric(12,2) \[default: 0\] // Tiền bị giam 96h (Quỹ bảo
hành chờ nhả)

debt_balance numeric(12,2) \[default: 0\] // Tiền nợ nền tảng

}

// Bảng Tài khoản ngân hàng thực tế của thợ

Table Worker_Bank_Accounts {

id uuid \[pk\] // Mã thẻ

worker_id uuid \[ref: \> Workers.worker_id\] // Thẻ này của thợ nào

bank_name varchar(100) // Tên ngân hàng. VD: MBBank, Vietcombank

account_number varchar(50) // Số tài khoản ngân hàng

account_name varchar(100) // Tên chủ thẻ (Bắt buộc phải giống tên trong
CCCD)

is_default boolean \[default: true\] // Cờ chọn thẻ mặc định để nhận
tiền rút về

}

// Bảng Lịch sử biến động số dư (Đối soát dòng tiền)

Table Transaction_Histories {

id uuid \[pk\] // Mã giao dịch

wallet_id uuid \[ref: \> Worker_Wallets.worker_id\] // Giao dịch của ví
nào

booking_id uuid \[ref: \> Bookings.id\] // Giao dịch sinh ra từ đơn nào

transaction_type varchar(50) // Deposit, Withdraw, Holding, Release,
Fee_Deduction

amount numeric(12,2) // Số tiền biến động

transaction_code varchar(50) \[unique\] // Mã cú pháp tự sinh để nhúng
vào QR VietQR

gateway_reference_code varchar(100) // Mã giao dịch đối soát trả về

target_bank_account_id uuid \[ref: \> Worker_Bank_Accounts.id\] // Bắn
về thẻ nào (Nếu rút)

status varchar(50) \[default: \'Pending\'\] // Pending, Success, Failed

admin_note text // Lời ghi chú của Admin khi duyệt lệnh nạp/rút bằng tay

held_release_at timestamp // TIER 1: Thời điểm dự kiến nhả tiền (Để đếm
ngược 96h)

transaction_time timestamp // Thời gian thực hiện giao dịch

}

// ==========================================

// 5. NHÓM TIỆN ÍCH (Utility)

// ==========================================

// Bảng Đánh giá chất lượng

Table Reviews {

id uuid \[pk\] // Mã đánh giá

booking_id uuid \[unique, ref: \> Bookings.id\] // Đánh giá cho đơn nào

customer_id uuid \[ref: \> Customers.customer_id\] // TIER 2: Khách hàng
nào thực hiện đánh giá

rating int // Số sao (Từ 1 đến 5)

reason_tag varchar(100) // BỔ SUNG: \"Thái độ kém\", \"Đến trễ\"\...
(Bắt buộc nếu rating \<= 3)

comment text // Lời nhận xét chi tiết

created_at timestamp // Thời gian gửi đánh giá

}

// Bảng Lịch sử Thông báo trong App

Table Notifications {

id uuid \[pk\] // Mã thông báo

user_id uuid \[ref: \> Users.id\] // Gửi cho ai (Khách hoặc Thợ)

title varchar(255) // Tiêu đề. VD: \"Có đơn sửa mới!\"

content text // Nội dung chi tiết

is_read boolean \[default: false\] // Đánh dấu đã đọc hay chưa (hiện
chấm đỏ)

created_at timestamp // Thời gian thông báo bắn tới

}

// Bảng Định danh Thiết bị (Để gửi Push Notification bằng Firebase FCM)

Table User_Devices {

id uuid \[pk\] // Mã bản ghi

user_id uuid \[ref: \> Users.id\] // Điện thoại này đang được đăng nhập
bởi ai

device_token varchar(255) \[not null\] // Mã Token do Firebase cấp

device_os varchar(20) // Hệ điều hành (iOS hoặc Android)

last_active timestamp // Lần cuối mở app

}
