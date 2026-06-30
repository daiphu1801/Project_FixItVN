# Nội dung yêu cầu cv

### **Yêu cầu buổi 1:**

- Đọc nhóm chức năng được phân, nghiên cứu, tìm hiểu
- Thiết kế quy trình hoạt động của từng nhóm chức năng. Mô tả bằng lời hoặc biểu đồ ( nếu có ) sau đó lập bảng đặc tả use case
- Deadline: 08/04/2026 - thứ Tư họp buổi tối, nộp trước vào 07/04/2026 - ae đọc qua trước
- Cảm thấy nhóm chức năng có vấn đề gì như thiếu 1 tính năng nhỏ nào đó để hoàn thiện lỗi xảy ra gây xung độ hệ thống thì bổ sung
- **Tài liệu tham khảo**
    - API ( copy ): https://web.facebook.com/cuocdoianhIT/posts/pfbid0dfdQdAmhE65jGT3ga58CQDof7hW9QYyT6Y2cotdGW1SZ8go5TgkQtQhv2VBH83rUl?rdid=EVzPASiSGjqzyBsy#
    - Bản đặc tả sơ qua nếu chưa nắm rõ đề tài ( đã tổng hợp của tất cả ae ):

[Bản đặc tả dự án FixIt VN](https://docs.google.com/document/d/1-244JOBAyJ-G-N5rI7j_P8AHL5bj24R4WrFTOZ4AsiE/edit?tab=t.0#heading=h.90wxespam93m)

- Mẫu bảng use case: https://thinhnotes.com/chuyen-nghe-ba/viet-dac-ta-use-case-sao-don-gian-nhung-hieu-qua/
- AI hỗ trợ thiết kế có thể xuất ra Figma:

https://stitch.withgoogle.com/

**Yêu cầu buổi 2 - Vẽ sequence và activity:**

- Deadline: 15/04/2026 - thứ Tư họp buổi tối, nộp trước vào 14/04/2026
- Ai làm phần nào vẽ phần đấy
- Chỉnh sửa:
    - Hiếu: tách luồng uc thành đăng ký, đăng nhập riêng. Bổ sung luồng UC quên mật khẩu gửi mã 6 số qua email
    - Thế Anh: chỉnh lại luồng uc thương lượng giá cả
    - !!! thiếu luồng UC hủy đơn hàng ( khách TAnh - thợ Phú ), UC đăng xuất ( Hiếu ), UC quên mật khẩu ( Hiếu ), UC Gửi khiếu nại ( Bảo hành - khách ) ( Tiến )
    - Chỉnh lại cho bản đặc tả đẹp, chỉn chu, không bị nhiều khoảng trống
    - **Phú: tách uc-03 thành 2 uc riêng biệt ( Chỉnh và bổ sung )**

**Yêu cầu buổi 3 - Tổng hợp bảng database:**

- Deadline: 20/04/2026 - Thứ 2
- Mỗi người làm bảng database cho nhóm chức năng của mình
- Làm tên bảng, tên trường đầy đủ
- Fix vẽ:

Chữa:

Hiếu:

1\. Dky tkhoan

\- ctl_otp -> actor

\- giao diện k xác thực đc -> gửi yêu cầu xác thực

\- sau khi gửi yêu cầu đăng ký phải vào db check luôn xem tkhoan đấy tồn tại chưa thì sau đấy mới bắt đầu chia trường hợp

\- Sửa luồng

\+ nhập sdt/emaik

\+ nhập pass,xác nhận pass

\+ gửi yêu cầu dky

\+ mới gửi yêu cầu otp

\+ check đúng thì tạo tkhoan thành công

2\. Đăng nhập bang gg

\- sửa gg là 1 actor

3\. Quên mật khẩu

\- đổi email thành actor

\- thêm đường check thông tin db đã r mới gửi mã

\- Activity:

1.Quên mật khẩu:

\- Sửa nhập lại mã -> gửi tbao cho người dùng -> cho người dung nhập lại mã

Phú:

\- seqTiepNhan:

\+ hệ thống kiểm tra đơn công việc

\+ cập nhật status trong db

\- seqCapNhatVaTienDoCongViec

\+ gửi yêu cầu

\+ sửa vòng loop thành hệ thống cập nhật kphai actor

\=> sửa luồng hđ khác

\- seqNapTienVaoVi

\+ hệ thống kiểm tra số tiền

### **4.1. Nhóm chức năng Tài khoản & Xác thực (Authentication & Profile) - Hiếu**

- **Đăng ký/Đăng nhập \[Chính\]:** Tách biệt luồng cho Khách hàng và Thợ. Đăng nhập thông thường qua Số điện thoại (OTP) hoặc Email/Mật khẩu.
- **Đăng nhập bằng Google \[Phụ\]:** Tích hợp Google Sign-in cho cả Khách hàng và Thợ để đăng ký/đăng nhập nhanh (1-tap login).
- **Quản lý thông tin cá nhân \[Chính\]:** Cập nhật Avatar, Họ tên, Số điện thoại. Khách hàng có thể lưu địa chỉ nhà/công ty. Thợ có thể cập nhật kinh nghiệm, khu vực hoạt động.
- **Xác minh (Verification) \[Chính/Phụ\]:** \* _Khách hàng:_ Xác minh cơ bản qua SĐT để chống spam đơn giả.
    - _Thợ:_ Yêu cầu bắt buộc tải lên hình ảnh CCCD và chứng chỉ nghề (eKYC) để Admin duyệt trước khi nhận việc, đảm bảo an toàn cho khách hàng.

### **4.2. Nhóm chức năng Tìm kiếm & Lọc Dịch vụ (Search & Matching) - Thế Anh**

- **Tìm kiếm thợ \[Chính\]:** Hiển thị danh mục dịch vụ. Tự động lấy tọa độ GPS hiện tại của khách hàng để quét và hiển thị số lượng thợ đang rảnh trên bản đồ (trong bán kính cho phép).
- **Danh sách Thợ quen \[Phụ\]:** Khách hàng có thể lưu lại những người thợ đã làm tốt vào danh sách "Thợ yêu thích". Khi có nhu cầu lần sau, hệ thống ưu tiên gửi thông báo đặt lịch cho nhóm "Thợ quen" này trước.

### **4.3. Nhóm chức năng Đặt lịch, Báo giá & Thanh toán (Booking & Payment) - Thế Anh**

- **Đặt thợ \[Chính\]:** Tạo yêu cầu sửa chữa (chụp ảnh, ghi chú lỗi), chọn thời gian (Đến ngay / Hẹn giờ). Hệ thống gửi yêu cầu đến thợ.
- **Thanh toán & Thương lượng \[Chính\]:** Sau khi thợ khảo sát thực tế, hai bên có thể **thương lượng**. Thợ chốt "Báo giá cuối cùng" trên app. Khách hàng bấm đồng ý. Hỗ trợ thanh toán tiền mặt hoặc chuyển khoản.

### **4.4. Nhóm chức năng Quản lý Yêu cầu & Tương tác (Interaction & Management) - Tiến**

- **Nhắn tin/Gọi điện trong App \[Chính\]:** Tích hợp Chat Real-time hoặc Call giữa Khách hàng và Thợ để trao đổi chi tiết tình trạng hỏng hóc hoặc chỉ đường mà không làm lộ số điện thoại cá nhân.
- **Lịch sử và Hóa đơn \[Chính\]:** Khách hàng/ Thợ xem lại các đơn đã hoàn thành, chi phí, thông tin thợ. Dùng làm căn cứ để yêu cầu bảo hành hoặc khiếu nại nếu thiết bị hỏng lại.  
    ( tao hỏi thằng đại rồi, thợ cũng xem lại lịch sử được, không chi mỗi khách hàng xem lại được đâu ok nha minh tiến)
- **Đánh giá Thợ \[Phụ\]:** Sau khi hoàn thành, khách hàng chấm điểm sao (1-5) và để lại nhận xét.
- **Trung tâm hỗ trợ \[Chính\]:** Mục FAQ (Câu hỏi thường gặp) và kênh liên hệ (Chat/Hotline) trực tiếp với Admin hệ thống để báo cáo sự cố (thợ thái độ kém, quỵt tiền, v.v.).

### **4.5. Nhóm chức năng dành riêng cho Thợ (Worker/Technician View) - Phú**

**Thông báo nhận công việc \[Chính/Phụ\]:** Thợ nhận thông báo đẩy (Push Notification) real-time ngay trên app. Tính năng phụ: Gửi kèm email thông báo định kỳ tổng hợp công việc.

**Bật/Tắt trạng thái Sẵn sàng nhận việc (Online/Offline Toggle) \[Chính — Bổ sung\]:** Thợ chủ động bật/tắt trạng thái "Sẵn sàng nhận việc" ngay trên màn hình chính của app.

- _Khi bật (Online):_ Hệ thống liên tục cập nhật tọa độ GPS của thợ vào Redis và hiển thị thợ trên bản đồ tìm kiếm phía khách hàng. Thợ bắt đầu nhận đơn hàng mới.
- _Khi tắt (Offline):_ Thợ biến mất khỏi bản đồ, không nhận đơn mới và hệ thống dừng cập nhật vị trí để tiết kiệm pin.
- _Lý do bắt buộc:_ Nếu không có toggle này, hệ thống không có cơ sở để xác định thợ nào đang rảnh để gửi đơn, toàn bộ luồng matching sẽ không hoạt động được.

**Chấp nhận / Từ chối Đơn hàng (Accept / Reject) \[Chính — Bổ sung\]:** Sau khi nhận Push Notification, thợ xem màn hình chi tiết đơn (địa chỉ, loại lỗi, ảnh khách chụp, khoảng cách di chuyển) và có **X phút** để phản hồi.

- _Chấp nhận:_ Hệ thống xác nhận với khách hàng, kích hoạt Live Tracking và chuyển trạng thái đơn sang "Thợ đang đến".
- _Từ chối:_ Thợ chọn lý do từ chối (bận việc, quá xa, ngoài chuyên môn). Hệ thống tự động chuyển đơn sang thợ tiếp theo trong danh sách phù hợp.
- _Không phản hồi:_ Sau khi hết thời gian X phút, hệ thống tự động bỏ qua và ghi nhận một lần "bỏ lỡ". Tỷ lệ bỏ lỡ cao ảnh hưởng đến điểm xếp hạng và thứ tự ưu tiên nhận đơn của thợ.

**Cập nhật Trạng thái Tiến độ Công việc \[Chính — Bổ sung\]:** Trong suốt quá trình thực hiện công việc, thợ chủ động cập nhật trạng thái theo từng mốc để khách hàng theo dõi theo thời gian thực.

Đã chấp nhận → Đang di chuyển → Đã đến nơi → Đang khảo sát / Báo giá → Đang sửa chữa → Hoàn thành

- Mỗi lần thợ chuyển trạng thái, hệ thống tự động gửi thông báo đến khách hàng.
- Trạng thái _"Đang di chuyển"_ kích hoạt tính năng Live Tracking trên bản đồ phía khách (sử dụng WebSocket/Socket.io).
- Trạng thái _"Hoàn thành"_ mở khóa bước nghiệm thu và thanh toán của khách hàng.

**Chụp ảnh Hiện trạng Trước / Sau sửa chữa (Proof of Work) \[Chính — Bổ sung\]:** Thợ bắt buộc chụp ảnh hiện trạng thiết bị trực tiếp qua app tại hai thời điểm:

- _Trước khi bắt đầu:_ Ghi lại tình trạng hư hỏng ban đầu để tránh bị quy trách nhiệm oan cho các hỏng hóc có sẵn.
- _Sau khi hoàn thành:_ Ghi lại kết quả sửa chữa làm bằng chứng chất lượng công việc.
- Ảnh được lưu tự động lên AWS S3 (Private Bucket) và gắn vĩnh viễn vào hồ sơ đơn hàng. Đây là tài liệu bằng chứng chính thức khi có tranh chấp trong thời gian bảo hiểm 4 ngày.

**Quản lý Ví (Rút tiền / Trả tiền nợ) \[Chính\]:** Trung tâm quản lý tài chính của thợ trên nền tảng, hiển thị rõ ràng 3 trạng thái số dư:

- _Khả dụng:_ Tiền có thể rút ngay về ngân hàng.
- _Đang bảo hiểm:_ Tiền đang trong thời gian giữ 4 ngày, chưa thể rút.
- _Đang nợ chiết khấu:_ Khoản cần nạp để trả cho nền tảng khi khách thanh toán tiền mặt.

Các thao tác chính:

- _Trả tiền nợ:_ Trong trường hợp khách trả tiền mặt cho thợ, thợ đang "nợ" app phí chiết khấu. Thợ phải nạp tiền vào ví qua cổng thanh toán (VNPAY, MoMo, ZaloPay...) để thanh toán khoản nợ, tránh bị hạn chế tài khoản.
- _Rút tiền:_ Rút phần tiền Khả dụng về tài khoản ngân hàng cá nhân. Hệ thống chỉ cho phép rút khi không còn khoản nợ chiết khấu tồn đọng.

**Cơ chế Bảo hiểm Dịch vụ — Giữ tiền 4 ngày \[Chính — Bổ sung\]:** Sau khi khách hàng bấm "Hoàn thành nghiệm thu", hệ thống không chuyển tiền ngay vào phần Khả dụng mà giữ trong trạng thái "Đang bảo hiểm" trong vòng 96 giờ.

- _Trong 96 giờ:_ Nếu khách phát hiện thiết bị hỏng lại và gửi khiếu nại kèm bằng chứng, Admin xem xét và có quyền phán quyết hoàn tiền một phần hoặc toàn bộ cho khách từ khoản đang giữ này.
- _Sau 96 giờ:_ Nếu không phát sinh khiếu nại, hệ thống tự động giải phóng toàn bộ số tiền (đã trừ % hoa hồng nền tảng) sang trạng thái Khả dụng mà không cần thợ thao tác thêm.
- _Thợ nhận thông báo_ ngay khi có khiếu nại và có quyền phản hồi bằng chứng (ảnh trước/sau) để bảo vệ quyền lợi.

**//Xem lịch các đơn đã hẹn (Schedule View) \[Phụ — Bổ sung\]:** Thợ xem danh sách các đơn đã được khách đặt theo lịch hẹn giờ cụ thể trong ngày/tuần. Giúp thợ chủ động sắp xếp lộ trình di chuyển, tránh trùng giờ và không bị quên đơn đã hẹn.

**Thống kê thu nhập \[Phụ\]:** Biểu đồ trực quan báo cáo thu nhập theo ngày/tuần/tháng, bao gồm tổng doanh thu thực nhận, chiết khấu đã nộp và tiền thưởng KPI tích lũy — giúp thợ quản lý tài chính cá nhân.

**Thưởng theo ngày/tháng \[Phụ\]:** Hệ thống Gamification. Nếu thợ đạt KPI (nhận 10 đơn/ngày hoặc được vote 5 sao liên tục), app sẽ tự động cộng tiền thưởng vào ví nhằm khích lệ thợ hoạt động tích cực. Các mốc KPI do Admin cấu hình từ Dashboard.

### **4.6. Nhóm chức năng Quản trị viên (Admin Dashboard) - Làm sau**

- **Quản lý Người dùng & KYC:** Khóa/Mở tài khoản, duyệt ảnh CCCD và bằng cấp của thợ.
- **Cấu hình Hệ thống & Khuyến mãi:** Thiết lập giá trần/sàn, tỷ lệ chiết khấu, tạo lập các chiến dịch Voucher/Mã giảm giá. Cài đặt các mốc KPI để thưởng cho thợ.
- **Thống kê (Analytics):** Biểu đồ doanh thu, tỷ lệ hủy đơn, dịch vụ hot nhất. Quản lý các phản hồi từ Trung tâm hỗ trợ.

# Use case - Phú**MỤC LỤC**

[**MỤC LỤC 1**](#_oj9nss90zz47)

[**USE CASE: Nhóm chức năng dành riêng cho Thợ (Worker/Technician View) 2**](#_1iy1pw63oh1d)

[**I, Tác nhân liên quan 2**](#_wy54bb141e98)

[**II, Use case cho nhóm chức năng 2**](#_qyc49wwbi3c1)

[1\. Danh sách UC cho nhóm chức năng 4.5 2](#_5bgf3jm9wtaj)

[2\. Chi tiết từng đặc tả UC cho nhóm chức năng 4.5 4](#_ismd7ko0hvtd)

[Bảng Đặc tả Use Case: UC-W01 - Tiếp nhận và phản hồi đơn hàng 4](#_mpfh6ffckykt)

[Bảng Đặc tả Use Case: UC-W02 - Bật/Tắt trạng thái Sẵn sàng nhận việc 6](#_448ujuqkb9hv)

[Bảng Đặc tả Use Case: UC-W03a - Cập nhật và thông báo tiến độ di chuyển 9](#_89tfktyftldc)

[Bảng Đặc tả Use Case: UC-W03b - Thực thi công việc và Nghiệm thu 10](#_8wcgsv5xubqg)

[Bảng Đặc tả Use Case: UC-W04 – Xem tổng quan ví tài khoản 14](#_pqo06ovx2yd8)

[Bảng Đặc tả Use Case: UC-W05 – Nạp tiền vào ví trả nợ ứng dụng 17](#_tjs9mlwf1apx)

[Bảng Đặc tả Use Case: UC-W06 – Rút tiền về ngân hàng 20](#_5kal1q2ckbuw)

[Bảng Đặc tả Use Case: UC-W07 – Nhận và phản hồi khiến nại ( Bảo hành ) 23](#_2vufcvu8485d)

[Bảng Đặc tả Use Case: UC-W08 – Bảng tổng kết công việc 26](#_o446f15g6rtp)

[Bảng Đặc tả Use Case: UC-W09 – Hủy công việc ( thợ )](#_odcqmmoarhr6) 29

# **USE CASE: Nhóm chức năng dành riêng cho Thợ (Worker/Technician View)****I, Tác nhân liên quan**

**Tác nhân chính (Primary Actor):**

● **Thợ (Worker/Technician)** — người trực tiếp tương tác với hệ thống để nhận việc, quản lý tài chính cá nhân

**Tác nhân phụ (Secondary Actors — hệ thống/bên ngoài):**

● **Hệ thống (System)** — tự động kích hoạt thông báo, tính toán KPI và cộng thưởng vào ví mà không cần thợ thao tác

● **Cổng thanh toán / Ngân hàng** — bên thứ ba xử lý giao dịch nạp tiền và rút tiền về tài khoản thực của thợ

# **II, Use case cho nhóm chức năng**

## **1\. Danh sách UC cho nhóm chức năng 4.5**

| **Mã UC** | **Tên Use Case** | **Mô tả sơ bộ** |
| --- | --- | --- |
| UC-W01 | Tiếp nhận và Phản hồi đơn hàng | Thợ nhận Push Notification qua FCM, xem chi tiết đơn và chủ động Chấp nhận hoặc Từ chối trong vòng 3 phút. Bao gồm cơ chế timeout tự động và chuyển đơn sang thợ tiếp theo. |
| --- | --- | --- |
| UC-W02 | Bật/Tắt trạng thái Sẵn sàng nhận việc | Thợ chủ động bật/tắt Online để xuất hiện hoặc ẩn khỏi bản đồ tìm kiếm của khách. Kích hoạt Foreground Service cập nhật GPS lên Redis liên tục. |
| --- | --- | --- |
| UC-W03 | Cập nhật tiến độ & Báo cáo thi công | Thợ điều hướng State Machine qua các mốc trạng thái từ "Đang di chuyển" đến "Hoàn thành". Bắt buộc upload ảnh trước/sau lên S3. Hành động "Hoàn thành" là điều kiện kích hoạt cơ chế giữ tiền bảo hiểm 4 ngày trên backend. |
| --- | --- | --- |
| UC-W04 | Xem tổng quan Ví tài khoản | Thợ xem toàn bộ trạng thái tài chính: tiền Khả dụng, tiền đang bị Hold (kèm đồng hồ đếm ngược giải phóng sau 96 giờ) và khoản nợ chiết khấu. Bảo hiểm 4 ngày được phản ánh tại đây dưới dạng Business Rule hiển thị. |
| --- | --- | --- |
| UC-W05 | Nạp tiền vào ví trả nợ chiết khấu | Thợ chủ động nạp tiền qua cổng thanh toán (VNPAY/MoMo) để thanh toán khoản nợ phát sinh khi khách trả tiền mặt. Backend xử lý Webhook và thực thi trừ nợ trong một Transaction. |
| --- | --- | --- |
| UC-W06 | Rút tiền Khả dụng về ngân hàng | Thợ khởi tạo lệnh rút phần tiền Khả dụng về tài khoản ngân hàng liên kết. Hệ thống kiểm tra điều kiện không còn nợ và xử lý ACID nghiêm ngặt để chống double-spending. |
| --- | --- | --- |
| UC-W07 | Nhận & Phản hồi khiếu nại bảo hiểm | Trong thời gian 4 ngày bảo hiểm, khi khách gửi khiếu nại, thợ nhận thông báo, xem nội dung và chủ động phản hồi bằng chứng ảnh trước/sau cho Admin phán quyết. |
| --- | --- | --- |
| UC-W08 | Xem Dashboard Thống kê & Lịch hẹn | Thợ xem một màn hình tổng hợp gồm: lịch các đơn đã hẹn, biểu đồ thu nhập theo ngày/tuần/tháng và thưởng KPI tích lũy. Backend dùng BFF API tổng hợp dữ liệu để giảm số request từ Mobile. |
| --- | --- | --- |

## **2\. Chi tiết từng đặc tả UC cho nhóm chức năng 4.5**

### _Bảng Đặc tả Use Case: UC-W01 - Tiếp nhận và phản hồi đơn hàng_

| **Thành phần** | **Nội dung chi tiết** |
| --- | --- |
| **Mã UC** | UC-W01 |
| --- | --- |
| **Tên UC** | Tiếp nhận và Phản hồi đơn hàng |
| --- | --- |
| **Mô tả** | Là người thợ, tôi muốn nhận được thông báo ngay khi có khách cần sửa chữa gần khu vực mình và chủ động quyết định Chấp nhận hoặc Từ chối để quản lý lịch trình công việc hiệu quả. |
| --- | --- |
| **Tác nhân** | Thợ (Người dùng chính), Hệ thống (Gửi thông báo & Điều phối) |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc có (Must Have) |
| --- | --- |
| **Sự kiện kích hoạt** | Khách hàng đặt lịch sửa chữa xong, hệ thống tìm thấy thợ phù hợp và lập tức báo tin đến điện thoại của thợ. |
| --- | --- |
| **Điều kiện tiên quyết** | \- Thợ đã đăng nhập và hoàn tất thủ tục xác minh hồ sơ.<br><br>\- Thợ đang bật chế độ "Sẵn sàng nhận việc".<br><br>\- Điện thoại có kết nối mạng và cho phép ứng dụng gửi thông báo. |
| --- | --- |
| **Kết quả đầu ra** | \- **(Chấp nhận):** Công việc được giao thành công cho thợ. Hệ thống cấp địa chỉ nhà khách, mở kênh liên lạc riêng tư và cho phép khách theo dõi lộ trình của thợ.<br><br>\- **(Từ chối):** Hệ thống ghi nhận lý do và lập tức chuyển cơ hội việc làm này cho người khác.<br><br>\- **(Bỏ lỡ):** Nếu thợ không thao tác gì, hệ thống đánh dấu là "bỏ lỡ". Bỏ lỡ quá 3 lần liên tiếp, ứng dụng tự động cho thợ chuyển sang chế độ tạm nghỉ. |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | **Bước 1:** Hệ thống tìm thợ theo mức độ ưu tiên: Thợ khách từng thuê -> Thợ ở gần nhất -> Thợ có đánh giá tốt nhất.<br><br>**Bước 2:** Gửi thông báo đến điện thoại thợ kèm theo đồng hồ đếm ngược 3 phút để suy nghĩ.<br><br>**Bước 3:** Thợ bấm vào thông báo để mở ứng dụng lên.<br><br>**Bước 4:** Ứng dụng hiện tóm tắt công việc (tạm ẩn số nhà chi tiết để bảo mật).<br><br>**Bước 5:** Thợ bấm nút "Chấp nhận".<br><br>**Bước 6:** Hệ thống chốt giao việc cho thợ này và tạm ngưng gửi thêm đơn mới cho họ.<br><br>**Bước 7:** Báo tin vui cho khách hàng, cấp số nhà cho thợ, mở tính năng gọi điện/nhắn tin và bật bản đồ dẫn đường.<br><br>**Bước 8:** Thợ bắt đầu lên đường đến nhà khách. |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- **Chủ động mở ứng dụng:** Thợ tự mở ứng dụng lên và thấy có đơn đang chờ phản hồi (không cần bấm từ thông báo).<br><br>\- **Hỏi kỹ trước khi nhận:** Thợ bấm gọi điện hoặc nhắn tin cho khách để hỏi rõ tình trạng hỏng hóc. Trong lúc trao đổi, đồng hồ 3 phút vẫn tiếp tục trôi.<br><br>\- **Từ chối nhận việc:** Thợ chọn "Từ chối" và chọn một lý do (ví dụ: Xa quá, Không đúng chuyên môn, Đang bận). Hệ thống lưu lại và chuyển việc cho thợ khác. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Chậm chân:** Thợ đang do dự thì đã có người khác nhận mất. Ứng dụng báo "Đơn đã có người nhận" và thợ tiếp tục chờ đơn mới.<br><br>\- **Hết thời gian suy nghĩ:** Sau 3 phút không phản hồi, hệ thống tự ghi nhận là thợ "bỏ lỡ" đơn này.<br><br>\- **Tạm nghỉ tự động:** Nếu thợ bỏ lỡ 3 đơn liên tiếp, hệ thống hiểu là thợ đang không cầm máy và tự động tắt chế độ "sẵn sàng nhận việc".<br><br>\- **Khách phải chờ:** Nếu tìm quanh không có thợ nào nhận việc, hệ thống sẽ báo để khách hàng biết và chờ thêm. |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1:** Thời gian 3 phút được tính toán chuẩn xác từ máy chủ trung tâm, không phụ thuộc vào đồng hồ trên điện thoại thợ.<br><br>\- **Quy định 2:** Tuyệt đối không hiển thị số nhà của khách nếu thợ chưa bấm nhận việc.<br><br>\- **Quy định 3:** Mỗi công việc chỉ được giao cho duy nhất một thợ, đảm bảo không có tình trạng tranh giành nhau.<br><br>\- **Quy định 4:** Luôn ưu tiên thông báo trước cho "thợ quen" của khách hàng.<br><br>\- **Quy định 5:** Thợ liên tục bấm từ chối (5 lần) sẽ bị giảm ưu tiên nhận việc trong 24 giờ tiếp theo.<br><br>\- **Quy định 6:** Thợ không để ý điện thoại làm lỡ mất 3 đơn sẽ bị ứng dụng tự động cho tạm nghỉ. |
| --- | --- |

### _Bảng Đặc tả Use Case: UC-W02 - Bật/Tắt trạng thái Sẵn sàng nhận việc_

| **Thành phần** | **Nội dung chi tiết** |
| --- | --- |
| **Mã UC** | UC-W02 |
| --- | --- |
| **Tên UC** | Bật/Tắt chế độ làm việc (Sẵn sàng / Nghỉ ngơi) |
| --- | --- |
| **Mô tả** | Là người thợ, tôi muốn có thể dễ dàng bật hoặc tắt trạng thái nhận việc chỉ bằng một nút gạt, giúp tôi làm chủ được thời gian làm việc và nghỉ ngơi của mình. |
| --- | --- |
| **Tác nhân** | Thợ (Người dùng chính), Hệ thống (Quản lý trạng thái và vị trí) |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc có (Must Have) |
| --- | --- |
| **Sự kiện kích hoạt** | Thợ chạm vào nút gạt (bật/tắt) ở ngay trên màn hình chính của ứng dụng. |
| --- | --- |
| **Điều kiện tiên quyết** | \- Thợ đã đăng nhập thành công và tài khoản hợp lệ (đã được duyệt, không bị khóa, không nợ tiền chiết khấu).<br><br>\- Điện thoại đang có mạng và đã cho phép ứng dụng sử dụng định vị (GPS). |
| --- | --- |
| **Kết quả đầu ra** | \- **(Khi Bật - Sẵn sàng):** Thợ chính thức xuất hiện trên bản đồ của khách hàng. Hệ thống bắt đầu theo dõi vị trí để gửi các công việc ở gần nhất.<br><br>\- **(Khi Tắt - Nghỉ ngơi):** Thợ "tàng hình" khỏi bản đồ. Hệ thống ngừng gửi công việc mới và ngừng theo dõi vị trí để giúp điện thoại của thợ tiết kiệm pin. |
| --- | --- |
| **Luồng cơ bản (Khi bắt đầu ngày làm việc)** | **Bước 1:** Thợ mở ứng dụng, thấy nút đang ở chế độ **Nghỉ ngơi** (màu xám) và bấm để bật lên.<br><br>**Bước 2:** Hệ thống kiểm tra nhanh xem tài khoản có đang bị khóa hay nợ tiền không, định vị điện thoại đã bật chưa.<br><br>**Bước 3:** Ứng dụng chốt vị trí hiện tại của thợ qua sóng GPS.<br><br>**Bước 4:** Trạng thái của thợ chuyển thành **Sẵn sàng**. Ứng dụng âm thầm cập nhật vị trí của thợ để luôn bắt được khách gần nhất.<br><br>**Bước 5:** Thợ bắt đầu hiện lên trên bản đồ của các khách hàng quanh đó.<br><br>**Bước 6:** Nút trên màn hình chuyển sang màu xanh lá, hiện dòng chữ: _"Bạn đã sẵn sàng — Hãy chờ công việc mới nhé!"_.<br><br>**Bước 7:** Thợ giữ ứng dụng mở và đợi hệ thống báo tin khi có khách gọi. |
| --- | --- |
| **Luồng thay thế (Khi muốn tạm nghỉ)** | \- **Thợ đang rảnh và muốn nghỉ:** Thợ gạt nút màu xanh để tắt. Hệ thống lập tức cho thợ "tàng hình" khỏi bản đồ, nút gạt chuyển về màu xám.<br><br>\- **Thợ đang làm dở việc nhưng muốn tắt:** Hệ thống sẽ hiện bảng hỏi: _"Bạn đang có đơn hàng chưa xong. Bạn có chắc muốn tạm nghỉ nhận việc mới không?"_<br><br>\+ Nếu thợ chọn **Đồng ý**: Ứng dụng chỉ chặn công việc mới gửi đến, còn việc đang làm dở thợ vẫn thao tác bình thường.<br><br>\+ Nếu thợ chọn **Hủy**: Ứng dụng giữ nguyên trạng thái Sẵn sàng. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Đang nợ tiền:** Thợ bấm bật nhưng hệ thống báo: _"Bạn đang nợ tiền chiết khấu. Vui lòng thanh toán vào Ví trước khi nhận việc."_ Nút bật bị giữ nguyên ở màu xám.<br><br>\- **Quên bật định vị:** Thợ bấm bật nhưng ứng dụng chưa được cấp quyền vị trí. Ứng dụng sẽ giải thích nhẹ nhàng: _"Hãy cho phép FixIt xem vị trí để tìm khách quanh đây giúp bạn nhé!"_ và dẫn thợ vào phần Cài đặt điện thoại.<br><br>\- **Mất sóng GPS:** Điện thoại không bắt được tọa độ (có thể do thợ đang ở dưới hầm). Ứng dụng nhắc nhở: _"Không xác định được vị trí của bạn. Vui lòng ra chỗ thoáng hơn và thử lại."_<br><br>\- **Mất mạng quá lâu:** Thợ đang ở trạng thái sẵn sàng nhưng đi vào vùng mất sóng quá 5 phút. Hệ thống tự động gạt thợ về chế độ "Nghỉ ngơi" để khách hàng không gọi nhầm. Khi có mạng lại, ứng dụng sẽ báo: _"Bạn vừa bị mất kết nối nên hệ thống đã tạm tắt chế độ sẵn sàng."_ |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1:** Khi khách thanh toán tiền mặt trực tiếp, hệ thống tự động trừ phần chiết khấu từ số dư ví thợ. Nếu ví không đủ số dư, hệ thống ghi nhận khoản nợ. Thợ có khoản nợ chiết khấu quá hạn trên 24 giờ không được phép bật trạng thái Sẵn sàng cho đến khi nạp tiền bù đủ vào ví.<br><br>\- **Quy định 2:** Để điện thoại của thợ không bị tụt pin nhanh, hệ thống sẽ lấy vị trí thưa ra khi thợ đang ngồi yên, và lấy liên tục khi thợ đang chạy xe.<br><br>\- **Quy định 3:** Nếu tài khoản bị bộ phận vận hành khóa đột xuất, thợ sẽ bị ép văng về trạng thái "Nghỉ ngơi" ngay lập tức và nhận được thông báo liên hệ tổng đài hỗ trợ. |
| --- | --- |

### 

### _Bảng Đặc tả Use Case: UC-W03a - Cập nhật và thông báo tiến độ di chuyển_

| **Thành phần** | Nội dung chi tiết |
| --- | --- |
| **Mã UC** | UC-W03a |
| --- | --- |
| **Tên UC** | Cập nhật và thông báo tiến độ di chuyển |
| --- | --- |
| **Mô tả** | Là một người thợ, tôi muốn hệ thống tự động cập nhật vị trí của mình cho khách hàng khi tôi đang trên đường đến. Việc này giúp khách hàng chủ động thời gian đón tiếp và tăng sự tin tưởng vào dịch vụ. |
| --- | --- |
| **Tác nhân** | Thợ (Tác nhân chính), Hệ thống (Ghi nhận GPS và điều phối), Khách hàng (Người theo dõi). |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc có (Must Have). |
| --- | --- |
| **Sự kiện kích hoạt** | Thợ bấm nút "Bắt đầu di chuyển" sau khi đã chấp nhận đơn hàng thành công. |
| --- | --- |
| **Điều kiện tiên quyết** | \- Thợ đã nhận đơn thành công (UC-W01).<br><br>\- Điện thoại thợ có kết nối mạng và đã bật quyền truy cập vị trí (GPS). |
| --- | --- |
| **Kết quả đầu ra** | \- Khách hàng theo dõi được vị trí thời gian thực của thợ trên bản đồ.<br><br>\- Trạng thái đơn hàng chuyển sang "Thợ đã đến nơi", sẵn sàng cho giai đoạn khảo sát/sửa chữa. |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | Bước 1: Thợ bấm nút "Bắt đầu di chuyển" trên màn hình chi tiết đơn hàng.<br><br>Bước 2: Hệ thống kích hoạt Foreground Service để lấy tọa độ GPS từ điện thoại thợ liên tục.<br><br>Bước 3: Hệ thống cập nhật vị trí này lên Redis và đẩy thông báo trạng thái "Thợ đang đến" cho khách hàng qua WebSocket.<br><br>Bước 4: Ứng dụng phía khách hàng mở bản đồ Live Tracking, hiển thị biểu tượng thợ đang di chuyển.<br><br>Bước 5: Thợ tới địa chỉ nhà khách, bấm nút "Đã đến nơi".<br><br>Bước 6: Hệ thống ngừng luồng Live Tracking, tắt bản đồ di chuyển và báo cho khách biết thợ đã có mặt tại cửa. |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- Cần trao đổi thêm về đường đi: Trong lúc di chuyển, thợ có thể bấm nút gọi điện hoặc nhắn tin (UC-Interaction) để hỏi khách hàng lối vào nhà hoặc mã cửa.<br><br>\- Sử dụng bản đồ hỗ trợ: Thợ bấm vào địa chỉ khách hàng để ứng dụng mở Google Maps điều hướng đường đi ngắn nhất. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- Mất tín hiệu GPS: Thợ đi vào vùng sóng yếu (hầm, nhà cao tầng). Ứng dụng cảnh báo: "Không xác định được vị trí của bạn. Vui lòng kiểm tra lại GPS".<br><br>\- Mất mạng internet: Nếu thợ mất kết nối quá 5 phút, hệ thống tự động báo cho khách hàng: "Kết nối với thợ bị gián đoạn, vui lòng liên hệ trực tiếp".<br><br>\- Gặp sự cố bất khả kháng: Thợ gặp tai nạn hoặc hỏng xe trên đường. Thợ thực hiện UC-W09 (Hủy công việc). Hệ thống báo lỗi cho khách và tự động tìm thợ thay thế. |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- Quy định 1 (Trình tự cố định): Thợ bắt buộc phải đi theo trình tự: Chấp nhận -> Đang di chuyển -> Đã đến nơi. Không được phép "nhảy cóc" trạng thái để đảm bảo tính xác thực của lộ trình.<br><br>\- Quy định 2 (Tần suất cập nhật): Để tiết kiệm pin, hệ thống lấy vị trí thưa ra khi thợ đứng yên và lấy liên tục khi thợ đang di chuyển với tốc độ trên 5km/h.<br><br>\- Quy định 3 (Quyền riêng tư): Live Tracking chỉ được kích hoạt từ lúc thợ bấm "Bắt đầu di chuyển" và phải kết thúc ngay khi thợ bấm "Đã đến nơi". |
| --- | --- |

### _Bảng Đặc tả Use Case: UC-W03b - Thực thi công việc và Nghiệm thu_

| **Thành phần** | **Nội dung chi tiết** |
| --- | --- |
| **Mã UC** | UC-W03b |
| --- | --- |
| **Tên UC** | Thực thi công việc và Nghiệm thu |
| --- | --- |
| **Mô tả** | Là một người thợ, tôi muốn báo cáo từng bước công việc mình đang làm và chụp ảnh lại trước/sau khi sửa. Việc này giúp khách hàng yên tâm theo dõi, đồng thời làm bằng chứng bảo vệ tôi nếu có thắc mắc sau này. |
| --- | --- |
| **Tác nhân** | Thợ (Người dùng chính), Hệ thống (Ghi nhận tiến độ và cất giữ hình ảnh), Khách hàng. |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc có (Must Have). |
| --- | --- |
| **Sự kiện kích hoạt** | Thợ bấm nút "Đã đến nơi" tại nhà khách hàng (Kết thúc UC-W03a). |
| --- | --- |
| **Điều kiện tiên quyết** | \- Thợ đã đến đúng vị trí khách hàng theo GPS.<br><br>\- Điện thoại có kết nối mạng, camera hoạt động tốt và đã cho phép ứng dụng sử dụng camera. |
| --- | --- |
| **Kết quả đầu ra** | \- Ảnh chụp hoàn công được cất giữ chặt chẽ làm bằng chứng (Proof of Work).<br><br>\- Tiền công được hệ thống tạm giữ an toàn để bảo hành và tự động cộng vào ví thợ sau 96 tiếng nếu không có khiếu nại. |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | Bước 1: Trước khi sửa, thợ dùng ứng dụng chụp lại tình trạng hỏng hóc hiện tại.<br><br>Bước 2: Hệ thống cất kỹ bức ảnh này làm bằng chứng ban đầu lên bộ lưu trữ (AWS S3/Clodinary).<br><br>Bước 3: Thợ bấm "Bắt đầu sửa chữa" và bắt tay vào làm.<br><br>Bước 4: Khách hàng nhận được thông báo thợ đang tiến hành công việc.<br><br>Bước 5: Sửa xong, thợ chụp lại thiết bị để chứng minh đã xử lý tốt.<br><br>Bước 6: Hệ thống cất bức ảnh thứ hai này vào bộ hồ sơ bảo hành.<br><br>Bước 7: Thợ bấm "Hoàn thành công việc" để báo khách ra nghiệm thu.<br><br>Bước 8: Hệ thống mời khách kiểm tra và kích hoạt trạng thái chờ thanh toán.<br><br>Bước 9: Tiền công nằm trong trạng thái chờ và tự động chảy vào ví thợ sau 96 tiếng nếu mọi thứ êm đẹp. |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- Cần báo giá lại: Khảo sát thấy bệnh nặng hơn dự kiến, thợ bấm "Đang khảo sát", chụp ảnh và nhắn tin báo giá mới cho khách. Khách ưng thì làm tiếp, khách chê đắt thì hai bên hủy đơn theo thỏa thuận (UC- hủy đơn hàng và bồi thường ).<br><br>\- Cần chạy thử thiết bị: Sửa xong thợ bấm "Đang kiểm tra kết quả" để khách biết. Thử xong thiết bị chạy tốt mới tiến hành chụp ảnh hoàn thành. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- Mạng yếu: Không tải được ảnh lên, ứng dụng báo thợ chờ hoặc kết nối lại. Bắt buộc tải xong ảnh mới cho làm bước tiếp theo.<br><br>\- Mất sóng giữa chừng: Ứng dụng lưu tạm ảnh vào bộ nhớ máy, khi có mạng sẽ tự động đẩy lên hệ thống.<br><br>\- Khách quên nghiệm thu: Quá 3 ngày (72 tiếng) khách im lặng, hệ thống tự động coi như khách đã hài lòng và bắt đầu đếm ngược 4 ngày giữ tiền bảo hành.<br><br>\- Thợ quên chụp ảnh: Thợ bấm "Hoàn thành" nhưng thiếu ảnh. Ứng dụng chặn lại và nhắc nhở: "Vui lòng chụp đủ ảnh trước và sau khi sửa để chốt đơn!". |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- Quy định 1: Thợ bắt buộc phải chụp đủ 2 kiểu ảnh (trước và sau). Tuyệt đối không được bỏ qua.<br><br>\- Quy định 2: Chỉ được dùng camera chụp trực tiếp trên ứng dụng, không lấy ảnh cũ từ thư viện để chống gian lận.<br><br>\- Quy định 3: Toàn bộ ảnh là "bằng chứng thép" để giải quyết khiếu nại trong 4 ngày bảo hành.<br><br>\- Quy định 4: Trình tự làm việc cố định: Đang khảo sát -> Đang sửa -> Hoàn thành. Không được bấm nhảy cóc bước.<br><br>\- Quy định 5: Tiền công bị "đóng băng" đúng 96 tiếng kể từ lúc chốt đơn để làm tin bảo hành.<br><br>\- Quy định 6: Sau 72 tiếng khách không ý kiến, hệ thống mặc định coi là đã đồng ý nghiệm thu. |
| --- | --- |

### _Bảng Đặc tả Use Case: UC-W04 – Xem tổng quan ví tài khoản_

| **Thành phần** | **Nội dung chi tiết** |
| --- | --- |
| **Mã UC** | UC-W04 |
| --- | --- |
| **Tên UC** | Xem tổng quan Ví tài khoản |
| --- | --- |
| **Mô tả** | Là người thợ, tôi muốn nhìn thấy rõ ràng tình hình tiền bạc của mình trên ứng dụng — bao gồm tiền có thể rút ngay, tiền đang bị tạm giữ chờ hết hạn bảo hành, và khoản đang nợ nền tảng — để dễ dàng quản lý thu nhập cá nhân. |
| --- | --- |
| **Tác nhân** | Thợ (Người dùng chính), Hệ thống (Tính toán và báo cáo tiền bạc) |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc có (Must Have) |
| --- | --- |
| **Sự kiện kích hoạt** | Thợ chạm vào mục **"Ví"** ở thanh công cụ bên dưới màn hình ứng dụng. |
| --- | --- |
| **Điều kiện tiên quyết** | \- Thợ đã đăng nhập và tài khoản đang hoạt động bình thường.<br><br>\- Điện thoại đang có kết nối mạng để tải số liệu mới nhất. |
| --- | --- |
| **Kết quả đầu ra** | \- Thợ nắm được bức tranh thu nhập chia làm 3 khoản rõ ràng: Tiền rút được ngay (Khả dụng), Tiền đang chờ bảo hành, và Khoản đang nợ.<br><br>\- Thợ thấy được đồng hồ đếm ngược báo khi nào tiền bảo hành sẽ "chảy" vào ví.<br><br>\- Các nút Nạp/Rút tiền sẽ tự động sáng lên hoặc mờ đi tùy theo tình trạng thực tế của ví. |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | **Bước 1:** Thợ bấm vào mục "Ví".<br><br>**Bước 2:** Ứng dụng tải dữ liệu và chia màn hình thành 3 ô dễ nhìn.<br><br>**Bước 3:** Hiện ô **Tiền Khả dụng** — Đây là khoản tiền tươi thóc thật, thợ có thể rút về ngân hàng ngay.<br><br>**Bước 4:** Hiện ô **Tiền đang Bảo hành** — Tiền công từ các đơn vừa làm xong bị giữ lại 4 ngày. Ứng dụng hiện rõ từng đơn và đồng hồ đếm ngược thời gian còn lại.<br><br>**Bước 5:** Hiện ô **Khoản nợ** — Số tiền thợ đang nợ ứng dụng (thường do khách trả tiền mặt trực tiếp cho thợ nên thợ chưa đóng phần chiết khấu lại cho ứng dụng).<br><br>**Bước 6:** Hiển thị nút "Nạp tiền" và "Rút tiền".<br><br>**Bước 7:** Thợ xem xong và tự quyết định rút tiền, nạp tiền trả nợ hoặc thoát ra đi làm tiếp. |
| --- | --- |
| **Luồng thay thế (Các trường hợp khác)** | \- **Không có tiền chờ bảo hành:** Ứng dụng hiện số 0 và giải thích nhẹ nhàng: _"Tiền công sẽ được tạm giữ 4 ngày để bảo hành trước khi cộng vào ví bạn nhé."_<br><br>\- **Không mắc nợ:** Ô khoản nợ hiện màu xanh _"Không có khoản nợ"_. Nút "Rút tiền" sáng lên mời gọi rút về ngân hàng.<br><br>\- **Tiền bảo hành vừa "chín":** Nếu thợ đang mở xem ví mà đúng lúc đơn hàng hết 4 ngày chờ, số tiền đó sẽ tự động nhảy từ ô "Bảo hành" sang ô "Khả dụng", kèm thông báo nhỏ: _"Tiền công từ đơn \[Mã đơn\] vừa chảy vào ví của bạn!"_<br><br>\- **Thợ đang nợ ứng dụng:** Nút "Rút tiền" bị khóa mờ đi, ứng dụng nhắc khéo: _"Bạn cần thanh toán khoản nợ chiết khấu trước khi rút tiền nhé."_ Đồng thời nút "Nạp tiền" sẽ nhấp nháy để thợ biết cần bấm vào đâu. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Mất mạng:** Ứng dụng không lấy được số mới, đành hiện số cũ lưu trong máy kèm cảnh báo: _"Đang hiện số liệu cũ do mất mạng. Vui lòng kết nối lại."_ Khóa tạm nút nạp/rút tiền để tránh lỗi.<br><br>\- **Hệ thống chưa tính kịp:** Thợ vừa làm xong đơn xong vào ví xem ngay, hệ thống chưa cộng xong. Ứng dụng sẽ hiện biểu tượng xoay vòng: _"Đang tính toán lại số dư, bạn đợi một chút nhé"_ và tự làm mới sau 10 giây. |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1:** Tiền bạc hiển thị phải chính xác tuyệt đối từng đồng ngay tại thời điểm xem, không được làm tròn hay ước lượng.<br><br>\- **Quy định 2:** Nếu khách trả tiền mặt, ứng dụng tự trừ tiền chiết khấu trong ví. Nếu ví hết tiền thì thợ sẽ bị ghi nợ.<br><br>\- **Quy định 3:** Tiền công bị "đóng băng" đúng 96 tiếng từ lúc khách chốt nghiệm thu. Hết thời gian mà không có khiếu nại thì tự động rã đông sang ô Khả dụng.<br><br>\- **Quy định 4:** Cứ có nợ là không được rút tiền, dù trong ví có bao nhiêu đi nữa. Phải sòng phẳng trả nợ xong mới được rút.<br><br>\- **Quy định 5:** Tiền đang đóng băng bảo hành là tiền "chết", không được rút, cũng không được lấy để trừ nợ.<br><br>\- **Quy định 6:** Trong 4 ngày đóng băng, nếu khách khiếu nại đúng lỗi do thợ, hệ thống sẽ trích tiền từ khoản này để đền bù trước khi trả phần còn lại cho thợ. |
| --- | --- |

### _Bảng Đặc tả Use Case: UC-W05 – Nạp tiền vào ví trả nợ ứng dụng_

| **Thành phần** | **Nội dung chi tiết** |
| --- | --- |
| **Mã UC** | UC-W05 |
| --- | --- |
| **Tên UC** | Nạp tiền vào ví trả nợ ứng dụng |
| --- | --- |
| **Mô tả** | Khi khách trả tiền mặt trực tiếp cho tôi, ứng dụng không thu được phần chiết khấu nên tôi sẽ bị ghi nợ. Tôi muốn có thể dễ dàng nạp tiền từ ngân hàng hoặc ví điện tử vào ứng dụng để trả dứt điểm khoản nợ này, giúp tài khoản không bị khóa và tiếp tục đi làm bình thường. |
| --- | --- |
| **Tác nhân** | Thợ (Người dùng chính), Hệ thống thanh toán (VNPAY / MoMo / ZaloPay), Hệ thống FixIt (Cộng tiền và xóa nợ) |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc có (Must Have) |
| --- | --- |
| **Sự kiện kích hoạt** | Thợ thấy thông báo đang nợ tiền ứng dụng và bấm vào nút **"Nạp tiền"** ở trong mục Ví. |
| --- | --- |
| **Điều kiện tiên quyết** | \- Thợ đang bị ghi nợ tiền chiết khấu trên ứng dụng.<br><br>\- Thợ có dùng ngân hàng hoặc ví điện tử (MoMo, ZaloPay...) và trong đó còn đủ tiền để nạp.<br><br>\- Điện thoại đang có mạng internet. |
| --- | --- |
| **Kết quả đầu ra** | \- **(Nạp thành công):** Tiền chạy vào ví ứng dụng và lập tức tự động trả hết khoản nợ. Thợ nhận được thông báo "Đã sạch nợ" và có thể bật lại chế độ nhận việc.<br><br>\- **(Nạp dư ra):** Trả nợ xong mà vẫn thừa tiền, số tiền dư đó nằm sẵn trong ví ứng dụng, thợ có thể để đó trừ dần cho các đơn sau hoặc rút ngược lại về ngân hàng.<br><br>\- **(Nạp lỗi):** Tiền trong thẻ ngân hàng không bị trừ, khoản nợ trên ứng dụng vẫn còn nguyên. Ứng dụng báo lỗi để thợ biết đường thử lại. |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | **Bước 1:** Thợ vào mục Ví, nhìn thấy khoản tiền đang nợ và bấm "Nạp tiền".<br><br>**Bước 2:** Ứng dụng tự động điền sẵn số tiền bằng đúng khoản nợ để thợ đỡ phải gõ lại.<br><br>**Bước 3:** Thợ chọn nạp qua MoMo, ZaloPay hoặc Ngân hàng.<br><br>**Bước 4:** Ứng dụng chuyển thợ sang màn hình của MoMo/Ngân hàng để xác nhận.<br><br>**Bước 5:** Thợ bấm xác nhận thanh toán (quẹt FaceID hoặc nhập mã OTP của ngân hàng).<br><br>**Bước 6:** Ngân hàng báo thanh toán thành công về cho ứng dụng FixIt.<br><br>**Bước 7:** FixIt lập tức cộng tiền vào ví và tự động lấy đúng số đó đập vào khoản nợ.<br><br>**Bước 8:** Màn hình Ví chớp lại: Khoản nợ nhảy về số 0. Ứng dụng báo tin vui: _"Nạp tiền thành công! Bạn đã sạch nợ."_<br><br>**Bước 9:** Thợ yên tâm quay ra bật lại nút "Sẵn sàng nhận việc" để đi làm tiếp. |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- **Thợ muốn nạp dư ra:** Thay vì chỉ nạp đủ trả nợ, thợ tự gõ số tiền lớn hơn (ví dụ nợ 50k nhưng nạp luôn 200k). Ứng dụng vẫn cho phép. Nạp xong, ứng dụng tự lấy 50k trả nợ, 150k còn lại cất vào phần "Tiền rút được ngay" trong ví.<br><br>\- **Đổi ý chọn ngân hàng khác:** Đang chọn nạp qua MoMo nhưng chợt nhớ MoMo hết tiền, thợ bấm nút quay lại và chọn nạp qua ZaloPay hoặc Ngân hàng bình thường.<br><br>\- **Ngân hàng xử lý chậm:** Thợ nạp xong nhưng ngân hàng báo về ứng dụng hơi chậm. Ứng dụng sẽ hiện chữ: _"Đang xử lý giao dịch, bạn chờ chút nhé"_. Khi nào ngân hàng báo xong, ứng dụng sẽ tự xóa nợ mà thợ không cần làm gì thêm. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Thẻ ngân hàng bị lỗi:** Có thể do thẻ hết tiền hoặc nhập sai mã OTP. Ứng dụng báo: _"Giao dịch thất bại. Bạn kiểm tra lại thẻ hoặc thử cách khác nhé"_. Khoản nợ vẫn còn nguyên.<br><br>\- **Đang nạp thì rớt mạng:** Thợ vừa bấm xác nhận bên ngân hàng xong thì mất 4G. Đừng lo, ngân hàng vẫn sẽ báo về máy chủ FixIt. Khi có mạng lại, thợ mở ứng dụng lên sẽ thấy nợ đã được xóa, không sợ bị mất oan tiền.<br><br>\- **Thợ nhập số tiền nạp ít hơn số nợ:** Thợ đang nợ 100k nhưng táy máy gõ thử nạp 50k. Ứng dụng sẽ chặn lại ngay: _"Bạn phải nạp ít nhất đủ 100k để trả hết nợ đã nhé!"_ và không cho qua bước chọn ngân hàng. |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1:** Cứ mỗi lần khách trả tiền mặt 100% cho thợ, thợ đang cầm phần tiền chiết khấu của ứng dụng. Ứng dụng sẽ tự móc túi thợ (trừ tiền trong ví) để thu lại. Nếu ví thợ sạch bách không còn đồng nào, ứng dụng đành ghi "sổ nợ" và bắt thợ phải nạp tiền từ ngoài vào để trả.<br><br>\- **Quy định 2:** Đã trả nợ là phải trả đứt đuôi. Không cho phép thợ trả góp (ví dụ nợ 100k không được nạp trả trước 50k).<br><br>\- **Quy định 3:** Tiền của thợ nằm ở ô "Đang chờ bảo hành" là tiền đóng băng. Ứng dụng tuyệt đối không tự ý lấy tiền bảo hành của thợ để cấn trừ nợ. Thợ bắt buộc phải nạp tiền tươi từ ngoài vào.<br><br>\- **Quy định 4:** Lịch sử nạp tiền luôn được lưu lại rõ ràng từng phút, từng giây để thợ tiện đối soát nếu thấy có gì sai sót. |
| --- | --- |

### _Bảng Đặc tả Use Case: UC-W06 – Rút tiền về ngân hàng_

| **Thành phần** | **Nội dung chi tiết** |
| --- | --- |
| **Mã UC** | UC-W06 |
| --- | --- |
| **Tên UC** | Rút tiền về ngân hàng |
| --- | --- |
| **Mô tả** | Là người thợ, tôi muốn rút phần tiền mồ hôi công sức của mình (tiền rút được ngay) từ ví ứng dụng về tài khoản ngân hàng để chi tiêu hàng ngày, sau khi đã làm xong việc và sòng phẳng nợ nần với ứng dụng. |
| --- | --- |
| **Tác nhân** | Thợ (Người dùng chính), Ứng dụng FixIt và Hệ thống Ngân hàng. |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc có (Must Have) |
| --- | --- |
| **Sự kiện kích hoạt** | Thợ chạm vào nút **"Rút tiền"** ở trong màn hình Ví. |
| --- | --- |
| **Điều kiện tiên quyết** | \- Thợ đang có tiền trong ô "Tiền Khả dụng" (Tiền tươi thóc thật).<br><br>\- Thợ đang "sạch nợ", không nợ đồng nào tiền chiết khấu của ứng dụng.<br><br>\- Thợ đã cài sẵn số tài khoản ngân hàng nhận tiền.<br><br>\- Điện thoại đang có kết nối mạng. |
| --- | --- |
| **Kết quả đầu ra** | \- **(Thành công):** Tiền trong ví ứng dụng bị trừ đi và chuyển thẳng về tài khoản ngân hàng của thợ. Thợ nhận được thông báo tiền đang trên đường về.<br><br>\- **(Thất bại):** Nếu thẻ ngân hàng lỗi, ứng dụng sẽ hoàn tiền y nguyên lại vào ví, không để thợ mất đi đâu đồng nào.<br><br>\- Mọi lịch sử rút tiền đều được ghi chép cẩn thận. |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | **Bước 1:** Thợ vào Ví, thấy mình đang có tiền và bấm nút "Rút tiền".<br><br>**Bước 2:** Ứng dụng rà soát nhanh thấy thợ không nợ nần gì nên cho qua.<br><br>**Bước 3:** Ứng dụng hiện ra màn hình rút tiền, báo cáo rõ đang có bao nhiêu tiền và sẽ chuyển về ngân hàng nào.<br><br>**Bước 4:** Thợ gõ số tiền muốn rút rồi bấm "Xác nhận".<br><br>**Bước 5:** Để tránh bị người khác cầm máy ăn cắp tiền, ứng dụng yêu cầu thợ quét vân tay, Face ID hoặc nhập mật khẩu.<br><br>**Bước 6:** Thợ xác thực chính chủ thành công.<br><br>**Bước 7:** Ứng dụng lập tức trừ tiền trong ví để giữ chỗ, đồng thời đánh điện sang ngân hàng yêu cầu chuyển khoản.<br><br>**Bước 8:** Ngân hàng xử lý và bơm tiền vào tài khoản thực của thợ.<br><br>**Bước 9:** Ứng dụng báo tin vui: _"Rút tiền thành công! Tiền đã được chuyển về tài khoản của bạn."_ |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- **Muốn rút sạch ví:** Thợ không cần gõ từng con số, chỉ cần bấm nút "Rút tất cả", ứng dụng sẽ tự động điền tối đa số tiền đang có.<br><br>\- **Muốn chuyển về thẻ khác:** Đang cài thẻ Vietcombank nhưng thợ muốn chuyển về Techcombank. Thợ bấm "Đổi tài khoản" để chọn ngân hàng khác đã liên kết.<br><br>\- **Rút vào ban đêm/cuối tuần:** Nếu ngân hàng nghỉ làm việc và xử lý chậm, ứng dụng sẽ dặn dò: _"Lệnh rút tiền đã được ghi nhận. Tiền sẽ về tài khoản của bạn trong 1-2 ngày làm việc tới."_ Thợ cứ yên tâm chờ đợi. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Đang mắc nợ:** Ứng dụng thấy thợ còn nợ tiền chiết khấu, nút Rút tiền lập tức bị khóa. Ứng dụng nhắc: _"Bạn cần trả nợ xong mới được rút tiền nhé!"_ và mời thợ ra nạp tiền trả nợ.<br><br>\- **Rút quá tay:** Trong ví có 500k nhưng thợ gõ nhầm thành 5 triệu. Ứng dụng chặn lại ngay: _"Bạn chỉ được rút tối đa \[số tiền đang có\] thôi."_<br><br>\- **Quét vân tay/mật khẩu sai:** Nếu nhập sai quá 3 lần, ứng dụng nghi ngờ có người lạ cầm máy nên tự động khóa tính năng rút tiền trong 30 phút để bảo vệ tài sản cho thợ.<br><br>\- **Ngân hàng báo lỗi:** Thẻ của thợ bị khóa hoặc ngân hàng bảo trì. Ứng dụng lập tức nhả tiền trả lại vào ví cho thợ và báo: _"Rút tiền thất bại. Tiền đã được hoàn lại vào ví, bạn kiểm tra lại thẻ nhé."_<br><br>\- **Rớt mạng khi đang rút:** Vừa bấm vân tay xong thì mất 4G. Ứng dụng vẫn âm thầm xử lý an toàn ở máy chủ. Khi có mạng lại, hệ thống sẽ hiện kết quả cuối cùng, đảm bảo thợ không bao giờ bị trừ tiền 2 lần. |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1:** Cứ có nợ là tuyệt đối cấm rút tiền, bắt buộc phải trả nợ sòng phẳng mới được rút.<br><br>\- **Quy định 2:** Số tiền rút mỗi lần ít nhất phải là 50.000 VNĐ.<br><br>\- **Quy định 3:** Ngay khi thợ bấm rút, hệ thống phải trừ tiền trong ví ngay lập tức trước khi báo ngân hàng, tránh trường hợp máy lag thợ bấm 2-3 lần liên tiếp rút lố tiền.<br><br>\- **Quy định 4:** Nếu lỗi do ngân hàng, ứng dụng bắt buộc phải hoàn tiền về ví cho thợ chậm nhất trong vòng 15 phút.<br><br>\- **Quy định 5:** Mỗi lệnh rút chỉ thực hiện đúng một lần, dù mạng chập chờn gửi lệnh lên máy chủ 2 lần thì hệ thống vẫn biết cách gạt bỏ để bảo vệ quỹ.<br><br>\- **Quy định 6:** Tiền chỉ được chuyển đi khi đã qua bước kiểm tra bảo mật (vân tay, khuôn mặt, mã PIN). |
| --- | --- |

## 

### _Bảng Đặc tả Use Case: UC-W07 – Nhận và phản hồi khiến nại ( Bảo hành )_

| **Thành phần** | **Nội dung chi tiết** |
| --- | --- |
| **Mã UC** | UC-W07 |
| --- | --- |
| **Tên UC** | Nhận và Phản hồi khiếu nại (Bảo hành) |
| --- | --- |
| **Mô tả** | Là một người thợ, tôi muốn được báo ngay lập tức nếu khách hàng phàn nàn về đồ vừa sửa trong thời gian bảo hành 4 ngày. Tôi cần cơ hội để giải thích và đưa ra bằng chứng bảo vệ công sức của mình trước khi Ban quản lý (Admin) đứng ra phân xử. |
| --- | --- |
| **Tác nhân** | Thợ (Người bị khiếu nại), Khách hàng (Người phàn nàn), Ban quản lý/Admin (Người phân xử), Ứng dụng FixIt (Gửi thông báo & Lưu bằng chứng). |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc có (Must Have) |
| --- | --- |
| **Sự kiện kích hoạt** | Khách hàng báo lỗi/chưa hài lòng trong vòng 4 ngày (96 tiếng) sau khi sửa xong. Ứng dụng lập tức "đánh điện" báo cho thợ. |
| --- | --- |
| **Điều kiện tiên quyết** | \- Đã sửa xong và khách đã xác nhận nghiệm thu.<br><br>\- Vẫn đang trong "thời gian vàng" 4 ngày bảo hành.<br><br>\- Tiền công của đơn này vẫn đang bị ứng dụng "đóng băng" ở trạng thái chờ. |
| --- | --- |
| **Kết quả đầu ra** | \- **(Thợ giải trình kịp thời):** Bằng chứng của thợ được gửi đi. Ban quản lý có đủ thông tin hai chiều để phân xử công bằng.<br><br>\- **(Thợ thắng kiện):** Toàn bộ tiền bị đóng băng được trả thẳng vào ví thợ (ô Khả dụng).<br><br>\- **(Khách thắng kiện):** Một phần hoặc toàn bộ tiền đóng băng được hoàn lại cho khách. Thợ nhận được thông báo giải thích rõ lý do.<br><br>\- **(Thợ im lặng):** Nếu thợ bỏ mặc không phản hồi, hệ thống ghi nhận là không hợp tác và Admin sẽ xử thua, dựa hoàn toàn vào lời của khách. |
| --- | --- |
| **Luồng cơ bản (Quá trình phân xử tiêu chuẩn)** | **Bước 1:** Ứng dụng nhận được phàn nàn từ khách kèm ảnh chụp. Lập tức báo cho thợ: _"Khách vừa báo lỗi đơn \[Mã đơn\]. Bạn có 24 giờ để lên tiếng nhé."_<br><br>**Bước 2:** Thợ bấm vào thông báo để mở ứng dụng ra xem.<br><br>**Bước 3:** Màn hình hiện rõ: Khách chê ở đâu, ảnh chụp tình trạng ra sao, số tiền đang bị giữ là bao nhiêu, và một đồng hồ đếm ngược 24 tiếng.<br><br>**Bước 4:** Thợ đọc kỹ, nhớ lại lúc mình làm và lôi ảnh chụp trước/sau ra đối chiếu.<br><br>**Bước 5:** Thợ bấm nút "Phản hồi", viết vài dòng giải thích sự tình và đính kèm hình ảnh làm bằng chứng.<br><br>**Bước 6:** Hệ thống cất kỹ lời giải thích này và báo cho Ban quản lý (Admin) vào phân xử.<br><br>**Bước 7:** Ban quản lý xem xét lời nói và hình ảnh của cả hai bên để đưa ra quyết định cuối cùng.<br><br>**Bước 8:** Ứng dụng thực hiện phán quyết: Chia lại tiền bảo hành (trả cho thợ hoặc hoàn cho khách).<br><br>**Bước 9:** Báo tin kết quả cho thợ: _"Vụ khiếu nại đơn \[Mã đơn\] đã có kết quả..."_ và cập nhật lại tiền trong ví.<br><br>**Bước 10:** Thợ xem kết quả và kiểm tra ví. |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- **Thợ nhận sai và muốn đền bù:** Thấy khách nói đúng, thợ bấm "Đề xuất hòa giải", tự nhập số tiền muốn trích ra đền cho khách. Ban quản lý duyệt thấy hợp lý là xong, không cần cãi nhau tốn thời gian.<br><br>\- **Muốn xem lại ảnh cũ:** Thợ bấm "Xem ảnh thi công" để ứng dụng lôi lại bộ ảnh cũ (chụp lúc vừa sửa xong) ra cho thợ nhớ lại chi tiết trước khi cãi lý.<br><br>\- **Ban quản lý cần hỏi thêm:** Thấy chưa rõ ràng, Admin yêu cầu thợ bổ sung thông tin. Ứng dụng báo: _"Admin cần bạn giải thích thêm, hạn chót 12 tiếng nhé."_ |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Thợ lặn mất tăm (Quá 24h):** Khi thời gian chỉ còn 6 tiếng, ứng dụng sẽ nhắc khéo. Nếu hết 24 tiếng thợ vẫn im lặng, hệ thống chốt "Thợ không hợp tác" và Admin xử lý luôn theo ý khách. Thợ ráng chịu thiệt.<br><br>\- **Khách kiện muộn (Quá 4 ngày):** Khách gửi khiếu nại khi đã qua 96 tiếng. Ứng dụng chặn luôn: _"Hết hạn bảo hành rồi ạ!"_ Tiền lúc này đã an vị trong ví thợ, khiếu nại tự động bị hủy bỏ.<br><br>\- **Thợ cãi chay (Không gửi ảnh):** Thợ gửi lời giải thích nhưng quên đính kèm ảnh. Ứng dụng nhắc nhẹ: _"Có ảnh bằng chứng thì dễ thắng hơn đó, bạn có chắc muốn gửi luôn không?"_<br><br>\- **Đang viết thì rớt mạng:** Đang hì hục gõ chữ thì điện thoại mất 4G. Ứng dụng khôn khéo tự lưu nháp, lúc sau có mạng thợ vào viết tiếp, không lo phải gõ lại từ đầu. |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1 (Luật 4 ngày):** Chỉ nhận khiếu nại trong đúng 4 ngày (96 tiếng) kể từ lúc sửa xong. Qua ngày là miễn bàn.<br><br>\- **Quy định 2 (Luật 24 giờ):** Thợ có đúng 1 ngày (24 tiếng) để tự bảo vệ mình. Im lặng bị coi là đuối lý và nhận lỗi.<br><br>\- **Quy định 3 (Kim bài miễn tử):** Bức ảnh chụp trước/sau lúc hoàn thành việc là bằng chứng gốc có giá trị cao nhất. Không ai cãi được ảnh này.<br><br>\- **Quy định 4:** Chỉ có Ban quản lý (Admin) mới có quyền chốt hạ cuối cùng, hai bên không được tự ý bóp méo số tiền đang bị giữ (trừ khi thợ chủ động xin hòa giải).<br><br>\- **Quy định 5:** Phán quyết của Admin là quyết định chốt hạ, ứng dụng trừ tiền hoặc cộng tiền ngay lập tức.<br><br>\- **Quy định 6:** Mọi lời cãi vã, hình ảnh đều được lưu vào "sổ đen" vĩnh viễn, không ai được xóa để làm bằng chứng về sau nếu có kiện cáo lên cao hơn.<br><br>\- **Quy định 7:** Thợ nào bị xử thua 3 lần trong vòng 1 tháng (30 ngày) sẽ bị Admin sờ gáy, nhẹ thì cảnh cáo, nặng thì khóa tài khoản nghỉ làm. |
| --- | --- |

### _Bảng Đặc tả Use Case: UC-W08 – Bảng tổng kết công việc_

| **Thành phần** | **Nội dung chi tiết** |
| --- | --- |
| **Mã UC** | UC-W08 |
| --- | --- |
| **Tên UC** | Xem Bảng tổng kết công việc & Lịch hẹn |
| --- | --- |
| **Mô tả** | Là một người thợ, tôi muốn xem một màn hình tổng hợp mọi thứ: từ lịch những nhà khách đã hẹn trước, biểu đồ tiền kiếm được cho đến tiến độ nhận thưởng chuyên cần — để tôi chủ động sắp xếp thời gian và có thêm động lực làm việc. |
| --- | --- |
| **Tác nhân** | Thợ (Người dùng chính), Hệ thống FixIt (Tổng hợp và báo cáo dữ liệu). |
| --- | --- |
| **Độ ưu tiên** | Nên có (Nice to Have) |
| --- | --- |
| **Sự kiện kích hoạt** | Thợ chạm vào mục **"Thống kê"** hoặc **"Thu nhập"** ở thanh công cụ dưới cùng ứng dụng. |
| --- | --- |
| **Điều kiện tiên quyết** | \- Thợ đã đăng nhập vào ứng dụng.<br><br>\- Điện thoại đang có mạng để cập nhật những con số mới nhất. |
| --- | --- |
| **Kết quả đầu ra** | \- Thợ nắm rõ lịch trình làm việc sắp tới để không bị lỡ hẹn với khách.<br><br>\- Thợ biết chính xác mình đã kiếm được bao nhiêu tiền và cần làm thêm bao nhiêu đơn nữa để được thưởng thêm.<br><br>\- Mọi thông tin thu nhập đều được trình bày bằng biểu đồ dễ nhìn, không cần tính toán thủ công. |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | **Bước 1:** Thợ bấm vào tab "Thống kê" trên màn hình.<br><br>**Bước 2:** Ứng dụng "gom" tất cả dữ liệu về tiền bạc, lịch hẹn và quà thưởng để hiện ra một lần cho nhanh.<br><br>**Bước 3:** Hiện ô **Lịch làm việc sắp tới** — Liệt kê các đơn khách đã đặt lịch hẹn giờ (hôm nay, ngày mai...), báo rõ mấy giờ, làm gì và ở khu vực nào.<br><br>**Bước 4:** Hiện ô **Tiền kiếm được** — Vẽ thành biểu đồ cột theo tuần để thợ thấy hôm nào mình kiếm khá nhất, tổng số đơn đã làm và thu nhập trung bình mỗi ngày.<br><br>**Bước 5:** Hiện ô **Thưởng chuyên cần (KPI)** — Hiện thanh tiến độ (ví dụ: làm 7/10 đơn sẽ được thưởng ngày), kèm số tiền thưởng đã tích lũy được trong tháng.<br><br>**Bước 6:** Thợ xem tổng quan và có thể bấm vào biểu đồ để xem chi tiết từng ngày. |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- **Đổi mốc thời gian xem tiền:** Thợ muốn xem tháng trước kiếm được bao nhiêu. Thợ chọn lọc theo "Tháng trước", ứng dụng lập tức vẽ lại biểu đồ và nhảy số tiền tương ứng.<br><br>\- **Xem kỹ một đơn hẹn:** Thợ thấy lịch hẹn chiều nay, bấm vào đơn đó để xem lại địa chỉ nhà khách hoặc gọi điện xác nhận lại lịch.<br><br>\- **Sắp được thưởng:** Khi thợ chỉ còn thiếu 1-2 đơn là đạt mốc thưởng, ứng dụng hiện dòng chữ động viên: _"Cố lên! Chỉ 1 đơn nữa là bạn được thưởng thêm 100k hôm nay rồi!"_<br><br>\- **Vừa nhận thưởng xong:** Nếu thợ vừa làm xong đơn và đủ mốc thưởng, ứng dụng hiện một thông báo chúc mừng kèm số tiền thưởng vừa được cộng vào ví. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Mất mạng giữa chừng:** Ứng dụng không lấy được số mới nên đành hiện lại số cũ từ lần xem trước và dặn: _"Đang hiện số liệu cũ do mất mạng, bạn hãy kết nối lại nhé."_<br><br>\- **Người thợ mới tinh:** Thợ mới đăng ký, chưa làm đơn nào. Thay vì hiện biểu đồ trống, ứng dụng hiện lời chào ấm áp: _"Chào mừng bạn tham gia FixIt! Hãy bật sẵn sàng để bắt đầu những đơn hàng đầu tiên nhé!"_<br><br>\- **Mạng quá yếu:** Nếu dữ liệu tải về quá chậm, ứng dụng hiện phần nào xong trước thì cho thợ xem trước (ví dụ hiện lịch hẹn trước), phần biểu đồ tiền nong hiện sau, không bắt thợ phải đợi cả trang. |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1:** Tiền hiện ở trang Thống kê phải khớp từng đồng với số tiền trong Ví. Tuyệt đối không để lệch số.<br><br>\- **Quy định 2:** Lịch làm việc chỉ hiện những đơn đã hẹn giờ cụ thể. Những đơn "Đến ngay" sẽ không hiện ở đây vì thợ phải đi luôn.<br><br>\- **Quy định 3:** Các mốc thưởng (KPI) sẽ do Ban quản lý (Admin) quy định và thay đổi tùy theo chương trình khuyến mãi của từng tháng.<br><br>\- **Quy định 4:** Biểu đồ tiền chỉ tính những khoản tiền thợ chắc chắn đã nhận được (Khả dụng), không tính tiền đang bị "đóng băng" bảo hành để thợ không bị nhầm lẫn.<br><br>\- **Quy định 5:** Ứng dụng phải tải dữ liệu cực nhanh và mượt, giúp thợ tiết kiệm dung lượng 4G khi đang ở ngoài đường.<br><br>\- **Quy định 6:** Thợ có thể xem lại lịch sử nhận thưởng trong vòng 1 năm để đối soát nếu cần. |
| --- | --- |

### _Bảng Đặc tả Use Case: UC-W09 – Hủy công việc ( thợ )_

| **Thành phần** | Nội dung chi tiết |
| --- | --- |
| **Mã UC** | UC-W09 |
| --- | --- |
| **Tên UC** | Hủy công việc (Từ phía thợ) |
| --- | --- |
| **Mô tả** | Là một người thợ, tôi muốn có thể chủ động hủy công việc đã nhận nếu gặp sự cố bất khả kháng (tai nạn, hỏng xe...). Dù hiểu việc này sẽ làm giảm điểm uy tín, tôi vẫn cần ứng dụng cảnh báo rõ hậu quả trước khi tôi bấm chốt để không vô tình thao tác nhầm. |
| --- | --- |
| **Tác nhân** | Thợ (Người hủy đơn), Khách hàng (Người bị ảnh hưởng), Ứng dụng FixIt (Phân xử và tìm người thay thế). |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc có (Must Have) |
| --- | --- |
| **Sự kiện kích hoạt** | Thợ bấm vào nút "Hủy đơn" (thường giấu trong menu "..." ở góc màn hình để tránh bấm nhầm) khi đang trên đường đi hoặc đang khảo sát. |
| --- | --- |
| **Điều kiện tiên quyết** | \- Thợ chưa bắt tay vào sửa (chưa chuyển sang trạng thái "Đang sửa chữa").<br><br>\- Tài khoản thợ đang hoạt động bình thường, điện thoại có kết nối mạng. |
| --- | --- |
| **Kết quả đầu ra** | \- (Hủy thành công): Đơn bị hủy. Khách được thông báo ngay và hệ thống lập tức cử thợ khác đến cứu nét. Thợ bị trừ điểm uy tín.<br><br>\- (Thợ đổi ý): Thợ rút lại quyết định ở phút chót, mọi thứ giữ nguyên và tiếp tục làm việc bình thường. |
| --- | --- |
| **Luồng cơ bản (Quá trình hủy chuẩn)** | Bước 1: Thợ mở menu góc, bấm chọn "Hủy đơn".<br><br>Bước 2: Ứng dụng bật bảng cảnh báo đỏ: "Bạn đang hủy đơn lúc đã nhận việc. Khách sẽ bị ảnh hưởng và điểm uy tín của bạn sẽ bị trừ. Bạn có chắc chắn không?"<br><br>Bước 3: Ứng dụng bắt buộc thợ phải chọn lý do (Ốm đau, Hỏng xe, Sự cố gia đình...).<br><br>Bước 4: Thợ bấm "Xác nhận hủy đơn".<br><br>Bước 5: Ứng dụng lập tức báo tin xin lỗi khách: "Rất tiếc, thợ vừa gặp sự cố nên phải hủy đơn. FixIt đang điều phối thợ khác cho bạn ngay lập tức."<br><br>Bước 6: Hệ thống tự động quét bản đồ tìm thợ rảnh khác để thay thế.<br><br>Bước 7: Hệ thống trừ điểm uy tín của thợ và ghi vào "sổ đen".<br><br>Bước 8: Báo lại cho thợ: "Đơn đã được hủy. Điểm uy tín của bạn vừa bị điều chỉnh giảm." |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- Ca khó quá làm không được: Thợ đến tận nơi mới phát hiện bệnh quá nặng, thiếu đồ nghề. Thợ chọn lý do "Ngoài khả năng chuyên môn". Hệ thống sẽ châm chước không trừ điểm (nếu đây là lần đầu trong tháng) vì thợ đã có thiện chí đến tận nhà khách.<br><br>\- Khách vui vẻ cho hủy: Khách chủ động đổi ý và đồng ý cho thợ hủy qua tin nhắn. Thợ chọn "Hủy theo thỏa thuận". Hệ thống ghi nhận đây là ca hủy êm đẹp, không phạt điểm nặng thợ.<br><br>\- Thợ đổi ý: Đang đọc cảnh báo thấy sợ bị trừ điểm quá, thợ bấm "Không hủy nữa, làm tiếp". Ứng dụng tắt bảng thông báo, thợ tiếp tục công việc như chưa có gì xảy ra. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- Đang sửa dở thì cấm hủy: Nếu thợ đã bấm "Bắt đầu sửa chữa", nút Hủy đơn sẽ bốc hơi. Đã đụng tay vào đồ của khách thì phải làm cho xong. Nếu có biến căng, thợ phải gọi lên Tổng đài nhờ Admin can thiệp tay.<br><br>\- Hủy xong rớt mạng: Vừa bấm xác nhận hủy xong thì điện thoại mất 4G. Ứng dụng khôn khéo lưu lại lệnh hủy, khi có mạng tự đẩy lên. Hệ thống đảm bảo không để khách phải đợi thợ trong vô vọng.<br><br>\- Hủy như cơm bữa: Hủy đến đơn thứ 2 trong ngày, ứng dụng sẽ hiện cảnh báo cực gắt: "Đây là lần hủy thứ 2 hôm nay. Hủy tiếp bạn sẽ bị cấm nhận việc đấy!"<br><br>\- Không chịu chọn lý do: Thợ lười không chọn lý do mà cứ bấm chốt. Ứng dụng bôi đỏ dòng chữ: "Bạn phải cho chúng tôi biết lý do thì mới được hủy nhé!" |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- Quy định 1 (Cửa sổ hủy): Chỉ được quay xe khi đang đi đường hoặc vừa đến nơi khảo sát. Đã cầm đồ nghề lên sửa là cấm hủy.<br><br>\- Quy định 2 (Luật trừ điểm): Hủy lúc đang ở nhà thì phạt nhẹ, để khách leo cây lúc đã đến tận cửa thì phạt rất nặng.<br><br>\- Quy định 3 (Khóa mỏ): Thợ nào vô kỷ luật, hủy 3 đơn trong 1 tuần sẽ bị ứng dụng cấm túc (tạm khóa quyền nhận việc) trọn 24 tiếng để răn đe.<br><br>\- Quy định 4: Tuyệt đối không phạt tiền thợ, chỉ phạt điểm uy tín (điểm uy tín thấp thì ứng dụng sẽ ít nổ đơn cho thợ đó hơn).<br><br>\- Quy định 5 (Bảo vệ khách hàng): Ngay giây phút thợ bấm hủy, hệ thống không được chần chừ quá 30 giây, phải lập tức kích hoạt tìm thợ khác thế chỗ ngay để khách không bực mình.<br><br>\- Quy định 6: Mọi vết nhơ hủy đơn đều được lưu vĩnh viễn trong hồ sơ của thợ để làm cơ sở đánh giá chất lượng nhân sự cuối năm. |
| --- | --- |

# Use case - Thế Anh

### **Danh sách UC**

| **Mã UC** | **Tên Use Case** | **Mô tả sơ bộ** |
| --- | --- | --- |
| **UC-01** | Tìm kiếm thợ | Khách hàng chọn dịch vụ cần sửa. Hệ thống tự động lấy tọa độ GPS để hiển thị danh sách và số lượng thợ đang rảnh (online) quanh khu vực trên bản đồ. |
| --- | --- | --- |
| **UC-02** | Danh sách Thợ quen | Khách hàng lưu thông tin các thợ làm tốt vào danh sách yêu thích. Khi có yêu cầu mới, hệ thống ưu tiên gửi thông báo đặt lịch cho nhóm thợ quen này trước. |
| --- | --- | --- |
| **UC-03** | Đặt thợ | Khách hàng tạo yêu cầu (chụp ảnh, ghi chú lỗi), chọn thời gian (đến ngay/hẹn giờ) và có thể nhập mức giá đề xuất. Hệ thống tự động gửi yêu cầu đến thợ. |
| --- | --- | --- |
| **UC-04** | Thương lượng giá qua Chat | Khách hàng và thợ trao đổi trực tiếp chi tiết trong khung chat. Thợ chốt "Báo giá cuối cùng" bằng thẻ báo giá trên app để khách hàng bấm đồng ý. |
| --- | --- | --- |
| **UC-05** | Thanh toán dịch vụ | Khách hàng nghiệm thu và thanh toán bằng tiền mặt hoặc chuyển khoản. Hệ thống ghi nhận hóa đơn, chuyển tiền vào trạng thái bảo hiểm hoặc tính nợ chiết khấu cho thợ. |
| --- | --- | --- |

### **Bảng Đặc tả Use Case: Tìm kiếm thợ**

| **Thuộc tính** | **Chi tiết** |
| --- | --- |
| **Mã Use Case** | UC-01 |
| --- | --- |
| **Tên Use Case** | Tìm kiếm thợ |
| --- | --- |
| **Nhóm chức năng** | Tìm kiếm & Lọc Dịch vụ |
| --- | --- |
| **Tác nhân (Actor)** | Khách hàng |
| --- | --- |
| **Mô tả ngắn gọn** | Chức năng này cho phép khách hàng tìm kiếm thợ sửa chữa thông qua việc hiển thị danh mục dịch vụ. Hệ thống sẽ tự động lấy tọa độ GPS của khách hàng để quét và hiển thị số lượng thợ đang rảnh trên bản đồ. |
| --- | --- |
| **Tiền điều kiện** | \* Khách hàng đã đăng nhập vào ứng dụng.<br><br>\* Khách hàng đã cấp quyền truy cập vị trí (GPS) cho thiết bị.<br><br>\* Có các thợ đang bật trạng thái "Sẵn sàng nhận việc" (Online) để hệ thống có cơ sở xác định thợ rảnh. |
| --- | --- |
| **Luồng sự kiện chính (Basic Flow)** | **Bước 1:** Khách hàng mở ứng dụng và chọn chức năng Tìm kiếm.<br><br>**Bước 2:** Hệ thống hiển thị danh mục các dịch vụ sửa chữa .<br><br>**Bước 3:** Khách hàng bấm chọn một dịch vụ đang có nhu cầu.<br><br>**Bước 4:** Hệ thống tự động lấy tọa độ GPS hiện tại của khách hàng .<br><br>**Bước 5:** Hệ thống tiến hành quét tìm các thợ đang bật trạng thái rảnh (Online) trong khu vực bán kính cho phép .<br><br>**Bước 6:** Hệ thống hiển thị số lượng thợ đang rảnh và vị trí của họ trực tiếp trên bản đồ cho khách hàng. |
| --- | --- |
| **Luồng thay thế/Ngoại lệ (Alternative Flows)** | \* **Ngoại lệ 1 (Không có quyền vị trí):** Tại Bước 4, nếu ứng dụng chưa được cấp quyền GPS, hệ thống sẽ bật thông báo yêu cầu cấp quyền. Nếu khách hàng từ chối, không thể sử dụng tính năng quét bản đồ.<br><br>\* **Ngoại lệ 2 (Không tìm thấy thợ):** Tại Bước 6, nếu các thợ đều đã tắt trạng thái (Offline) và biến mất khỏi bản đồ hoặc không có thợ nào trong bán kính cho phép, hệ thống sẽ hiển thị thông báo "Hiện không có thợ rảnh quanh bạn" và gợi ý thử lại sau. |
| --- | --- |
| **Hậu điều kiện** | Khách hàng nhìn thấy được thợ trên bản đồ và có thể chuyển sang bước tạo yêu cầu sửa chữa, chọn thời gian để đặt thợ. |
| --- | --- |

### **Bảng Đặc tả Use Case: Danh sách Thợ quen**

| **Thuộc tính** | **Chi tiết** |
| --- | --- |
| **Mã Use Case** | UC-02 |
| --- | --- |
| **Tên Use Case** | Danh sách Thợ quen (Thợ yêu thích) |
| --- | --- |
| **Nhóm chức năng** | Tìm kiếm & Lọc Dịch vụ |
| --- | --- |
| **Tác nhân (Actor)** | Khách hàng |
| --- | --- |
| **Mô tả ngắn gọn** | Khách hàng có thể lưu lại những người thợ đã làm tốt vào danh sách "Thợ yêu thích". Khi có nhu cầu đặt thợ vào lần sau, hệ thống sẽ ưu tiên gửi thông báo đặt lịch cho nhóm "Thợ quen" này trước. |
| --- | --- |
| **Tiền điều kiện** | \* Khách hàng đã đăng nhập vào ứng dụng.<br><br>\* Khách hàng đã từng đặt dịch vụ và có thợ đã hoàn thành công việc. |
| --- | --- |
| **Luồng sự kiện chính (Basic Flow)** | **Bước 1:** Khách hàng truy cập vào lịch sử đơn hàng hoặc xem thông tin thợ sau khi hoàn thành một dịch vụ.<br><br>**Bước 2:** Khách hàng bấm chọn "Thêm vào thợ yêu thích" (hoặc Thợ quen) đối với người thợ đã làm tốt .<br><br>**Bước 3:** Hệ thống cập nhật và lưu thợ đó vào "Danh sách Thợ quen" của khách hàng.<br><br>**Bước 4:** Ở lần đặt lịch tiếp theo, khi khách hàng tạo yêu cầu mới, hệ thống sẽ ưu tiên gửi thông báo nhận việc cho các thợ trong danh sách này trước. |
| --- | --- |
| **Luồng thay thế/Ngoại lệ (Alternative Flows)** | \* **Ngoại lệ 1 (Xóa thợ khỏi danh sách):** Khách hàng truy cập vào "Danh sách Thợ quen", chọn một người thợ và bấm "Xóa". Hệ thống cập nhật lại danh sách.<br><br>\* **Ngoại lệ 2 (Thợ quen không online/bận):** Tại Bước 4, nếu không có thợ nào trong danh sách đang bật trạng thái "Sẵn sàng nhận việc" hoặc họ từ chối, hệ thống sẽ tự động chuyển thông báo đến các thợ khác trong khu vực theo luồng tìm kiếm bình thường. |
| --- | --- |
| **Hậu điều kiện** | Thợ được thêm vào danh sách thành công và luôn được ưu tiên nhận thông báo đặt lịch từ khách hàng đó trong tương lai. |
| --- | --- |

**Bảng Đặc tả Use Case: Đặt thợ**

| **Thuộc tính** | **Chi tiết** |
| --- | --- |
| **Mã Use Case** | UC-03 |
| --- | --- |
| **Tên Use Case** | Đặt thợ |
| --- | --- |
| **Nhóm chức năng** | Đặt lịch, Báo giá & Thanh toán |
| --- | --- |
| **Tác nhân (Actor)** | Khách hàng |
| --- | --- |
| **Mô tả ngắn gọn** | Khách hàng tiến hành tạo yêu cầu sửa chữa bằng cách cung cấp hình ảnh, ghi chú lỗi và chọn thời gian thực hiện. Sau đó, hệ thống sẽ tự động gửi yêu cầu đến thợ. |
| --- | --- |
| **Tiền điều kiện** | \* Khách hàng đã đăng nhập vào ứng dụng.<br><br>\* Khách hàng đã chọn được dịch vụ thông qua tính năng tìm kiếm hoặc chọn từ danh sách thợ quen. |
| --- | --- |
| **Luồng sự kiện chính (Basic Flow)** | **Bước 1:** Khách hàng ấn chọn vào nút Đặt thợ/Tạo yêu cầu.<br><br>**Bước 2:** Khách hàng nhập thông tin chi tiết về tình trạng hỏng hóc bằng cách chụp ảnh và điền ghi chú lỗi .<br><br>**Bước 3:** Khách hàng thiết lập thời gian mong muốn bằng cách chọn "Đến ngay" hoặc "Hẹn giờ" .<br><br>**Bước 4:** Khách hàng bấm nút xác nhận gửi yêu cầu.<br><br>**Bước 5:** Hệ thống ghi nhận đơn và tiến hành gửi yêu cầu công việc đến thợ. |
| --- | --- |
| **Luồng thay thế/Ngoại lệ (Alternative Flows)** | \* **Ngoại lệ 1 (Thiếu thông tin):** Tại Bước 2 hoặc 3, nếu khách hàng bỏ trống các trường thông tin bắt buộc (ví dụ không có mô tả lỗi), hệ thống sẽ báo đỏ và yêu cầu nhập đầy đủ trước khi cho phép bấm xác nhận.<br><br>\* **Ngoại lệ 2 (Hủy tạo đơn):** Trong quá trình nhập thông tin, khách hàng bấm nút "Quay lại" hoặc "Hủy". Hệ thống sẽ xóa các dữ liệu tạm và không tạo yêu cầu sửa chữa. |
| --- | --- |
| **Hậu điều kiện** | Yêu cầu sửa chữa được tạo thành công trên hệ thống và chuyển sang trạng thái chờ thợ phản hồi. |
| --- | --- |

**Bảng Đặc tả Use Case: Thương lượng giá - chỉnh sửa**

| **Thuộc tính** | **Chi tiết** |
| --- | --- |
| **Mã Use Case** | UC-04 (Cập nhật) |
| --- | --- |
| **Tên Use Case** | Thương lượng giá qua Chat |
| --- | --- |
| **Nhóm chức năng** | Đặt lịch, Báo giá & Thanh toán (kết hợp Tương tác) |
| --- | --- |
| **Tác nhân (Actor)** | Khách hàng, Thợ |
| --- | --- |
| **Mô tả ngắn gọn** | Khách hàng và thợ sử dụng tính năng Chat trên hệ thống để trao đổi chi tiết và thương lượng giá cả. Khi hai bên thống nhất, thợ sẽ gửi một thẻ "Báo giá" vào khung chat. Khách hàng bấm đồng ý trên thẻ này để hệ thống ghi nhận giá chính thức. |
| --- | --- |
| **Tiền điều kiện** | \* Yêu cầu đặt thợ đã được tạo.<br><br>\* Hai bên đang ở trong giao diện Chat Real-time của ứng dụng. |
| --- | --- |
| **Luồng sự kiện chính (Basic Flow)** | **Bước 1:** Khách hàng và thợ nhắn tin qua lại trong khung chat để trình bày rõ hơn về lỗi thiết bị và kỳ vọng về giá.<br><br>**Bước 2:** Sau khi trao đổi và thống nhất được một con số cụ thể bằng tin nhắn văn bản, thợ bấm vào công cụ **"Gửi báo giá"** (biểu tượng đính kèm) ngay trong khung chat.<br><br>**Bước 3:** Thợ nhập số tiền đã thống nhất vào ô trống và bấm gửi.<br><br>**Bước 4:** Hệ thống hiển thị một thẻ "Báo giá chính thức" (Quote Card) nổi bật trong luồng tin nhắn của hai người.<br><br>**Bước 5:** Khách hàng xem thẻ báo giá và bấm nút **"Đồng ý"** ngay trên thẻ đó.<br><br>**Bước 6:** Hệ thống tự động trích xuất con số từ thẻ này, cập nhật vào cơ sở dữ liệu làm "Giá trị đơn hàng" cuối cùng và chuyển trạng thái đơn sang "Đã chốt". |
| --- | --- |
| **Luồng thay thế/Ngoại lệ (Alternative Flows)** | \* **Ngoại lệ 1 (Khách không đồng ý giá trên thẻ):** Tại Bước 5, khách hàng bấm "Từ chối" trên thẻ báo giá. Hai bên tiếp tục nhắn tin thương lượng lại từ Bước 1, sau đó thợ gửi một thẻ báo giá mới.<br><br>\* **Ngoại lệ 2 (Hủy đơn trong lúc chat):** Một trong hai bên cảm thấy không thể thống nhất được giá và bấm nút "Hủy yêu cầu/Hủy đơn". Hệ thống đóng khung chat và đơn hàng bị hủy. |
| --- | --- |
| **Hậu điều kiện** | Mức giá được chốt thành công, hệ thống lưu trữ chính xác con số để phục vụ cho bước thanh toán và tính toán chiết khấu sau này. |
| --- | --- |

**Bảng Đặc tả Use Case: Thanh toán dịch vụ**

| **Thuộc tính** | **Chi tiết** |
| --- | --- |
| **Mã Use Case** | UC-05 |
| --- | --- |
| **Tên Use Case** | Thanh toán dịch vụ |
| --- | --- |
| **Nhóm chức năng** | Đặt lịch, Báo giá & Thanh toán |
| --- | --- |
| **Tác nhân (Actor)** | Khách hàng, Thợ, Hệ thống |
| --- | --- |
| **Mô tả ngắn gọn** | Khách hàng tiến hành nghiệm thu và thanh toán chi phí sửa chữa (đã chốt trước đó) bằng tiền mặt hoặc chuyển khoản. Hệ thống ghi nhận doanh thu và xử lý luồng tiền vào cơ chế bảo hiểm hoặc tính phí nợ chiết khấu. |
| --- | --- |
| **Tiền điều kiện** | \* Quá trình sửa chữa đã xong và thợ chuyển trạng thái công việc sang "Hoàn thành" .<br><br>\* Trạng thái "Hoàn thành" đã mở khóa bước nghiệm thu và thanh toán cho khách hàng. |
| --- | --- |
| **Luồng sự kiện chính (Basic Flow)** | **Bước 1:** Khách hàng kiểm tra thực tế thiết bị sau sửa chữa và bấm nút "Hoàn thành nghiệm thu" trên ứng dụng .<br><br>**Bước 2:** Hệ thống hiển thị tổng số tiền cần thanh toán (dựa trên mức giá đã thương lượng) và yêu cầu chọn phương thức: Tiền mặt hoặc Chuyển khoản .<br><br>**Bước 3:** Khách hàng thực hiện thanh toán.<br><br>**Bước 4:** Xử lý luồng tiền theo phương thức:<br><br>\* **Nếu thanh toán chuyển khoản qua app:** Hệ thống nhận tiền và đưa vào trạng thái "Đang bảo hiểm" trong vòng 96 giờ (4 ngày) thay vì chuyển ngay cho thợ.<br><br>\* **Nếu thanh toán tiền mặt:** Khách hàng đưa trực tiếp tiền cho thợ. Hệ thống tự động ghi nhận thợ đang "nợ" app khoản phí chiết khấu tương ứng với đơn hàng này .<br><br>**Bước 5:** Hệ thống thông báo thanh toán thành công, lưu lại lịch sử/hóa đơn và chuyển sang màn hình Đánh giá thợ. |
| --- | --- |
| **Luồng thay thế/Ngoại lệ (Alternative Flows)** | \* **Ngoại lệ 1 (Lỗi cổng thanh toán):** Tại Bước 3, nếu khách hàng chọn Chuyển khoản nhưng giao dịch bị lỗi (ngân hàng bảo trì, rớt mạng), hệ thống thông báo thất bại và yêu cầu khách hàng thử lại hoặc đổi sang trả Tiền mặt.<br><br>\* **Ngoại lệ 2 (Khách hàng từ chối nghiệm thu):** Tại Bước 1, nếu khách hàng không hài lòng với kết quả sửa chữa, họ không bấm "Hoàn thành nghiệm thu" mà có thể báo cáo sự cố qua kênh liên hệ (Chat/Hotline) trực tiếp với Admin. |
| --- | --- |
| **Hậu điều kiện** | Giao dịch hoàn tất. Tiền của thợ rơi vào trạng thái chờ (bảo hiểm 96 giờ) hoặc thợ bị ghi nhận khoản nợ chiết khấu vào ví tài khoản. |
| --- | --- |

# Use case - Duck Hiếu

**USE CASE NHÓM CHỨC NĂNG 4.1**

**I, Use case cho nhóm chức năng**

**1\. Danh sách UC cho nhóm chức năng 4.1**

| **Mã UC** | **Tên Use Case** | **Mô tả sơ bộ** |
| --- | --- | --- |
| UC-A01 | Đăng ký tài khoản (OTP) | Người dùng chọn vai trò (Khách hàng/Thợ), nhập số điện thoại và nhận OTP để xác thực. Nếu chưa tồn tại tài khoản thì hệ thống tạo mới, nếu đã có thì tự động đăng nhập. |
| --- | --- | --- |
| UC-A02 | Đăng nhập hệ thống | Người dùng đăng nhập bằng SĐT (OTP) hoặc Email/Mật khẩu. Backend xác thực thông tin và cấp JWT (Access + Refresh Token). |
| --- | --- | --- |
| UC-A03 | Đăng nhập bằng Google | Người dùng sử dụng Google Sign-In để đăng nhập nhanh. Hệ thống nhận idToken, xác thực với Google, sau đó tạo hoặc đăng nhập tài khoản tương ứng. |
| --- | --- | --- |
| UC-A04 | Quản lý thông tin cá nhân | Người dùng cập nhật avatar, họ tên, SĐT. Khách hàng có thể thêm địa chỉ (nhà/công ty). Thợ có thể cập nhật kinh nghiệm và khu vực hoạt động. |
| --- | --- | --- |
| UC-A05 | Xác minh khách hàng | Khách hàng xác minh cơ bản qua OTP số điện thoại để đảm bảo tính hợp lệ, hạn chế spam đơn hàng giả. |
| --- | --- | --- |
| UC-A06 | Xác minh thợ (eKYC) | Thợ tải lên CCCD, ảnh chân dung và chứng chỉ nghề. Hệ thống lưu trữ bảo mật và chuyển trạng thái PENDING để Admin duyệt. |
| --- | --- | --- |
| UC-A07 | Duyệt hồ sơ thợ | Admin kiểm tra hồ sơ eKYC của thợ và quyết định VERIFIED hoặc REJECTED. Kết quả được cập nhật vào hệ thống. |
| --- | --- | --- |

  
<br/>

**II, Đặc tả từng use case cho nhóm chức năng**

**1.Đăng ký**

| **Thành phần** | **Nội dung chi tiết** |
| --- | --- |
| **Mã UC** | UC-A01 |
| --- | --- |
| **Tên UC** | Đăng ký |
| --- | --- |
| **Mô tả** | Người dùng (Khách hàng hoặc Thợ) thực hiện đăng ký vào hệ thống thông qua số điện thoại (OTP) hoặc Email/Mật khẩu để sử dụng ứng dụng |
| --- | --- |
| **Tác nhân** | Người dùng (Khách hàng / Thợ), Hệ thống (OTP Service) |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc |
| --- | --- |
| **Sự kiện kích hoạt** | Người dùng mở app và chọn "Đăng ký" |
| --- | --- |
| **Điều kiện kiên quyết** | \- Thiết bị có kết nối mạng  <br>\- Người dùng chưa bị khóa tài khoản |
| --- | --- |
| **Kết quả đầu ra** | \- Đăng nhập thành công → chuyển vào trang chính  <br>\- Thất bại → hiển thị lỗi |
| --- | --- |
| **Luồng cơ bản** | **B1:** Người dùng chọn vai trò (Khách hàng/Thợ)  <br>**B2:** Nhập SĐT hoặc Email  <br>**B3:** Chọn phương thức đăng ký (OTP hoặc Password)<br><br>**Nếu OTP:**<br><br>**B4**: Hệ thống gửi OTP<br><br>**B5**: Người dùng nhập OTP<br><br>**Nếu Password:**<br><br>**B4**: Người dùng nhập mật khẩu<br><br>**B5:** Hệ thống xác thực  <br>**B7:** Tạo tài khoản  <br>**B8:** Đăng ký thành công |
| --- | --- |
| **Luồng thay thế** | \- SĐT/Email đã tồn tại → chuyển sang đăng nhập<br><br>\-OTP sai → nhập lại |
| --- | --- |
| **Luồng ngoại lệ** | \- OTP hết hạn  <br>\-Không gửi được OTP |
| --- | --- |
| **Quy tắc nghiệp vụ** | \-Mỗi SĐT/Email chỉ tạo 1 tài khoản<br><br>\-OTP có hiệu lực trong 60 giây |
| --- | --- |

**1.Đăng Nhập**

| **Thành phần** | **Nội dung chi tiết** |
| --- | --- |
| **Mã UC** | UC-A02 |
| --- | --- |
| **Tên UC** | Đăng Nhập |
| --- | --- |
| **Mô tả** | Người dùng đăng nhập vào hệ thống bằng số điện thoại (OTP) hoặc Email/Mật khẩu |
| --- | --- |
| **Tác nhân** | Người dùng (Khách hàng / Thợ), Hệ thống (OTP Service) |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc |
| --- | --- |
| **Sự kiện kích hoạt** | Người dùng mở app và chọn "Đăng nhập" |
| --- | --- |
| **Điều kiện kiên quyết** | \- Thiết bị có kết nối mạng  <br>\- Đã có tài khoản |
| --- | --- |
| **Kết quả đầu ra** | \- Đăng nhập thành công → chuyển vào trang chính  <br>\- Thất bại → hiển thị lỗi |
| --- | --- |
| **Luồng cơ bản** | **B1:** Người dùng chọn vai trò (Khách hàng/Thợ)  <br>**B2:** Nhập SĐT hoặc Email  <br>**B3:** Chọn phương thức đăng nhập (OTP hoặc Password)<br><br>**Nếu OTP:**<br><br>**B4**: Hệ thống gửi OTP<br><br>**B5**: Người dùng nhập OTP<br><br>**Nếu Password:**<br><br>**B4**: Người dùng nhập mật khẩu<br><br>**B5:** Hệ thống xác thực  <br>**B7:** Tạo tài khoản  <br>**B8:** Đăng ký thành công |
| --- | --- |
| **Luồng thay thế** | \-Sai mật khẩu thì báo lỗi<br><br>\-OTP sai → nhập lại |
| --- | --- |
| **Luồng ngoại lệ** | \- OTP hết hạn  <br>\-Tài khoản bị khóa |
| --- | --- |
| **Quy tắc nghiệp vụ** | \-Giới hạn số lần nhập sai<br><br>\-OTP có hiệu lực trong 60 giây |
| --- | --- |

**2.Đăng nhập bằng GG**

| **Thành phần** | **Nội dung chi tiết** |
| --- | --- |
| **Mã UC** | UC-A03 |
| --- | --- |
| **Tên UC** | Đăng nhập bằng Google |
| --- | --- |
| **Mô tả** | Người dùng đăng nhập nhanh bằng tài khoản Google mà không cần nhập mật khẩu |
| --- | --- |
| **Tác nhân** | Người dùng, Hệ thống |
| --- | --- |
| **Độ ưu tiên** | Phụ |
| --- | --- |
| **Sự kiện kích hoạt** | Người dùng chọn "Đăng nhập bằng Google" |
| --- | --- |
| **Điều kiện kiên quyết** | \- Có tài khoản Google  <br>\- Thiết bị có internet |
| --- | --- |
| **Kết quả đầu ra** | \- Đăng nhập thành công  <br>\- Tạo tài khoản mới nếu chưa tồn tại |
| --- | --- |
| **Luồng cơ bản** | **B1:** Người dùng chọn Google Login  <br>**B2:** Chọn tài khoản Google  <br>**B3:** Google trả về token  <br>**B4:** Backend xác thực token  <br>**B5:** Đăng nhập thành công |
| --- | --- |
| **Luồng thay thế** | \- Tài khoản chưa tồn tại thì tạo mới |
| --- | --- |
| **Luồng ngoại lệ** | \-Người dùng hủy đăng nhập |
| --- | --- |
| **Quy tắc nghiệp vụ** | \-Mỗi email chỉ liên kết 1 tài khoản |
| --- | --- |

**3.Cập nhật thông tin cá nhân**

**Thành phần**

**Nội dung chi tiết**

**Mã UC**

UC-A04

**Tên UC**

Cập nhật thông tin cá nhân

**Mô tả**

Người dùng chỉnh sửa thông tin hồ sơ cá nhân (avatar, tên, địa chỉ, kinh nghiệm…)

**Tác nhân**

Người dùng

**Độ ưu tiên**

Bắt buộc

**Sự kiện kích hoạt**

Người dùng vào mục "Hồ sơ cá nhân"

**Điều kiện kiên quyết**

\-Đã đăng nhập

**Kết quả đầu ra**

\-Thông tin được cập nhật thành công

**Luồng cơ bản**

**B1:** Mở trang hồ sơ  
**B2:** Chỉnh sửa thông tin  
**B3:** Nhấn lưu  
**B4:** Hệ thống validate  
**B5:** Cập nhật DB

**Luồng thay thế**

\- Thông tin không hợp lệ thì báo lỗi

**Luồng ngoại lệ**

\- Lỗi upload avatar

**Quy tắc nghiệp vụ**

\- SĐT không được trùng  
\- Ảnh < 5MB

**4.Xác minh khách hàng**

**Thành phần**

Nội dung chi tiết

**Mã UC**

UC-A05

**Tên UC**

Xác minh khách hàng

**Mô tả**

Xác thực số điện thoại khách hàng để đảm bảo tài khoản thật

**Tác nhân**

Khách hàng,Hệ thống

**Độ ưu tiên**

Bắt buộc

**Sự kiện kích hoạt**

Đã có tài khoản

**Điều kiện kiên quyết**

\-Có số điện thoại hợp lệ

**Kết quả đầu ra**

\-Tài khoản được xác minh

**Luồng cơ bản**

Như OTP Login

**Quy tắc nghiệp vụ**

\-1 số điện thoại là 1 tài khoản

**5.Xác minh danh tính thợ**

**Thành phần**

**Nội dung chi tiết**

**Mã UC**

UC-A06

**Tên UC**

Xác minh danh tính thợ

**Mô tả**

Thợ tải ảnh CCCD và chứng chỉ để Admin duyệt trước khi nhận việc

**Tác nhân**

Thợ,Hệ thống,Admin

**Độ ưu tiên**

Bắt buộc

**Sự kiện kích hoạt**

Thợ đăng ký tài khoản

**Điều kiện kiên quyết**

\-Đã đăng nhập

**Kết quả đầu ra**

\-Trạng thái chờ duyệt,đã duyệt,từ chối

**Luồng cơ bản**

**B1:** Thợ upload CCCD + chứng chỉ  
**B2:** Hệ thống lưu trữ  
**B3:** Admin kiểm duyệt  
**B4:** Trả kết quả

**Luồng thay thế**

\-Thiếu giấy tờ thì yêu cầu bổ sung

**Luồng ngoại lệ**

\-Ảnh mờ không hợp lệ

**Quy tắc nghiệp vụ**

\- Bắt buộc duyệt mới được nhận việc  
\- Dữ liệu CCCD phải được mã hóa

**6\. Duyệt hồ sơ thợ**

**Thành phần**

**Nội dung chi tiết**

**Mã UC**

UC-A07

**Tên UC**

Duyệt hồ sơ thợ

**Mô tả**

Admin kiểm tra và phê duyệt hồ sơ đăng ký của thợ trước khi cho phép hoạt động trên hệ thống

**Tác nhân**

Hệ thống,Admin

**Độ ưu tiên**

Bắt buộc

**Sự kiện kích hoạt**

Thợ hoàn tất đăng ký và chờ duyệt hồ sơ

**Điều kiện kiên quyết**

\-Thợ đã đăng ký tài khoản

**Kết quả đầu ra**

\- Hồ sơ được duyệt → thợ có thể nhận việc

\- Hồ sơ bị từ chối → thợ nhận thông báo chỉnh sửa

**Luồng cơ bản**

**B1**: Admin đăng nhập vào hệ thống quản trị  
**B2**: Truy cập danh sách hồ sơ thợ chờ duyệt  
**B3**: Chọn một hồ sơ cụ thể  
**B4**: Xem chi tiết thông tin và giấy tờ  
**B5**: Admin chọn “Duyệt”  
**B6**: Hệ thống cập nhật trạng thái “Đã duyệt”  
**B7**: Gửi thông báo cho thợ

**Luồng thay thế**

\- Hồ sơ thiếu thông tin → yêu cầu bổ sung

\- Giấy tờ không hợp lệ → từ chối hồ sơ

**Luồng ngoại lệ**

\- Lỗi hệ thống khi cập nhật trạng thái

\-Không tải được hồ sơ/ảnh

\-Mất kết nối mạng

**Quy tắc nghiệp vụ**

\- Hồ sơ phải đầy đủ thông tin mới được duyệt

\-Giấy tờ phải hợp lệ và còn hiệu lực

\-Admin có quyền từ chối hoặc yêu cầu chỉnh sửa

\-Mỗi hồ sơ phải có trạng thái rõ ràng: Chờ duyệt / Đã duyệt / Từ chối

**7.Quên mật khẩu**

**Thành phần**

**Nội dung chi tiết**

**Mã UC**

UC-A08

**Tên UC**

Quên mật khẩu

**Mô tả**

Người dùng yêu cầu đặt lại mật khẩu khi quên,hệ thống gửi mã xác thực 6 số qua email.

**Tác nhân**

Người dùng,hệ thống

**Độ ưu tiên**

Phụ

**Sự kiện kích hoạt**

Người dùng chọn quên mật khẩu

**Điều kiện kiên quyết**

\-Email đã đăng ký tài khoản

\-Có kết nối mạng

**Kết quả đầu ra**

\-Đặt lại mật khẩu thành công

\-Thất bại thì hiển thị lỗi

**Luồng cơ bản**

**B1:** Người dùng chọn “Quên mật khẩu”  
**B2:** Nhập email  
**B3:** Hệ thống gửi mã xác thực 6 số qua email  
**B4:** Người dùng nhập mã xác thực  
**B5:** Hệ thống kiểm tra mã  
**B6:** Người dùng nhập mật khẩu mới  
**B7:** Hệ thống cập nhật mật khẩu  
**B8:** Hoàn tất

**Luồng thay thế**

\- Email chưa tồn tại → báo lỗi

\-Mã sai → nhập lại

**Luồng ngoại lệ**

\-Mã hết hạn

\-Không gửi được email

**Quy tắc nghiệp vụ**

\-Mã xác thực gồm 6 chữ số

\-Thời gian hiệu lực: 60–120 giây

\-Giới hạn số lần nhập sai

# Use case - Minh Tiến

### **4.4. Nhóm chức năng Quản lý Yêu cầu & Tương tác (Interaction & Management) - Tiến**

- **Nhắn tin/Gọi điện trong App \[Chính\]:** Tích hợp Chat Real-time hoặc Call giữa Khách hàng và Thợ để trao đổi chi tiết tình trạng hỏng hóc hoặc chỉ đường mà không làm lộ số điện thoại cá nhân.
- **Lịch sử và Hóa đơn \[Chính\]:** Khách hàng xem lại các đơn đã hoàn thành, chi phí, thông tin thợ. Dùng làm căn cứ để yêu cầu bảo hành hoặc khiếu nại nếu thiết bị hỏng lại.
- **Đánh giá Thợ \[Phụ\]:** Sau khi hoàn thành, khách hàng chấm điểm sao (1-5) và để lại nhận xét.
- **Trung tâm hỗ trợ \[Chính\]:** Mục FAQ (Câu hỏi thường gặp) và kênh liên hệ (Chat/Hotline) trực tiếp với Admin hệ thống để báo cáo sự cố (thợ thái độ kém, quỵt tiền, v.v.).

| **Thành phần** | Nội dung chi tiết ( ngu vl ) |
| --- | --- |
| **Mã UC** | UC-01a |
| --- | --- |
| **Tên UC** | Nhắn tin trong ứng dụng |
| --- | --- |
| **Mô tả** | Cho phép Khách hàng và Thợ liên lạc trực tiếp với nhau (Chat) qua hệ thống của ứng dụng để trao đổi công việc, chỉ đường mà không làm lộ số điện thoại cá nhân của cả hai bên. |
| --- | --- |
| **Tác nhân** | Khách hàng, Thợ, |
| --- | --- |
| **Độ ưu tiên** | Cao (must have) |
| --- | --- |
| **Sự kiện kích hoạt** | Thợ chấp nhận đơn đặt việc của Khách hàng thành công. Nút Chat xuất hiện trên màn hình theo dõi đơn hàng. |
| --- | --- |
| **Điều kiện tiên quyết** | \- Cả Khách hàng và Thợ đều đang đăng nhập.<br><br>\- Thiết bị có kết nối mạng<br><br>\- Đơn hàng đang ở trạng thái "Đã nhận", "Đang di chuyển" hoặc "Đang thực hiện". |
| --- | --- |
| **Kết quả đầu ra** | \- Nội dung trao đổi được thực hiện thông suốt, bảo mật danh tính.<br><br>\- Toàn bộ lịch sử tin nhắn được lưu lại trên server để làm căn cứ giải quyết tranh chấp (nếu có). |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | B1: Khách hàng/ thợ nhấn vào biểu tượng khung chat<br><br>B2: (Nếu chọn nhắn tin) Hệ thống sẽ mở giao diện trò chuyện giữa khách hàng và thợ<br><br>B3: Hai bên trao đổi về tình trạng hỏng hóc của thiết bị.<br><br>B4: Kết thúc trao đổi, người dùng đóng cửa sổ chat hoặc tẳt máy. |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- **Gửi hình ảnh/video:** Trong lúc chat, Khách hàng có thể chụp ảnh trực tiếp hoặc chọn ảnh từ thư viện để gửi cho thợ xem trước tình trạng hỏng hóc. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Mất mạng/Không thể kết nối:** Hệ thống báo "Đường truyền không ổn định, vui lòng thử lại sau". Nếu đang chat, tin nhắn hiện dấu chấm than đỏ (gửi lỗi).<br><br>\- **Đối phương không trực tuyến:** (Với chat) Tin nhắn chuyển sang dạng tin nhắn chờ. Hệ thống gửi Push Notification (thông báo đẩy) đến máy đối phương. (Với gọi) Thông báo "Người dùng hiện không thể nghe máy". |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1:** Cửa sổ chat khóa sau 24 giờ kể từ khi đơn hàng được đánh dấu "Hoàn thành" hoặc "Đã hủy".<br><br>\- **Quy định 2:** Tuyệt đối không hiển thị số điện thoại thật của 2 bên. Mọi cuộc gọi phải được định tuyến qua tổng đài VoIP của ứng dụng.<br><br>\- **Quy định 3:** Hệ thống tự động quét và che (mask) các chuỗi số giống số điện thoại nếu người dùng cố tình gửi qua tin nhắn chat. |
| --- | --- |

2\. Quản lý lịch sử và Hóa đơn ( dành cho khách)

| **Thành phần** | tp là cái l gì ???? |
| --- | --- |
| **Mã UC** | UC-02a |
| --- | --- |
| **Tên UC** | Quản lý lịch sử đơn hàng và Hóa |
| --- | --- |
| **Mô tả** | Cung cấp cho khách hàng danh sách các đơn đã đặt, thông tin chi phí, thông tin thợ để quản lý, đánh giá chất lượng và dùng để làm căn cứ yêu cầu bảo hành hoặc khiếu nại. |
| --- | --- |
| **Tác nhân** | Khách hàng, hệ thống. |
| --- | --- |
| **Độ ưu tiên** | Cao (must have) |
| --- | --- |
| **Sự kiện kích hoạt** | Khách hàng chọn mục "Lịch sử" hoặc "Đơn hàng" trên menu chính của ứng dụng. |
| --- | --- |
| **Điều kiện tiên quyết** | Khách hàng đã đăng nhập vào hệ thống.. |
| --- | --- |
| **Kết quả đầu ra** | Hiển thị chính xác danh sách các đơn hàng, phân loại rõ ràng theo trạng thái. Khách hàng có thể xem lại chi tiết hóa đơn điện tử của từng đơn. |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | **Bước 1:** Khách hàng bấm chọn menu "Lịch sử".<br><br>**Bước 2:** Hệ thống tải và hiển thị danh sách các đơn hàng (sắp xếp từ mới nhất đến cũ nhất).<br><br>**Bước 3:** Khách hàng bấm vào một đơn hàng cụ thể đã hoàn thành.<br><br>**Bước 4:** Ứng dụng hiển thị "Chi tiết đơn hàng" bao gồm: Tên thợ, Tóm tắt công việc, Ngày giờ, Tổng tiền, Trạng thái thanh toán, và thời hạn bảo hành (nếu có).<br><br>**Bước 5:** Khách hàng có thể bấm "Tải hóa đơn" để lưu ảnh biên lai hoặc bấm "Yêu cầu bảo hành" (nếu thiết bị hỏng lại) |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- **Lọc danh sách:** Khách hàng sử dụng bộ lọc để chỉ xem "Đơn đã hoàn thành", "Đơn đã hủy", hoặc lọc theo tháng cụ thể. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Tài khoản mới:** Nếu khách hàng chưa từng đặt đơn nào, hệ thống hiển thị màn hình trống kèm câu thông báo "Bạn chưa có đơn hàng nào" và nút điều hướng "Đặt thợ ngay".<br><br>\- **Lỗi tải dữ liệu:** Trải nghiệm bị gián đoạn do lỗi máy chủ, hệ thống hiển thị "Không thể tải dữ liệu, vuốt xuống để thử lại". |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1:** Dữ liệu lịch sử đơn hàng phải được lưu trữ tối thiểu 2 năm.<br><br>\- **Quy định 2:** Nút "Yêu cầu bảo hành" chỉ khả dụng và có thể bấm được nếu đơn hàng đó còn trong thời hạn bảo hành do Thợ/Hệ thống quy định (ví dụ 7 ngày, 30 ngày). Hết hạn, nút này sẽ bị mờ đi. |
| --- | --- |

2b. Quản lý lịch sử và Hóa đơn ( dành cho thợ)

| **Thành phần** | tp là cái l gì ???? |
| --- | --- |
| **Mã UC** | UC-02b |
| --- | --- |
| **Tên UC** | Quản lý lịch sử công việc và thu nhập. |
| --- | --- |
| **Mô tả** | Cho phép thợ xem lại danh sách các đơn hàng đã thực hiện, chi tiết số tiền nhận nhận được |
| --- | --- |
| **Tác nhân** | Thợ, Hệ thống |
| --- | --- |
| **Độ ưu tiên** | Bắt buộc có (Must Have) |
| --- | --- |
| **Sự kiện kích hoạt** | Thợ truy cập vào mục "Thu nhập" hoặc "Lịch sử việc làm" trên ứng dụng. |
| --- | --- |
| **Điều kiện tiên quyết** | Thợ đã đăng nhập tài khoản và đã hoàn thành ít nhất một đơn hàng (hoặc có đơn hàng bị hủy). |
| --- | --- |
| **Kết quả đầu ra** | Hiển thị danh sách công việc đã thực hiện, tổng số tiền tích lũy và các báo cáo thu nhập theo ngày/tuần/tháng. |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | **Bước 1:** Thợ chọn mục "Ví/Thu nhập" hoặc "Lịch sử công việc" trên thanh menu.<br><br>**Bước 2:** Hệ thống hiển thị tổng số dư hiện tại và danh sách các đơn hàng gần nhất.<br><br>**Bước 3:** Thợ có thể chọn xem theo bộ lọc: "Tất cả", "Đã hoàn thành", "Đã hủy".<br><br>**Bước 4:** Thợ bấm vào một đơn hàng cụ thể để xem chi tiết: Tên khách hàng, địa chỉ, loại máy đã sửa, số tiền thực nhận (đã trừ phí), và đánh giá (số sao/nhận xét) của khách.<br><br>**Bước 5:** Thợ có thể xem tổng hợp thu nhập theo biểu đồ tuần hoặc tháng. |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- **Xuất báo cáo:** Thợ chọn khoảng thời gian và bấm "Xuất báo cáo" để nhận file sao kê thu nhập qua email nhằm mục đích đối soát hoặc khai báo thuế cá nhân. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Đơn hàng đang tranh chấp:** Nếu đơn hàng đang bị khách hàng khiếu nại (ở UC-IM04), trạng thái thu nhập của đơn đó sẽ hiển thị là "Đang tạm giữ" cho đến khi Admin xử lý xong. |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1:** Thu nhập của mỗi đơn hàng phải được bóc tách rõ ràng: \[Số tiền khách trả\] - \[Phí nền tảng\] = \[Số tiền thực nhận\].<br><br>\- **Quy định 2:** Thợ không có quyền xóa lịch sử công việc (để đảm bảo tính minh bạch và làm căn cứ bảo hành cho khách).<br><br>\- **Quy định 3:** Các đánh giá từ khách hàng trong lịch sử công việc sẽ ảnh hưởng trực tiếp đến "Điểm uy tín" của thợ trên hệ thống. |
| --- | --- |

3\. Bảng Đặc tả Use Case: Đánh giá Thợ

| **Thành phần** | tp là cái l gì ???? |
| --- | --- |
| **Mã UC** | UC- |
| --- | --- |
| **Tên UC** | Đánh giá và Nhận xét Thợ |
| --- | --- |
| **Mô tả** | Sau khi công việc hoàn tất, Khách hàng có quyền chấm điểm sao (1-5) và để lại nhận xét về thái độ, chuyên môn của thợ. |
| --- | --- |
| **Tác nhân** | Khách hàng, Hệ thống |
| --- | --- |
| **Độ ưu tiên** | Bình thường |
| --- | --- |
| **Sự kiện kích hoạt** | Hệ thống ghi nhận đơn hàng chuyển sang trạng thái "Hoàn thành" (Thợ xác nhận thu tiền xong). |
| --- | --- |
| **Điều kiện tiên quyết** | \- Đơn hàng đã hoàn thành.<br><br>\- Khách hàng chưa thực hiện đánh giá cho đơn hàng này. |
| --- | --- |
| **Kết quả đầu ra** | Điểm số và nhận xét được lưu vào hồ sơ của Thợ. Điểm trung bình của thợ được hệ thống tính toán lại và cập nhật công khai. |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | **Bước 1:** Ngay khi đơn hàng hoàn thành, hệ thống tự động bật một popup hiển thị ảnh đại diện và tên thợ, kèm câu hỏi "Trải nghiệm dịch vụ của bạn thế nào?".<br><br>**Bước 2:** Khách hàng chạm vào số sao tương ứng (từ 1 đến 5 sao).<br><br>**Bước 3:** Khách hàng nhập nội dung nhận xét chi tiết vào ô văn bản (không bắt buộc).<br><br>**Bước 4:** Khách hàng bấm nút "Gửi đánh giá".<br><br>**Bước 5:** Hệ thống lưu trữ, hiện thông báo "Cảm ơn bạn đã đánh giá" và đóng popup. |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- **Đánh giá sau:** Khách hàng bấm nút "Bỏ qua" hoặc "Để sau" khi popup hiện lên. Sau đó, họ vào mục "Lịch sử đơn hàng", tìm đơn chưa đánh giá và bấm "Đánh giá ngay" để thực hiện lại luồng cơ bản. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Đánh giá điểm thấp:** Nếu khách hàng chọn từ 3 sao trở xuống, hệ thống bắt buộc khách hàng phải chọn lý do từ danh sách có sẵn (Thái độ kém, Thu thêm phụ phí sai quy định, Đến trễ, Tay nghề yếu) trước khi được phép bấm "Gửi". |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1:** Mỗi đơn hàng chỉ được đánh giá đúng 1 lần. Đã gửi thì không thể tự sửa (trừ khi liên hệ Admin).<br><br>\- **Quy định 2:** Hệ thống tự động lọc các từ ngữ chửi thề, thô tục trong phần nhận xét. Nếu vi phạm, nhận xét bị ẩn hoặc báo lỗi không cho gửi.<br><br>\- **Quy định 3:** Thợ có điểm trung bình dưới 3.0 trong tuần sẽ bị giảm ưu tiên nhận việc. |
| --- | --- |

4\. Bảng Đặc tả Use Case: Trung tâm hỗ trợ - để sau

| **Thành phần** | tp là cái l gì ???? |
| --- | --- |
| **Mã UC** | UC-04 |
| --- | --- |
| **Tên UC** | Trung tâm hỗ trợ và Báo cáo sự cố |
| --- | --- |
| **Mô tả** | Cung cấp các câu hỏi thường gặp (FAQ) và kênh liên hệ trực tiếp với bộ phận Chăm sóc khách hàng (Admin) để xử lý các vấn đề phát sinh như: báo cáo thợ thái độ kém, quỵt tiền, tranh chấp giá cả. |
| --- | --- |
| **Tác nhân** | Khách hàng, Thợ, Admin (Nhân viên CSKH), Hệ thống |
| --- | --- |
| **Độ ưu tiên** | Cao |
| --- | --- |
| **Sự kiện kích hoạt** | Người dùng bấm vào mục "Trung tâm hỗ trợ" trên menu, hoặc bấm nút "Báo cáo sự cố" trực tiếp trong chi tiết đơn hàng. |
| --- | --- |
| **Điều kiện tiên quyết** | Người dùng đã đăng nhập vào ứng dụng và có kết nối Internet. |
| --- | --- |
| **Kết quả đầu ra** | Người dùng tìm được câu trả lời qua FAQ, hoặc một phiếu yêu cầu hỗ trợ (Ticket) được tạo ra cho Admin, hoặc cuộc hội thoại với nhân viên CSKH được kết nối thành công. |
| --- | --- |
| **Luồng cơ bản (Mọi việc suôn sẻ)** | **Bước 1:** Người dùng vào "Trung tâm hỗ trợ".<br><br>**Bước 2:** Hệ thống hiển thị danh mục FAQ theo chủ đề (Thanh toán, Bảo hành, Tài khoản...) và mục "Liên hệ CSKH".<br><br>**Bước 3:** Người dùng bấm "Chat với CSKH".<br><br>**Bước 4:** Hệ thống yêu cầu chọn Đơn hàng cần hỗ trợ (hoặc vấn đề chung).<br><br>**Bước 5:** Ứng dụng kết nối với nhân viên CSKH đang online. Giao diện Live Chat mở ra.<br><br>**Bước 6:** Người dùng nhắn tin trình bày vấn đề (có thể gửi kèm hình ảnh bằng chứng). Admin tiếp nhận và xử lý sự cố. |
| --- | --- |
| **Luồng thay thế (Các cách xử lý khác)** | \- **Hết giờ làm việc/Không có Admin online:** Nếu người dùng bấm "Chat với CSKH" ngoài giờ hành chính, hệ thống hiển thị form điền "Gửi yêu cầu hỗ trợ (Ticket)". Người dùng nhập nội dung, hệ thống thông báo sẽ phản hồi qua email/app vào ngày làm việc tiếp theo.. |
| --- | --- |
| **Luồng ngoại lệ (Các tình huống sự cố)** | \- **Đánh giá điểm thấp:** Nếu khách hàng chọn từ 3 sao trở xuống, hệ thống bắt buộc khách hàng phải chọn lý do từ danh sách có sẵn (Thái độ kém, Thu thêm phụ phí sai quy định, Đến trễ, Tay nghề yếu) trước khi được phép bấm "Gửi". |
| --- | --- |
| **Quy tắc nghiệp vụ (Quy định bắt buộc)** | \- **Quy định 1:** Các yêu cầu báo cáo về "Thợ có hành vi nguy hiểm/Quỵt tiền" sẽ tự động được gán cờ "Khẩn cấp" (High Priority) đẩy lên đầu danh sách của Admin.<br><br>\- **Quy định 2:** Mọi nội dung chat với Admin và thông tin báo cáo đều được lưu trữ không thời hạn để phục vụ kiểm tra pháp lý nếu cần.<br><br>\- **Quy định 3:** Phải phản hồi ticket của người dùng trong vòng tối đa 24 giờ. |
| --- | --- |

  
<br/><br/>

# Use case ( chung ) MẪU THAM KHẢO ĐỂ VẼ TAYSeq - Ac - Phú (done)**DANH SÁCH SEQ VÀ ACT DIAGRAM**

## **I,Biểu đồ sequence diagram**

## **II,Biểu đồ activity diagram**

Vẽ như cc vẽ lại đi nhá

# Seq - Ac - Minh Tiến

1.  nhắn tin

1.  Quản lý hóa đơn cho thợ

- tìm kiếm:

- xóa

1.  đánh giá thợ

bảng invoices

- InvoiceID: primarykey
- BookingID: khóa ngoại
- TotalAmount: tổng số tiền khách phải trả
- PlatformFee:
- PaymentMethod: phương thức thanh toán
- status: trạng thái( pending/paid/cancelled)
- CreateAt
- PaitAt

Bảng InvoiceDetails

- DetailId: khóa chính
- InvoiceID: khóa ngoại
- ItemName: tên mục thu( tiền công thợ, vật liệu) Nvarchar
- Amount: số tiền cho mục thu ở trên (decimal)

**A. Bảng Reviews (Lưu chi tiết từng lượt đánh giá)** Bảng này lưu lại bằng chứng khách hàng nào đã đánh giá đơn hàng nào.

- ReviewID (Khóa chính)
- BookingID (Khóa ngoại, Unique) -> _Rất quan trọng:_ Cài đặt Unique (Duy nhất) để đảm bảo 1 chuyến đi/1 lần sửa chữa chỉ được đánh giá đúng 1 lần.
- CustomerID (Khóa ngoại) -> Ai đánh giá?
- WorkerID (Khóa ngoại) -> Đánh giá ai?
- StarRating (Int: từ 1 đến 5) -> Số sao.
- Comment (Nvarchar) -> Lời nhận xét (Có thể cho phép NULL nếu khách chỉ bấm sao mà không viết gì).
- CreatedAt (DateTime) -> Ngày đánh giá.

**B. Cập nhật bảng Workers (Lưu điểm trung bình - Dữ liệu Cache)** Bạn **không nên** dùng lệnh AVG(StarRating) để tính điểm trung bình mỗi lần hiển thị danh sách thợ trên app, vì khi có hàng nghìn lượt đánh giá, server sẽ bị chậm. Thay vào đó, hãy lưu thẳng điểm số vào bảng Workers:

- Thêm cột TotalReviews (Int) -> Tổng số lượt đánh giá thợ đã nhận (Ví dụ: 15).
- Thêm cột AverageRating (Decimal/Float) -> Điểm trung bình hiện tại (Ví dụ: 4.8).

# Seq - Ac - Hiếu

1.Sơ đồ sequence diagram

1.đăng ký tài khoản

2.Đăng nhập tài khoản

3.Đăng nhập bằng GG

[4.Qu](http://4.qu)ên mật khẩu

5.Duyệt hồ sơ thợ

6.Xác minh thợ

7.Cập nhật thông tin cá nhân

8.Xác minh khách hàng

II.Sơ dồ Activyti

1.Đăng ký khách hàng

2.Đăng nhập khách hàng

3.Đăng nhập bằng GG

[4.Qu](http://4.qu)ên mật khẩu

5.Xác minh khách hàng

6.Xác minh thợ

7.Duyệt hồ sơ thợ

8.Cập nhật thông tin cá nhân

# Seq - Ac - Thế Anh

# SQL-The Anh

\-- 1. Bảng Khách Hàng

CREATE TABLE KhachHang (

KhachHangID INT IDENTITY(1,1) PRIMARY KEY,

HoTen NVARCHAR(100) NOT NULL,

SoDienThoai VARCHAR(15) UNIQUE NOT NULL,

Email VARCHAR(100),

NgayTao DATETIME DEFAULT GETDATE()

);

\-- 2. Bảng Thợ

CREATE TABLE Tho (

ThoID INT IDENTITY(1,1) PRIMARY KEY,

HoTen NVARCHAR(100) NOT NULL,

SoDienThoai VARCHAR(15) UNIQUE NOT NULL,

TrangThaiSanSang BIT DEFAULT 1, -- 1: Sẵn sàng, 0: Không sẵn sàng (Biểu đồ 1 & 4)

ViTriHienTai NVARCHAR(255), -- Hỗ trợ tìm kiếm thợ gần nhất

NgayTao DATETIME DEFAULT GETDATE()

);

\-- 3. Bảng Danh Mục Dịch Vụ (Mục muốn sửa chữa)

CREATE TABLE DanhMucDichVu (

DichVuID INT IDENTITY(1,1) PRIMARY KEY,

TenDichVu NVARCHAR(100) NOT NULL,

MoTa NVARCHAR(255)

);

\-- Bảng trung gian: Thợ làm được những dịch vụ gì

CREATE TABLE Tho_DichVu (

ThoID INT FOREIGN KEY REFERENCES Tho(ThoID),

DichVuID INT FOREIGN KEY REFERENCES DanhMucDichVu(DichVuID),

PRIMARY KEY (ThoID, DichVuID)

);

\-- 4. Bảng Đơn Đặt Thợ (Booking)

CREATE TABLE DonDatTho (

DonDatID INT IDENTITY(1,1) PRIMARY KEY,

KhachHangID INT FOREIGN KEY REFERENCES KhachHang(KhachHangID),

ThoID INT FOREIGN KEY REFERENCES Tho(ThoID),

DichVuID INT FOREIGN KEY REFERENCES DanhMucDichVu(DichVuID),

ThoiGianDat DATETIME DEFAULT GETDATE(), -- Dùng để check logic quá 2 phút

TrangThai NVARCHAR(50) DEFAULT 'Pending', -- 'Pending', 'Accepted', 'Canceled', 'Completed'

DiaChi SuaChua NVARCHAR(255) NOT NULL

);

\-- 5. Bảng Chi Tiết Hủy Đơn

\-- Phục vụ biểu đồ 2: Lưu lý do hủy và phân loại để có hướng xử lý

CREATE TABLE ChiTietHuyDon (

HuyDonID INT IDENTITY(1,1) PRIMARY KEY,

DonDatID INT FOREIGN KEY REFERENCES DonDatTho(DonDatID),

NguoiHuy NVARCHAR(50), -- 'KhachHang' hoặc 'Tho'

PhanLoaiLyDo NVARCHAR(50), -- 'DoTho' (Lý do do thợ) hoặc 'DoNhuCau' (Lý do nhu cầu)

ChiTietLyDo NVARCHAR(MAX),

ThoiGianHuy DATETIME DEFAULT GETDATE()

);

\-- 6. Bảng Danh Sách Thợ Quen

\-- Phục vụ biểu đồ 3: Lưu lại các thợ mà khách hàng đã thêm vào danh sách yêu thích

CREATE TABLE DanhSachThoQuen (

KhachHangID INT FOREIGN KEY REFERENCES KhachHang(KhachHangID),

ThoID INT FOREIGN KEY REFERENCES Tho(ThoID),

NgayThem DATETIME DEFAULT GETDATE(),

PRIMARY KEY (KhachHangID, ThoID)

);

\-- 7. Bảng Lọc Thợ Bị Qua Bỏ (Blacklist)

\-- Phục vụ biểu đồ 2: Khi khách hàng hủy với lý do do thợ, thợ có thể bị đưa vào đây để không query ra ở lần sau

CREATE TABLE ThợBiBoQua (

KhachHangID INT FOREIGN KEY REFERENCES KhachHang(KhachHangID),

ThoID INT FOREIGN KEY REFERENCES Tho(ThoID),

LyDo NVARCHAR(255),

NgayBoQua DATETIME DEFAULT GETDATE(),

PRIMARY KEY (KhachHangID, ThoID)

);

Tiến

bảng invoices

- InvoiceID: primarykey
- BookingID: khóa ngoại
- TotalAmount: tổng số tiền khách phải trả
- PlatformFee:
- PaymentMethod: phương thức thanh toán
- status: trạng thái( pending/paid/cancelled)
- CreateAt
- PaitAt

Bảng InvoiceDetails

- DetailId: khóa chính
- InvoiceID: khóa ngoại
- ItemName: tên mục thu( tiền công thợ, vật liệu) Nvarchar
- Amount: số tiền cho mục thu ở trên (decimal)

**A. Bảng Reviews (Lưu chi tiết từng lượt đánh giá)** Bảng này lưu lại bằng chứng khách hàng nào đã đánh giá đơn hàng nào.

- ReviewID (Khóa chính)
- BookingID (Khóa ngoại, Unique)
- CustomerID (Khóa ngoại)
- WorkerID (Khóa ngoại)
- StarRating (Int: từ 1 đến 5)
- Comment (Nvarchar) .
- CreatedAt (DateTime)

**B. Cập nhật bảng Workers (Lưu điểm trung bình - Dữ liệu Cache)** Bạn **không nên** dùng lệnh AVG(StarRating) để tính điểm trung bình mỗi lần hiển thị danh sách thợ trên app, vì khi có hàng nghìn lượt đánh giá, server sẽ bị chậm. Thay vào đó, hãy lưu thẳng điểm số vào bảng Workers:

- Thêm cột TotalReviews (Int) -> Tổng số lượt đánh giá thợ đã nhận (Ví dụ: 15).
- Thêm cột AverageRating (Decimal/Float) -> Điểm trung bình hiện tại (Ví dụ: 4.8).

## **TÀI LIỆU ĐẶC TẢ CƠ SỞ DỮ LIỆU**

**Phân hệ:** Nhóm chức năng 4.5 (Quản lý Đối tác/Thợ)

**Dự án:** FixIt VN

### **1\. Danh mục các kiểu dữ liệu liệt kê (ENUM Types)**

Hệ thống sử dụng các tập giá trị cố định (ENUM) để tối ưu dung lượng và đảm bảo tính toàn vẹn dữ liệu:

- kyc_status: **INIT** (Khởi tạo), **APPROVED_AUTO** (Hệ thống tự động duyệt), **REJECT_AUTO** (Hệ thống tự động từ chối), **PENDING_MANUAL** (Chờ Admin duyệt tay).
- broadcast_status: **WAITING** (Đang chờ nhận), **ACCEPTED** (Đã nhận), **REJECTED** (Đã từ chối), **EXPIRED** (Hết hạn/Thợ khác đã nhận).
- proof_type: **BEFORE** (Ảnh trước thi công), **AFTER** (Ảnh sau thi công), **RECEIPT** (Hóa đơn vật tư).
- trans_type: **DEPOSIT** (Nạp tiền), **WITHDRAW** (Rút tiền), **HOLD** (Tạm giữ bảo hành), **RELEASE** (Giải phóng tiền), **DEBT** (Ghi nợ chiết khấu).

### **2\. Chi tiết các bảng dữ liệu (Data Dictionary)**

#### **2.1. Bảng workers (Thông tin Đối tác & Trạng thái hoạt động)**

**Mô tả:** Lưu trữ thông tin cốt lõi, định vị GPS và trạng thái sẵn sàng nhận việc của Thợ.

| **Tên trường** | **Kiểu dữ liệu** | **Khóa** | **Bắt buộc** | **Mô tả chi tiết** |
| --- | --- | --- | --- | --- |
| worker_id | UUID | PK  | Có  | Mã định danh duy nhất của thợ (Tự động tạo). |
| --- | --- | --- | --- | --- |
| user_id | UUID | FK  | Có  | Liên kết đến bảng users (Nhóm 4.1) để lấy thông tin đăng nhập/liên hệ. |
| --- | --- | --- | --- | --- |
| is_online | BOOLEAN | \-  | Có  | Trạng thái Bật/Tắt nhận cuốc. Mặc định: FALSE. |
| --- | --- | --- | --- | --- |
| current_lat | DECIMAL(9,6) | \-  | Không | Tọa độ Vĩ độ hiện tại để luồng điều phối quét bản đồ. |
| --- | --- | --- | --- | --- |
| current_long | DECIMAL(9,6) | \-  | Không | Tọa độ Kinh độ hiện tại. |
| --- | --- | --- | --- | --- |
| rating_avg | DECIMAL(2,1) | \-  | Có  | Điểm đánh giá trung bình. Mặc định: 5.0. |
| --- | --- | --- | --- | --- |
| reputation_score | INT | \-  | Có  | Điểm uy tín, dùng để xếp hạng ưu tiên phát đơn. Mặc định: 100. |
| --- | --- | --- | --- | --- |
| missed_count | INT | \-  | Có  | Số lần bỏ lỡ đơn liên tiếp. Tự động tắt is_online nếu vượt quá giới hạn. |
| --- | --- | --- | --- | --- |
| created_at | TIMESTAMP | \-  | Có  | Thời gian khởi tạo hồ sơ thợ. |
| --- | --- | --- | --- | --- |

#### 

#### 

#### 

#### **2.2. Bảng worker_kyc_profiles (Hồ sơ Định danh Điện tử - VNPT eKYC)**

**Mô tả:** Quản lý giấy tờ pháp lý và kết quả đối soát tự động từ API VNPT eKYC. Mối quan hệ 1-1 với bảng workers.

| **Tên trường** | **Kiểu dữ liệu** | **Khóa** | **Bắt buộc** | **Mô tả chi tiết** |
| --- | --- | --- | --- | --- |
| profile_id | UUID | PK  | Có  | Mã định danh hồ sơ KYC. |
| --- | --- | --- | --- | --- |
| worker_id | UUID | FK  | Có  | Khóa ngoại liên kết với workers (Ràng buộc Unique). |
| --- | --- | --- | --- | --- |
| id_card_number | VARCHAR(20) | \-  | Có  | Số CCCD (Bóc tách từ công nghệ OCR). |
| --- | --- | --- | --- | --- |
| full_name | VARCHAR(100) | \-  | Có  | Họ và tên hiển thị trên CCCD. |
| --- | --- | --- | --- | --- |
| dob | DATE | \-  | Có  | Ngày tháng năm sinh trên CCCD. |
| --- | --- | --- | --- | --- |
| front_url | VARCHAR(255) | \-  | Có  | Đường dẫn ảnh chụp mặt trước CCCD. |
| --- | --- | --- | --- | --- |
| back_url | VARCHAR(255) | \-  | Có  | Đường dẫn ảnh chụp mặt sau CCCD. |
| --- | --- | --- | --- | --- |
| selfie_url | VARCHAR(255) | \-  | Có  | Đường dẫn ảnh/video dùng để kiểm tra thực thể sống (Liveness Check). |
| --- | --- | --- | --- | --- |
| vnpt_trans_id | VARCHAR(50) | \-  | Không | Mã giao dịch trả về từ VNPT để phục vụ đối soát, tra cứu lỗi. |
| --- | --- | --- | --- | --- |
| face_match_score | DECIMAL(5,2) | \-  | Không | Tỷ lệ phần trăm khớp khuôn mặt giữa Selfie và ảnh CCCD. |
| --- | --- | --- | --- | --- |
| status | ENUM | \-  | Có  | Trạng thái phê duyệt (kyc_status). Mặc định: INIT. |
| --- | --- | --- | --- | --- |
| processed_at | TIMESTAMP | \-  | Không | Thời điểm hoàn tất quy trình eKYC. |
| --- | --- | --- | --- | --- |

#### 

#### **2.3. Bảng worker_skills (Chuyên môn & Kỹ năng)**

**Mô tả:** Lưu trữ danh sách các nghề/dịch vụ mà thợ có khả năng thực hiện (Quan hệ Many-to-Many).

| **Tên trường** | **Kiểu dữ liệu** | **Khóa** | **Bắt buộc** | **Mô tả chi tiết** |
| --- | --- | --- | --- | --- |
| id  | UUID | PK  | Có  | Mã định danh bản ghi kỹ năng. |
| --- | --- | --- | --- | --- |
| worker_id | UUID | FK  | Có  | Mã thợ. |
| --- | --- | --- | --- | --- |
| category_id | UUID | FK  | Có  | Mã danh mục dịch vụ (Liên kết với module Dịch vụ). |
| --- | --- | --- | --- | --- |
| exp_years | INT | \-  | Có  | Số năm kinh nghiệm làm nghề này của thợ. |
| --- | --- | --- | --- | --- |

#### 

#### **2.4. Bảng worker_broadcasts (Lịch sử Nhận/Phát đơn)**

**Mô tả:** Ghi nhận lịch sử hệ thống phát đơn hàng đến thiết bị của thợ để xử lý tranh chấp (Race Condition).

| **Tên trường** | **Kiểu dữ liệu** | **Khóa** | **Bắt buộc** | **Mô tả chi tiết** |
| --- | --- | --- | --- | --- |
| broadcast_id | UUID | PK  | Có  | Mã phiên phát đơn. |
| --- | --- | --- | --- | --- |
| order_id | UUID | FK  | Có  | Mã đơn hàng được phát (Liên kết module Order). |
| --- | --- | --- | --- | --- |
| worker_id | UUID | FK  | Có  | Mã thợ nhận được thông báo. |
| --- | --- | --- | --- | --- |
| status | ENUM | \-  | Có  | Trạng thái phản hồi của thợ (broadcast_status). |
| --- | --- | --- | --- | --- |
| created_at | TIMESTAMP | \-  | Có  | Thời điểm hệ thống "bắn" thông báo. |
| --- | --- | --- | --- | --- |

#### 

#### **2.5. Bảng proof_of_works (Bằng chứng nghiệm thu)**

**Mô tả:** Nơi lưu trữ hình ảnh hiện trường nhằm bảo vệ thợ khi có tranh chấp bảo hành.

| **Tên trường** | **Kiểu dữ liệu** | **Khóa** | **Bắt buộc** | **Mô tả chi tiết** |
| --- | --- | --- | --- | --- |
| proof_id | UUID | PK  | Có  | Mã định danh bằng chứng. |
| --- | --- | --- | --- | --- |
| order_id | UUID | FK  | Có  | Đơn hàng phát sinh bằng chứng. |
| --- | --- | --- | --- | --- |
| worker_id | UUID | FK  | Có  | Thợ thực hiện upload bằng chứng. |
| --- | --- | --- | --- | --- |
| image_url | VARCHAR(255) | \-  | Có  | Đường dẫn ảnh lưu trữ trên Cloud/S3. |
| --- | --- | --- | --- | --- |
| proof_type | ENUM | \-  | Có  | Phân loại ảnh (proof_type). |
| --- | --- | --- | --- | --- |
| created_at | TIMESTAMP | \-  | Có  | Thời gian upload ảnh. |
| --- | --- | --- | --- | --- |

#### 

#### **2.6. Bảng worker_wallets (Quản lý Ví Thợ)**

**Mô tả:** Quản lý dòng tiền của thợ, tách biệt 3 ngăn để phục vụ nghiệp vụ bảo hành và chiết khấu.

| **Tên trường** | **Kiểu dữ liệu** | **Khóa** | **Bắt buộc** | **Mô tả chi tiết** |
| --- | --- | --- | --- | --- |
| wallet_id | UUID | PK  | Có  | Mã định danh ví. |
| --- | --- | --- | --- | --- |
| worker_id | UUID | FK  | Có  | Chủ sở hữu ví (Ràng buộc Unique). |
| --- | --- | --- | --- | --- |
| balance_available | DECIMAL(12,2) | \-  | Có  | Số dư khả dụng, thợ có thể thực hiện lệnh rút tiền về ngân hàng. |
| --- | --- | --- | --- | --- |
| balance_holding | DECIMAL(12,2) | \-  | Có  | Số dư đang tạm giữ để đảm bảo thời gian bảo hành (96 giờ). |
| --- | --- | --- | --- | --- |
| balance_debt | DECIMAL(12,2) | \-  | Có  | Số dư ghi nợ, phí chiết khấu nền tảng thợ phải trả khi thu tiền mặt từ khách. |
| --- | --- | --- | --- | --- |

#### 

#### **2.7. Bảng worker_transactions (Lịch sử Giao dịch Ví)**

**Mô tả:** Nhật ký ghi nhận mọi biến động cộng/trừ tiền trong ví thợ để đối soát.

| **Tên trường** | **Kiểu dữ liệu** | **Khóa** | **Bắt buộc** | **Mô tả chi tiết** |
| --- | --- | --- | --- | --- |
| trans_id | UUID | PK  | Có  | Mã giao dịch. |
| --- | --- | --- | --- | --- |
| wallet_id | UUID | FK  | Có  | Liên kết ví phát sinh biến động. |
| --- | --- | --- | --- | --- |
| order_id | UUID | FK  | Không | Mã đơn hàng liên quan (Có thể Null nếu là giao dịch nạp/rút độc lập). |
| --- | --- | --- | --- | --- |
| amount | DECIMAL(12,2) | \-  | Có  | Số tiền giao dịch. |
| --- | --- | --- | --- | --- |
| type | ENUM | \-  | Có  | Loại giao dịch (trans_type). |
| --- | --- | --- | --- | --- |
| created_at | TIMESTAMP | \-  | Có  | Thời gian phát sinh giao dịch. |
| --- | --- | --- | --- | --- |
| release_at | TIMESTAMP | \-  | Không |     |
| --- | --- | --- | --- | --- |

# Tech Stack

**Coong nghe**

- Cơ sở dữ liệu: postgreSQL + redis ( Cho sử dụng bản đồ ) + mongodb ( chat )
- giao diện app: java mobile
- Server backend: Java spring boot
- Cloud lưu ảnh: cloudinary hoặc cloudflare storeage
- Quản lý giao dịch thanh toán: SePay
- Tạo mã qr: Vietqr
- !!!! giao diện admin và liên kết ngân hàng
- Architecture:
    - MVVM: Java mobile
    - Thiết kế theo kiến trúc: model -> repository -> service -> controller
- Keyc: VNPT keyc https://vnptai.io/ekyc/vi/price
- Design patterns:

**Key Word**

- 3.Recycel view
- 4.SQLite
- 5.Room Database
- 6.Acdapter
- 7.onUpdate
- 8.cơ chết ORM Note
- 9\. Backgroud thead
- 10\. Livedata
- 11\. MVVM
- 12\. Observer pattern
- 13\. Lifecycle aware
- Fragment
- Thiet ke da man hinh
- Framelayout

// ==========================================

// 1. NHÓM TÀI KHOẢN & XÁC THỰC (Account & Auth)

// ==========================================

// Bảng lưu thông tin gốc của tất cả người dùng (Khách, Thợ, Admin)

Table Users {

id uuid \[pk\] // Mã định danh duy nhất của người dùng

phone_number varchar(15) \[unique, not null\] // Số điện thoại dùng để đăng nhập

email varchar(255) \[unique\] // Email liên hệ (có thể rỗng)

password_hash varchar(255) // Mật khẩu đã được mã hóa

role varchar(20) // Phân quyền: Customer (Khách), Worker (Thợ), Admin (Quản trị)

avatar_url text // Link ảnh đại diện

is_active boolean \[default: true\] // Trạng thái khóa/mở tài khoản

created_at timestamp // Thời gian tạo tài khoản

}

// Bảng lưu hồ sơ riêng của Thợ

Table Workers {

worker_id uuid \[pk, ref: > Users.id\] // Mã thợ (liên kết với bảng Users)

full_name varchar(100) // Họ và tên thật (khớp với CCCD)

identity_card varchar(20) \[unique\] // Số Căn cước công dân

verification_status varchar(50) \[default: 'Pending'\] // Trạng thái duyệt hồ sơ: Pending (Chờ), Approved (Đã duyệt), Rejected (Từ chối)

latitude numeric(10,8) // Tọa độ GPS động (Vĩ độ) - Vị trí hiện tại của thợ

longitude numeric(11,8) // Tọa độ GPS động (Kinh độ) - Vị trí hiện tại của thợ

is_available boolean \[default: false\] // Công tắc Bật/Tắt nhận đơn của thợ

reputation_score numeric(3,1) \[default: 5.0\] // Điểm đánh giá uy tín (Tối đa 5.0)

missed_count int \[default: 0\] // Đếm số lần lờ đơn hàng (để hệ thống tự động phạt)

}

// Bảng lưu hồ sơ định danh điện tử (VNPT eKYC) của thợ

Table Worker_Identity_Cards {

id uuid \[pk\] // Mã bản ghi

worker_id uuid \[unique, ref: > Workers.worker_id\] // Hồ sơ này của thợ nào

front_image_url text // Link ảnh CCCD mặt trước

back_image_url text // Link ảnh CCCD mặt sau

vnpt_ekyc_hash varchar(255) // Mã băm/Mã đối soát trả về từ hệ thống VNPT

status varchar(50) \[default: 'Pending'\] // Trạng thái xác thực khuôn mặt & giấy tờ

}

// Bảng lưu hồ sơ riêng của Khách hàng

Table Customers {

customer_id uuid \[pk, ref: > Users.id\] // Mã khách hàng (liên kết với bảng Users)

full_name varchar(100) // Tên hiển thị của khách

loyalty_points int \[default: 0\] // Điểm tích lũy sau mỗi lần hoàn thành đơn để đổi mã giảm giá

cancelled_count int \[default: 0\] // Đếm số lần khách "bom" thợ để Admin khóa tài khoản

}

// Bảng lưu sổ địa chỉ quen thuộc của khách (Giúp đặt đơn nhanh 1 chạm)

Table Customer_Addresses {

id uuid \[pk\] // Mã bản ghi

customer_id uuid \[ref: > Customers.customer_id\] // Thuộc về khách hàng nào

label varchar(50) // Tên gợi nhớ. VD: "Nhà riêng", "Công ty", "Phòng trọ"

address text \[not null\] // Địa chỉ chi tiết (dạng chữ)

latitude numeric(10,8) // Vĩ độ của địa chỉ này

longitude numeric(11,8) // Kinh độ của địa chỉ này

is_default boolean \[default: false\] // Cờ đánh dấu đây là địa chỉ mặc định khi mở app

}

// Bảng quản lý mã xác thực OTP gửi qua SMS

Table Otp_Codes {

id uuid \[pk\] // Mã bản ghi

phone_number varchar(15) // Số điện thoại nhận mã

otp_code varchar(6) // Mã OTP 6 số

action_type varchar(50) // Phân loại mục đích dùng OTP: Register (Đăng ký), Forgot_Password (Quên pass), Withdraw_Money (Rút tiền)

expires_at timestamp // Thời gian mã hết hạn (thường là sau 2-3 phút)

is_used boolean \[default: false\] // Cờ đánh dấu mã này đã được sử dụng chưa (chống dùng lại 2 lần)

}

// Bảng quản lý phiên đăng nhập (Refresh Tokens)

Table Refresh_Tokens {

id uuid \[pk\] // Mã bản ghi

user_id uuid \[ref: > Users.id\] // Thuộc về người dùng nào

token text \[not null\] // Chuỗi Refresh Token được cấp

expires_at timestamp \[not null\] // Thời gian hết hạn của token

is_revoked boolean \[default: false\] // Cờ đánh dấu token này đã bị thu hồi/đăng xuất chưa

created_at timestamp // Thời gian tạo

}

// ==========================================

// 2. NHÓM DỊCH VỤ & TÌM KIẾM (Service & Search)

// ==========================================

// Bảng danh mục ngành nghề lớn

Table Service_Categories {

id int \[pk, increment\] // Mã danh mục (tự tăng)

service_name varchar(255) \[not null\] // Tên nghề. VD: "Sửa điện lạnh", "Sửa ống nước"

}

// Bảng danh sách bệnh/vật tư chi tiết (Dùng để thợ tick chọn báo giá nhanh)

Table Service_Items {

id int \[pk, increment\] // Mã hạng mục

service_category_id int \[ref: > Service_Categories.id\] // Thuộc ngành nghề nào

item_name varchar(255) \[not null\] // Tên hạng mục. VD: "Bơm gas điều hòa", "Thay lốc tủ lạnh"

suggested_price numeric(12,2) // Mức giá tham khảo cho hạng mục này

}

// Bảng Hồ sơ năng lực của thợ (Cho biết thợ biết làm nghề gì)

Table Worker_Services {

worker_id uuid \[ref: > Workers.worker_id\] // Mã người thợ

service_id int \[ref: > Service_Categories.id\] // Mã nghề mà thợ này biết làm

base_price numeric(12,2) // Giá công thợ cơ bản cho nghề này (nếu có)

indexes {

(worker_id, service_id) \[pk\] // Khóa chính kép: 1 thợ không thể đăng ký 1 nghề 2 lần

}

}

//Bảng thợ quen, thợ yêu thích

Table Favorite_Workers {

customer_id uuid \[ref: > Customers.customer_id\]

worker_id uuid \[ref: > Workers.worker_id\]

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

customer_id uuid \[ref: > Customers.customer_id\] // Khách nào đặt

worker_id uuid \[ref: > Workers.worker_id\] // Thợ nào nhận

service_id int \[ref: > Service_Categories.id\] // Yêu cầu sửa nghề gì

address text \[not null\] // Địa chỉ nhà khách (dạng chữ)

destination_lat numeric(10,8) // Tọa độ GPS tĩnh (Vĩ độ) của nhà khách

destination_lng numeric(11,8) // Tọa độ GPS tĩnh (Kinh độ) của nhà khách

issue_description text // Lời nhắn/Mô tả bệnh thiết bị từ khách hàng

scheduled_time timestamp // Khung giờ khách hàng mong muốn thợ có mặt

payment_method varchar(50) \[default: 'CASH'\] // Hình thức thanh toán: CASH (Tiền mặt), BANK_TRANSFER (Chuyển khoản), WALLET (Ví)

final_price numeric(12,2) // Giá tiền chốt cuối cùng sau khi mặc cả

status varchar(50) \[default: 'Pending'\] // Trạng thái đơn: Pending, Accepted, Surveying, Waiting_Approval, In_Progress, Completed, Cancelled

created_at timestamp // Thời gian khách ấn nút tạo đơn

}

// Bảng Thẻ Báo Giá (Biên bản khảo sát từ thợ gửi khách)

Table Worker_Quotations {

id uuid \[pk\] // Mã thẻ báo giá

worker_id uuid \[ref: > Workers.worker_id\] // Thợ nào báo giá

booking_id uuid \[ref: > Bookings.id\] // Của đơn hàng nào

items_description text // Nội dung chi tiết các hạng mục sửa chữa/vật tư

total_proposed_price numeric(12,2) \[not null\] // Tổng tiền thợ đề xuất

status varchar(50) \[default: 'Pending'\] // Trạng thái duyệt báo giá từ khách: Pending, Accepted, Rejected

created_at timestamp // Thời gian phát hành báo giá

}

// Bảng Bằng chứng thép (Lưu ảnh chụp hiện trường)

Table Proof_Of_Works {

id uuid \[pk\] // Mã bằng chứng

booking_id uuid \[ref: > Bookings.id\] // Của đơn hàng nào

image_url text \[not null\] // Link ảnh chụp hiện trạng

proof_type varchar(50) // Phân loại ảnh: BEFORE_REPAIR (Chụp trước khi sửa), AFTER_REPAIR (Chụp sau khi sửa xong)

captured_at timestamp \[default: \`CURRENT_TIMESTAMP\`\] // Thời điểm bấm chụp ảnh

}

// Bảng Lịch sử/Lý do hủy đơn

Table Cancellation_Details {

booking_id uuid \[pk, ref: > Bookings.id\] // Mã đơn bị hủy

cancelled_by_id uuid \[ref: > Users.id\] // Ai là người bấm hủy

cancelled_by_role varchar(20) // Vai trò người hủy: Khách, Thợ hay Admin

reason_category varchar(50) // Phân loại lỗi hủy (để thống kê). VD: Thợ đến trễ, Khách đổi ý, Sai thông tin

cancellation_reason text // Lời giải thích chi tiết lý do hủy

reputation_penalty_applied numeric(3,1) \[default: 0\] // Số điểm uy tín bị hệ thống trừ đi do hủy sai quy định

cancelled_at timestamp // Thời gian bấm hủy

}

// Bảng Nhật ký đơn hàng (Track các mốc thời gian)

Table Booking_Histories {

id uuid \[pk\] // Mã nhật ký

booking_id uuid \[ref: > Bookings.id\] // Của đơn hàng nào

status_update varchar(50) // Trạng thái được cập nhật sang gì

updated_at timestamp // Vào lúc mấy giờ (Giúp biết được thợ làm mất bao lâu)

}

// Bảng Khiếu nại bảo hành (Kích hoạt khi khách không hài lòng sau khi sửa)

Table Complaint_Warranties {

id uuid \[pk\] // Mã khiếu nại

booking_id uuid \[unique, ref: > Bookings.id\] // Khiếu nại cho đơn hàng nào (1 đơn chỉ được khiếu nại 1 lần)

customer_reason text // Lời phàn nàn của khách

worker_response text // Lời giải thích/phản biện của thợ

status varchar(50) \[default: 'Pending'\] // Trạng thái xử lý của Admin: Pending, Worker_Responded, Resolved

deadline_to_respond timestamp // Thời hạn chót (thường là 24h) bắt buộc thợ phải vào giải trình

created_at timestamp // Thời gian khách bấm khiếu nại

}

// ==========================================

// 4. NHÓM VÍ THỢ & THANH TOÁN (Wallet & Payment)

// ==========================================

// Bảng Ví ảo của thợ

Table Worker_Wallets {

worker_id uuid \[pk, ref: > Workers.worker_id\] // Ví của thợ nào (1 thợ có 1 ví)

available_balance numeric(12,2) \[default: 0\] // Tiền khả dụng (Có thể rút ngay về ngân hàng)

held_balance numeric(12,2) \[default: 0\] // Tiền bị giam 96h (Quỹ bảo hành chờ nhả)

debt_balance numeric(12,2) \[default: 0\] // Tiền nợ nền tảng (Phát sinh khi khách đưa tiền mặt cho thợ, thợ nợ lại % hoa hồng)

}

// Bảng Tài khoản ngân hàng thực tế của thợ

Table Worker_Bank_Accounts {

id uuid \[pk\] // Mã thẻ

worker_id uuid \[ref: > Workers.worker_id\] // Thẻ này của thợ nào

bank_name varchar(100) // Tên ngân hàng. VD: MBBank, Vietcombank

account_number varchar(50) // Số tài khoản ngân hàng

account_name varchar(100) // Tên chủ thẻ (Bắt buộc phải giống tên trong CCCD)

is_default boolean \[default: true\] // Cờ chọn thẻ mặc định để nhận tiền rút về

}

// Bảng Lịch sử biến động số dư (Đối soát dòng tiền)

Table Transaction_Histories {

id uuid \[pk\] // Mã giao dịch

wallet_id uuid \[ref: > Worker_Wallets.worker_id\] // Giao dịch của ví nào

booking_id uuid \[ref: > Bookings.id\] // Giao dịch sinh ra từ đơn nào (Nếu nạp/rút từ ngoài thì để NULL)

transaction_type varchar(50) // Loại GD: Deposit (Nạp), Withdraw (Rút), Holding (Giữ bảo hành), Release (Nhả tiền), Fee_Deduction (Trừ phí hoa hồng)

amount numeric(12,2) // Số tiền biến động

transaction_code varchar(50) \[unique\] // Mã cú pháp tự sinh để nhúng vào QR VietQR (SePay bắt mã này)

gateway_reference_code varchar(100) // Mã giao dịch đối soát do SePay/Ngân hàng trả về

target_bank_account_id uuid \[ref: > Worker_Bank_Accounts.id\] // Nếu là lệnh rút, tiền được bắn về thẻ nào

status varchar(50) \[default: 'Pending'\] // Trạng thái GD: Pending (Chờ), Success (Thành công), Failed (Lỗi)

admin_note text // Lời ghi chú của Admin khi duyệt lệnh nạp/rút bằng tay

transaction_time timestamp // Thời gian thực hiện giao dịch

}

// ==========================================

// 5. NHÓM TIỆN ÍCH (Utility)

// ==========================================

// Bảng Đánh giá chất lượng

Table Reviews {

id uuid \[pk\] // Mã đánh giá

booking_id uuid \[unique, ref: > Bookings.id\] // Đánh giá cho đơn nào

rating int // Số sao (Từ 1 đến 5)

reason_tag varchar(100) // BỔ SUNG: "Thái độ kém", "Đến trễ"... (Bắt buộc nếu rating <= 3)

comment text // Lời nhận xét chi tiết

created_at timestamp // Thời gian gửi đánh giá

}

// Bảng Lịch sử Thông báo trong App

Table Notifications {

id uuid \[pk\] // Mã thông báo

user_id uuid \[ref: > Users.id\] // Gửi cho ai (Khách hoặc Thợ)

title varchar(255) // Tiêu đề. VD: "Có đơn sửa mới!"

content text // Nội dung chi tiết

is_read boolean \[default: false\] // Đánh dấu đã đọc hay chưa (hiện chấm đỏ)

created_at timestamp // Thời gian thông báo bắn tới

}

// Bảng Định danh Thiết bị (Để gửi Push Notification bằng Firebase FCM)

Table User_Devices {

id uuid \[pk\] // Mã bản ghi

user_id uuid \[ref: > Users.id\] // Điện thoại này đang được đăng nhập bởi ai

device_token varchar(255) \[not null\] // Mã Token siêu dài do Firebase cấp để định vị đúng máy này

device_os varchar(20) // Hệ điều hành (iOS hoặc Android) để tối ưu định dạng tin nhắn

last_active timestamp // Lần cuối mở app (Dùng để dọn dẹp các token rác nếu người dùng đã gỡ app quá lâu)

}

# vấn đề db - fix

**dbdgram mới tham khảo : https://dbdiagram.io/d/WorkerVN-69ef26d9ddb9320fdc610318**

**Các vấn đề:**

\- Ở mục ví thì thiếu 1 mục là số tiền đang nợ ngoài mục tiền khả dung ( có thể rút ) và tiền đang giữ ( tiền bảo hiểm )

\- Thiếu 1 bảng lưu thông tin khiếu nại đối với tiền giữ bảo hiểm 96 ngày

\- cần thêm 1 bảng báo giá Worker_Quotations để khi chốt giá cuối cùng ng thợ bấm gửi giá trực tiếp qua app cho khách ( nếu thêm bảng này có thể loại bỏ bảng hóa đơn và chi tiết hóa đơn vì bảng báo giá kia khi khách chốt chính là hóa đơn luôn, tang hiệu suất truy vấn của hệ thống )

\- Thêm bảng proof_of_work chụp ảnh lưu bằng chứng hiện trạng trước và sau khi sửa.

\- Bảng booking thiếu trường thời gian khách muốn thợ đến (hẹn lịch trc ), phương thức khách hàng thanh toán

\- Bổ sung customers thêm trường đếm số lần hủy đơn ( khách hàng xấu - để khóa nick), bảng address linh hoạt hơn cho customers ( nếu cần )

Nếu sử dung 1 bảng ảnh chung thì vấn đề về ảnh keyc sẽ rất phức tạp vì hệ thống bên ngoài sẽ có 1 cấu trúc chung cho việc phân tích thì ở phần backend sẽ phải code xử lý rất loằng ngoằng và tốn thời gian. Ngoài ra vc bảng proof_of_works k chỉ là ảnh báo cáo mà nó còn là các trường lquan để nếu phản triển admin có thể xem xét và giải quyết. Nên loại bỏ bảng image_reference vì dự liệu ảnh hệ thống có các nghiệp vụ khác nhau nên cần ở từng bảng riêng.

- thiếu bảng notifications vì khi thợ thao tác gì đó cần gửi tbao cho khách biết. Bảng notifications sẽ lưu các lsu thông báo cũng như thông tin thông báo đó
- thểm trường action_otp trong trường hợp rút tiền ngân hàng hoặc gì đó cũng cần otp chứ k chỉ là đăng ký tài khoản hoặc quên tài khoản
- Hiện tại thanh toán việc tích hợp cổng hơi khó vì mất phí nên luồng hiện tại sử dụng sepay là:
    - **Nạp tiền:**
    - Khi thợ muốn nạp 500k, thì chọn nạp 500k sau đó hiện mã qr
    - Người thợ chụp mã qr vào ngân hàng thanh toán
    - sau đó quay lại app tôi ấn xác nhận thanh toán
    - sepay sẽ trả tbao cho hệ thống để biết nó thành công hay chưa rồi gửi tbao phù hợp cho thợ
    - **Rút tiền:**
    - vì dịch vụ payout sẽ mất tiền để sử dụng nên sẽ demo bằng data có sẵn
    - Thợ chọn rút tiền và nhập số tiền mong muốn
    - Hệ thống ktra số tiền khả dụng có phù hợp với hay k kèm các validate đặc biệt
    - sau đó sẽ đóng gói thông tin yêu cầu rút tiền đó gửi lên server và thoogn qua 1 dvu thứ 3.
    - Trừ tiền tkhoan ngân hàng và vào cho thợ sau đó gửi tbao qua sepay

\=> Đã chỉnh sửa bảng thanh toán cho phù hợp với hệ thống ( transactions và transactiosn_history và thêm bảng worker_bank_Accounts ( thông tin tkhoan ngân hàng thợ )

Thêm bảng **user_services** để đẩy đc thông báo kể cả khi người dùng thoát app

- **user_id:** Định danh xem cái điện thoại này đang được ai đăng nhập. (Ví dụ: Thợ Bùi Văn A). Một người có thể dùng 2 điện thoại (1 cái iPhone, 1 cái Samsung), nên 1 user_id có thể gắn với nhiều dòng trong bảng này.
- **device_token:** Đây là chuỗi ký tự rất dài do Google/Apple cấp cho app FixIt VN khi app vừa cài xong trên máy. Nó giống như **địa chỉ chính xác của cái điện thoại**. Máy chủ của bạn sẽ cầm chuỗi này, ném cho Google và bảo: _"Hãy hiện chữ 'Có đơn sửa ống nước mới' lên màn hình của cái máy có mã token này"_.
- **device_os (Hệ điều hành iOS hay Android):** Rất quan trọng vì Apple và Google có cách nhận gói tin báo "Ting ting" hơi khác nhau một chút. Máy chủ cần biết máy này là iPhone hay Samsung để gói gém tin nhắn cho đúng chuẩn.
- **last_active (Lần cuối sử dụng):** Dùng để dọn dẹp dữ liệu. Có những khách hàng cài app xong, nửa năm sau họ xóa app đi. Cái device_token đó trở thành "token chết". Máy chủ của bạn cứ cố gửi thông báo vào cái máy đã xóa app thì sẽ gây nghẽn mạng và tốn tài nguyên. Dựa vào ngày tháng ở cột này, hệ thống sẽ tự động xóa bớt các máy đã "bỏ trốn" quá lâu.

### **Triển khai Chat với NoSQL (MongoDB) - Lùi**

Tin nhắn có đặc điểm là: nhiều, ngắn, và đôi khi kèm ảnh/video.

- **Cách làm:** Bạn sẽ dùng booking_id làm khóa chính. Mỗi cuộc hội thoại là một tài liệu (document) lớn trong MongoDB.
- **Lợi ích:** Bạn có thể thêm các tính năng như "đã xem", "thu hồi" hay gửi tệp đính kèm rất linh hoạt mà không cần phải thay đổi cấu trúc bảng như bên SQL.

### **Triển khai GPS thực tế với Redis - Phú**

việc thợ di chuyển trên bản đồ sinh ra hàng nghìn yêu cầu mỗi phút.

- **Cách làm:** Điện thoại thợ sẽ "bắn" tọa độ lên Backend. Backend chỉ việc ném tọa độ đó vào Redis bằng lệnh GEOADD.
- **Lợi ích:** Khi khách hàng tìm thợ, hệ thống chỉ cần quét trong Redis (trên bộ nhớ RAM) nên tốc độ phản hồi sẽ gần như tức thì, thay vì phải bắt PostgreSQL "cày" trên ổ cứng.

**Bổ sung:**

- Phần backend: xử lý thêm jwt ( json web token ) là 1 mã định danh giúp trang web biết mình là user nào từ đó đưa tài nguyên cho user đó xem. Khi login thành công nó sẽ cho bạn 1 mã hash accessToken

Để tối ưu hóa việc thợ gửi tọa độ liên tục mỗi 5 giây mà không làm hao pin điện thoại hay tốn băng thông 4G của họ, bạn dự định dùng giao thức gửi dữ liệu nào từ máy điện thoại lên máy chủ (gọi API HTTP thông thường hay dùng giao thức nhẹ như MQTT)?

Code db:  
\-- 0. UUID

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

\-- 1. USERS

CREATE TABLE users (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

phone_number VARCHAR(15) UNIQUE NOT NULL,

email VARCHAR(255) UNIQUE,

password_hash VARCHAR(255),

role VARCHAR(20) CHECK (role IN ('Customer', 'Worker', 'Admin')),

avatar_url TEXT,

is_active BOOLEAN DEFAULT TRUE,

created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE user_social_logins (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

user_id UUID REFERENCES users(id) ON DELETE CASCADE,

provider VARCHAR(50),

provider_id VARCHAR(255) NOT NULL,

created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE otp_codes (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

phone_number VARCHAR(15),

otp_code VARCHAR(6),

action_type VARCHAR(50),

expires_at TIMESTAMP,

is_used BOOLEAN DEFAULT FALSE

);

CREATE TABLE refresh_tokens (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

user_id UUID REFERENCES users(id) ON DELETE CASCADE,

token TEXT NOT NULL,

expires_at TIMESTAMP NOT NULL,

is_revoked BOOLEAN DEFAULT FALSE,

created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

\-- 2. PROFILE

CREATE TABLE workers (

worker_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,

full_name VARCHAR(100),

identity_card VARCHAR(20) UNIQUE,

verification_status VARCHAR(50) DEFAULT 'Pending'

CHECK (verification_status IN ('Pending', 'Approved', 'Rejected')),

latitude NUMERIC(10,8),

longitude NUMERIC(11,8),

is_available BOOLEAN DEFAULT FALSE,

reputation_score NUMERIC(3,1) DEFAULT 5.0,

missed_count INT DEFAULT 0,

rejection_count INT DEFAULT 0,

rejected_priority_until TIMESTAMP,

experience_description TEXT,

service_area VARCHAR(255)

);

CREATE TABLE worker_identity_cards (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

worker_id UUID UNIQUE REFERENCES workers(worker_id) ON DELETE CASCADE,

front_image_url TEXT,

back_image_url TEXT,

vnpt_ekyc_hash VARCHAR(255),

status VARCHAR(50) DEFAULT 'Pending'

);

CREATE TABLE customers (

customer_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,

full_name VARCHAR(100)

);

CREATE TABLE customer_addresses (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

customer_id UUID REFERENCES customers(customer_id) ON DELETE CASCADE,

label VARCHAR(50),

address TEXT NOT NULL,

latitude NUMERIC(10,8),

longitude NUMERIC(11,8),

is_default BOOLEAN DEFAULT FALSE

);

\-- 3. SERVICES

CREATE TABLE service_categories (

id SERIAL PRIMARY KEY,

service_name VARCHAR(255) NOT NULL

);

CREATE TABLE service_items (

id SERIAL PRIMARY KEY,

service_category_id INT REFERENCES service_categories(id) ON DELETE CASCADE,

item_name VARCHAR(255) NOT NULL,

suggested_price NUMERIC(12,2)

);

CREATE TABLE worker_services (

worker_id UUID REFERENCES workers(worker_id) ON DELETE CASCADE,

service_id INT REFERENCES service_categories(id) ON DELETE CASCADE,

base_price NUMERIC(12,2),

PRIMARY KEY (worker_id, service_id)

);

CREATE TABLE favorite_workers (

customer_id UUID REFERENCES customers(customer_id) ON DELETE CASCADE,

worker_id UUID REFERENCES workers(worker_id) ON DELETE CASCADE,

saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

PRIMARY KEY (customer_id, worker_id)

);

\-- 4. BOOKINGS

CREATE TABLE bookings (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

customer_id UUID REFERENCES customers(customer_id),

worker_id UUID REFERENCES workers(worker_id),

service_id INT REFERENCES service_categories(id),

address TEXT NOT NULL,

destination_lat NUMERIC(10,8),

destination_lng NUMERIC(11,8),

issue_description TEXT,

scheduled_time TIMESTAMP,

payment_method VARCHAR(50) DEFAULT 'CASH',

final_price NUMERIC(12,2),

status VARCHAR(50) DEFAULT 'Pending',

created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE booking_worker_assignments (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

booking_id UUID REFERENCES bookings(id) ON DELETE CASCADE,

worker_id UUID REFERENCES workers(worker_id),

status VARCHAR(50) DEFAULT 'Pending',

assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

responded_at TIMESTAMP

);

CREATE TABLE worker_quotations (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

worker_id UUID REFERENCES workers(worker_id),

booking_id UUID REFERENCES bookings(id) ON DELETE CASCADE,

total_proposed_price NUMERIC(12,2) NOT NULL,

status VARCHAR(50) DEFAULT 'Pending',

created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE quotation_items (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

quotation_id UUID REFERENCES worker_quotations(id) ON DELETE CASCADE,

service_item_id INT REFERENCES service_items(id) ON DELETE SET NULL,

item_name VARCHAR(255) NOT NULL,

quantity INT DEFAULT 1 CHECK (quantity > 0),

unit_price NUMERIC(12,2) NOT NULL,

total_price NUMERIC(12,2) GENERATED ALWAYS AS (quantity \* unit_price) STORED,

created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

\-- 5. SUPPORT

CREATE TABLE proof_of_works (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

booking_id UUID REFERENCES bookings(id) ON DELETE CASCADE,

image_url TEXT NOT NULL,

proof_type VARCHAR(50),

captured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE cancellation_details (

booking_id UUID PRIMARY KEY REFERENCES bookings(id) ON DELETE CASCADE,

cancelled_by_id UUID REFERENCES users(id),

cancelled_by_role VARCHAR(20),

reason_category VARCHAR(50),

cancellation_reason TEXT,

reputation_penalty_applied NUMERIC(3,1) DEFAULT 0,

cancelled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE booking_histories (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

booking_id UUID REFERENCES bookings(id) ON DELETE CASCADE,

status_update VARCHAR(50),

updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE complaint_warranties (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

booking_id UUID UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,

customer_reason TEXT,

worker_response TEXT,

evidence_image_urls TEXT,

status VARCHAR(50) DEFAULT 'Pending',

deadline_to_respond TIMESTAMP,

created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

\-- 6. WALLET

CREATE TABLE worker_wallets (

worker_id UUID PRIMARY KEY REFERENCES workers(worker_id) ON DELETE CASCADE,

available_balance NUMERIC(12,2) DEFAULT 0,

held_balance NUMERIC(12,2) DEFAULT 0,

debt_balance NUMERIC(12,2) DEFAULT 0

);

CREATE TABLE worker_bank_accounts (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

worker_id UUID REFERENCES workers(worker_id) ON DELETE CASCADE,

bank_name VARCHAR(100),

account_number VARCHAR(50),

account_name VARCHAR(100),

is_default BOOLEAN DEFAULT TRUE

);

CREATE TABLE transaction_histories (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

wallet_id UUID REFERENCES worker_wallets(worker_id) ON DELETE CASCADE,

booking_id UUID REFERENCES bookings(id) ON DELETE SET NULL,

transaction_type VARCHAR(50),

amount NUMERIC(12,2),

transaction_code VARCHAR(50) UNIQUE,

gateway_reference_code VARCHAR(100),

target_bank_account_id UUID REFERENCES worker_bank_accounts(id) ON DELETE SET NULL,

status VARCHAR(50) DEFAULT 'Pending',

admin_note TEXT,

held_release_at TIMESTAMP,

transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

\-- 7. NOTIFICATIONS

CREATE TABLE reviews (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

booking_id UUID UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,

customer_id UUID REFERENCES customers(customer_id),

rating INT CHECK (rating >= 1 AND rating <= 5),

reason_tag VARCHAR(100),

comment TEXT,

created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE notifications (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

user_id UUID REFERENCES users(id) ON DELETE CASCADE,

title VARCHAR(255),

content TEXT,

is_read BOOLEAN DEFAULT FALSE,

created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE user_devices (

id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

user_id UUID REFERENCES users(id) ON DELETE CASCADE,

device_token VARCHAR(255) NOT NULL,

device_os VARCHAR(20),

last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

# API

**DANH SÁCH API TỪNG NHÓM CHỨC NĂNG**

\=== 1. NHÓM XÁC THỰC & TÀI KHOẢN (AUTH & ACCOUNT) ===

POST /auth/register - Đăng ký tài khoản (OTP hoặc Email)

POST /auth/login - Đăng nhập bằng SĐT/Email

POST /auth/login/google - Đăng nhập bằng Google OAuth2

POST /auth/logout - Đăng xuất, revoke refresh token

POST /auth/refresh-token - Làm mới Access Token

POST /auth/otp/send - Gửi mã OTP qua SMS/Email

POST /auth/otp/verify - Xác thực mã OTP

POST /auth/forgot-password - Yêu cầu đặt lại mật khẩu

POST /auth/reset-password - Đặt lại mật khẩu bằng mã 6 số

PATCH /auth/change-password - \[MỚI\] Đổi mật khẩu chủ động khi đang đăng nhập

GET /users/me - Lấy thông tin người dùng hiện tại

PATCH /users/me - Cập nhật thông tin cá nhân (tên, avatar)

DELETE /users/me - Yêu cầu xóa tài khoản (Right to be forgotten)

\=== 2. NHÓM HẠ TẦNG CHUNG (UPLOAD & NOTIFICATION) ===

POST /uploads/presigned-url - \[MỚI\] Lấy URL Cludinary để upload file/ảnh

POST /users/me/device-tokens - \[MỚI\] Lưu FCM Token để nhận Push Notification

DELETE /users/me/device-tokens - \[MỚI\] Xóa FCM Token khi đăng xuất

GET /notifications - \[MỚI\] Lấy danh sách lịch sử thông báo in-app

PATCH /notifications/{id}/read - \[MỚI\] Đánh dấu thông báo đã đọc

\=== 3. NHÓM KHÁCH HÀNG (CUSTOMER) ===

GET /customers/{id}/addresses - Lấy danh sách địa chỉ đã lưu

POST /customers/{id}/addresses - Thêm địa chỉ mới

DELETE /customers/{id}/addresses/{addressId} - Xóa địa chỉ đã lưu

GET /customers/me/favorite-workers - Danh sách thợ quen của khách

POST /customers/me/favorite-workers/{workerId}- Thêm thợ vào danh sách yêu thích

DELETE /customers/me/favorite-workers/{workerId}- Xóa thợ khỏi danh sách yêu thích

GET /customers/me/history - Lịch sử đơn hàng khách hàng

PATCH /customers/{id}/addresses/{addressId} — hiện có thêm và xóa nhưng **không có sửa** địa chỉ. Khách chuyển nhà không thể cập nhật.

PATCH /customers/{id}/addresses/{addressId}/default — đặt địa chỉ mặc định để mở app là tự điền sẵn.

\=== 4. NHÓM THỢ SỬA CHỮA (WORKER) ===

POST /workers/kyc - Thợ nộp hồ sơ CCCD + ảnh chân dung

GET /workers/kyc/status - Thợ kiểm tra trạng thái duyệt hồ sơ

POST /workers/kyc/resubmit — thợ bị Admin **reject hồ sơ** thì không có cách nào nộp lại. Luồng bị tắc hoàn toàn.

POST /workers/skills - Thợ đăng ký danh mục dịch vụ mình biết làm

PATCH /workers/me/profile - \[MỚI\] Cập nhật kinh nghiệm, khu vực hoạt động

PATCH /workers/me/status - Thợ bật/tắt trạng thái sẵn sàng nhận việc

PATCH /workers/me/location - Cập nhật tọa độ GPS của thợ lên Redis

GET /workers/me/stats - Thống kê thu nhập theo ngày/tuần/tháng

GET /workers/me/kpi - Tiến độ KPI và thưởng gamification

GET /workers/me/schedule - Lịch các đơn đã hẹn giờ sắp tới

GET /workers/me/history - Lịch sử công việc của thợ

GET /workers/{id}/profile - Xem hồ sơ công khai của thợ (rating, kỹ năng, khu vực)

GET /workers/{id}/skills - Xem danh mục nghề của một thợ

GET /workers/{id}/reviews - Xem danh sách đánh giá của thợ

\=== 5. NHÓM TÌM KIẾM & DỊCH VỤ (SEARCH & SERVICES) ===

GET /services/categories - Danh sách danh mục dịch vụ (điện, nước,...)

GET /workers/nearby - Tìm thợ online trong bán kính GPS

\=== 6. NHÓM ĐẶT LỊCH & BÁO GIÁ (BOOKING & QUOTATION) ===

POST /bookings - Khách tạo yêu cầu sửa chữa mới

GET /bookings - Lấy danh sách đơn (lọc theo role, trạng thái)

GET /bookings/{id} - Xem chi tiết một đơn hàng

PATCH /bookings/{id}/cancel - Hủy đơn hàng (khách hoặc thợ)

POST /bookings/{id}/accept - Thợ chấp nhận đơn hàng

POST /bookings/{id}/reject - Thợ từ chối đơn hàng kèm lý do

PATCH /bookings/{id}/status - Thợ cập nhật tiến độ (di chuyển, đã đến, đang sửa...)

GET /bookings/{id}/tracking - Lấy tọa độ thời gian thực của thợ (WebSocket fallback)

POST /bookings/{id}/quotations - Thợ gửi thẻ báo giá cho khách

GET /bookings/{id}/quotations - Lấy lịch sử báo giá của đơn

POST /bookings/{id}/quotations/{qId}/approve - Khách đồng ý báo giá

POST /bookings/{id}/quotations/{qId}/reject - Khách từ chối báo giá

POST /bookings/{id}/proof-of-work - Thợ upload ảnh trước/sau sửa chữa

GET /bookings/{id}/proof-of-work - Lấy danh sách ảnh bằng chứng nghiệm thu

POST /bookings/{id}/complete - Khách xác nhận nghiệm thu hoàn thành

POST /bookings/{id}/payment - Ghi nhận phương thức thanh toán (cash/transfer)

GET /bookings/{id}/invoice - Lấy hóa đơn điện tử của đơn

GET /bookings/{id}/warranty-status — client cần biết **còn trong 96h bảo hành không** để quyết định hiển thị/ẩn nút "Gửi khiếu nại". Hiện không có endpoint nào trả thông tin này rõ ràng.

\=== 7. NHÓM TƯƠNG TÁC & HỖ TRỢ (CHAT, REVIEW, SUPPORT) ===

GET /chat/rooms/{bookingId}/messages - Lấy lịch sử tin nhắn của đơn hàng

POST /chat/rooms/{bookingId}/messages - Gửi tin nhắn văn bản hoặc ảnh

POST /bookings/{id}/reviews - Khách gửi đánh giá sao + nhận xét

POST /bookings/{id}/complaints - Khách gửi khiếu nại bảo hành (trong 96h)

GET /bookings/{id}/complaints - Xem chi tiết khiếu nại đang xử lý

POST /bookings/{id}/complaints/respond - Thợ phản hồi khiếu nại bảo hành

GET /support/faq - Lấy danh sách câu hỏi thường gặp (FAQ)

POST /support/tickets - Gửi yêu cầu hỗ trợ lên Admin

GET /support/tickets - Lấy danh sách ticket của người dùng

\=== 8. NHÓM VÍ & TÀI CHÍNH (WALLET) ===

GET /workers/me/wallet - Xem tổng quan ví (khả dụng, đang giữ, nợ)

GET /workers/me/wallet/transactions - Lịch sử giao dịch ví

POST /workers/me/wallet/deposit - Tạo lệnh nạp tiền (trả nợ chiết khấu)

GET /workers/me/wallet/deposit/{txId}/qr - Lấy mã QR VietQR để nạp tiền

POST /workers/me/wallet/withdraw - Tạo lệnh rút tiền về ngân hàng

GET /workers/me/bank-accounts - Danh sách tài khoản ngân hàng đã liên kết

POST /workers/me/bank-accounts - Thêm tài khoản ngân hàng

DELETE /workers/me/bank-accounts/{id} - Xóa tài khoản ngân hàng

POST /webhooks/sepay - Webhook SePay nhận xác nhận giao dịch (internal)

\=== 9. NHÓM QUẢN TRỊ VIÊN (ADMIN DASHBOARD) ===

GET /admin/users - Danh sách toàn bộ người dùng (có filter)

GET /admin/users/{id} - Chi tiết người dùng

PATCH /admin/users/{id}/block - Khóa tài khoản người dùng

PATCH /admin/users/{id}/unblock - Mở khóa tài khoản

GET /admin/kyc/pending - Lấy danh sách hồ sơ eKYC chờ duyệt

GET /admin/kyc/{workerId} - Xem chi tiết hồ sơ eKYC của một thợ

POST /admin/kyc/{workerId}/approve - Admin duyệt hồ sơ thợ

POST /admin/kyc/{workerId}/reject - Admin từ chối eKYC kèm lý do

GET /admin/bookings - Danh sách toàn bộ đơn hàng hệ thống

GET /admin/complaints - Danh sách khiếu nại cần xử lý

POST /admin/complaints/{id}/resolve - Admin phán quyết khiếu nại

GET /admin/support/tickets - Danh sách ticket hỗ trợ cần xử lý

PATCH /admin/support/tickets/{id}/resolve - Đánh dấu ticket đã xử lý

GET /admin/transactions - Danh sách giao dịch nạp/rút chờ duyệt

POST /admin/transactions/{id}/confirm-withdraw- Admin xác nhận lệnh rút tiền

GET /admin/analytics/revenue - Thống kê doanh thu theo kỳ

GET /admin/analytics/bookings - Tỷ lệ hoàn thành/hủy đơn

GET /admin/analytics/top-services - Dịch vụ được đặt nhiều nhất

POST /admin/config/discount-rate - Cấu hình tỷ lệ chiết khấu nền tảng

POST /admin/config/kpi-milestones - Cài đặt mốc KPI thưởng cho thợ

GET /admin/notifications - Quản lý thông báo broadcast

POST /admin/notifications - \[MỚI\] Gửi thông báo broadcast toàn hệ thống

nại

# Thẻ 15