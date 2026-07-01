# **Tài liệu phân chia API và mapping màn hình Android --- FixIt VN**

## **1. Mục tiêu tài liệu**

Tài liệu này dùng để chốt phạm vi triển khai API backend Spring Boot và
cách nối sang Android Java của dự án FixIt VN.

Tài liệu giải quyết 4 việc:

1.  Chốt lại danh sách API sau khi đã sửa lỗi thiết kế.

2.  Chia API theo từng đợt triển khai để làm MVP trước, mở rộng sau.

3.  Phân công người làm chính theo tải công việc giảm dần: Phú → Thế Anh
    > → Tiến → Hiếu.

4.  Mô tả mỗi nhóm API sẽ nối với màn hình Android/package Android nào.

## **2. Quy ước API chung**

### **2.1. Base path**

Toàn bộ REST API dùng chung prefix:

/api/v1

Không để lẫn kiểu:

/workers/\...

/api/v1/workers/\...

/auth/\...

Lý do: Android Retrofit cần route ổn định. Nếu API bị lẫn prefix, khi
thay mock bằng API thật rất dễ sai endpoint.

### **2.2. Auth header**

Các API cần đăng nhập dùng:

Authorization: Bearer \<access_token\>

Các API public không cần token:

POST /api/v1/auth/login

POST /api/v1/auth/register

POST /api/v1/auth/refresh-token

GET /api/v1/services/categories

GET /api/v1/workers/{workerId}/profile

Webhook không dùng JWT người dùng. Webhook phải xác thực bằng
secret/signature riêng.

### **2.3. Response format đề xuất**

{

\"success\": true,

\"message\": \"OK\",

\"data\": {}

}

Khi lỗi:

{

\"success\": false,

\"message\": \"Validation failed\",

\"errorCode\": \"VALIDATION_ERROR\",

\"errors\": \[

{

\"field\": \"phoneNumber\",

\"message\": \"Số điện thoại không hợp lệ\"

}

\]

}

### **2.4. Quy tắc dùng /me**

Các API thao tác với chính user đang đăng nhập phải dùng /me, không dùng
{id}.

Ví dụ đúng:

GET /api/v1/customers/me/addresses

PATCH /api/v1/workers/me/profile

GET /api/v1/users/me

Ví dụ không nên dùng:

GET /api/v1/customers/{id}/addresses

PATCH /api/v1/users/{id}

Lý do: backend lấy user từ JWT để tránh client truyền sai hoặc cố tình
truyền ID người khác.

## **3. Kiến trúc Android cần tuân thủ**

Android đang đi theo Clean Architecture theo feature:

feature/\<feature\>/

├── presentation/ \# Activity, Fragment, ViewModel, UI state/event

├── domain/

│ ├── model/ \# Model nghiệp vụ dùng trong app

│ ├── repository/ \# Interface repository

│ └── usecase/ \# Business use cases

├── data/

│ ├── remote/api/ \# Retrofit API interfaces

│ ├── remote/dto/ \# DTO request/response

│ ├── remote/mapper/ \# Mapper DTO \<-\> domain

│ └── repository/ \# Repository implementation

└── di/ \# Hilt module bind/provide

Luồng chuẩn:

Fragment

-\> ViewModel

-\> UseCase

-\> Domain Repository Interface

-\> Data Repository Implementation

-\> Retrofit API

-\> Spring Boot REST API

Không để Fragment gọi Retrofit trực tiếp.\
Không để ViewModel biết DTO.\
Không đặt API của từng feature vào core.network.\
core.network chỉ chứa cấu hình chung như Retrofit, OkHttp, interceptor.

### **3.1. Các màn hình worker đã có hoặc đã định danh**

Các layout/màn worker chính:

fragment_worker_home.xml

fragment_worker_orders.xml

fragment_worker_order_detail.xml

fragment_worker_wallet.xml

fragment_worker_chat.xml hoặc fragment_chat_worker.xml tùy nhánh hiện
tại

Các dialog/bottom sheet nghiệp vụ:

dialog_incoming_order.xml

bottom_sheet_cancel_reason.xml

dialog_add_fee.xml

Các item/layout tái sử dụng:

layout_worker_topbar.xml

layout_empty_state.xml

item_worker_order_card.xml

item_wallet_transaction.xml

layout_worker_home_header.xml

layout_worker_home_toggle.xml

layout_worker_home_active_order.xml

### **3.2. Package Android quan trọng khi nối API**

com.fixit.feature.auth.presentation

com.fixit.feature.auth.data

com.fixit.feature.worker.presentation

com.fixit.feature.worker.availability

com.fixit.feature.worker.home

com.fixit.feature.worker.job

com.fixit.feature.worker.orders

com.fixit.feature.worker.wallet

com.fixit.feature.worker.kyc

com.fixit.feature.worker.chat

com.fixit.feature.worker.profile

com.fixit.feature.worker.stats

com.fixit.feature.customer

com.fixit.feature.notification

com.fixit.core.network

com.fixit.core.storage

com.fixit.core.common

## **4. Phân công tổng quan**

### **4.1. Mức tải công việc**

1\. Phú

2\. Thế Anh

3\. Tiến

4\. Hiếu

### **4.3. Phạm vi theo người**

Phú

Phú phụ trách chính các flow worker và nghiệp vụ::

Worker profile/status/location

Worker KYC

Worker home/schedule/history/stats/kpi

Booking assignment accept/reject/miss

Booking status lifecycle

Tracking

Proof of work

Wallet

Bank account

Deposit/withdraw

SePay/payment webhook

Admin KYC

Admin transactions

Thế Anh

Thế Anh phụ trách flow khách hàng từ tìm thợ đến đặt đơn, báo giá, thanh
toán:

Service categories/items

Search nearby workers

Customer profile/address

Favorite workers

Booking create/list/detail/cancel

Quotation

Payment request/status

Admin analytics

Tiến

Tiến phụ trách tương tác, hậu mãi, tranh chấp, hỗ trợ:

Chat

Review

Complaint/warranty claim

Warranty status

Support FAQ

Support ticket

Invoice display

Admin complaint

Admin support ticket

Hiếu

Hiếu phụ trách nền tảng tài khoản và admin cấu hình:

Auth

OTP

Forgot/reset password

Change password

Users/me

Upload

Device token

Notification

Admin user

Admin config

Admin broadcast notification

# **5. Đợt 1 --- Nền tảng tài khoản, upload, notification, service**

## **5.1. Mục tiêu**

Đợt 1 dùng để làm nền móng. Sau đợt này, app phải đăng ký/đăng nhập
được, lưu token được, lấy profile hiện tại được, upload ảnh được,
nhận/lưu device token được và lấy danh mục dịch vụ cơ bản được.

## **5.2. Người phụ trách chính**

Hiếu: Auth, Account, Upload, Notification

Thế Anh: Service categories/items

Phú: chuẩn bị Worker home/status sau khi Auth xong

Tiến: chưa cần code nhiều, chuẩn bị contract cho chat/support nếu thiếu
DB

## **5.3. Auth & Account --- Hiếu**

API

POST /api/v1/auth/register

POST /api/v1/auth/login

POST /api/v1/auth/login/google

POST /api/v1/auth/logout

POST /api/v1/auth/refresh-token

POST /api/v1/auth/otp/send

POST /api/v1/auth/otp/verify

POST /api/v1/auth/forgot-password

POST /api/v1/auth/reset-password

PATCH /api/v1/auth/change-password

GET /api/v1/users/me

PATCH /api/v1/users/me

//DELETE /api/v1/users/me

Android mapping

Package UI/ViewModel:

com.fixit.feature.auth.presentation

Package data/API:

com.fixit.feature.auth.data

Retrofit API cần có:

feature/auth/data/remote/api/AuthApi.java

Màn hình Android liên quan

AuthActivity

LoginFragment

RegisterFragment

ForgotPasswordFragment

ChangePassword screen hoặc Profile change password screen

Ghi chú triển khai Android

AuthViewModel chỉ gọi use case:

LoginUseCase

RegisterUseCase

LogoutUseCase

ForgotPasswordUseCase

ResetPasswordUseCase

ChangePasswordUseCase

GetCurrentUserUseCase

UpdateCurrentUserUseCase

Không để LoginFragment gọi trực tiếp AuthApi.

## **5.4. Upload --- Hiếu**

API

POST /api/v1/uploads/presigned-url

POST /api/v1/uploads/confirm

Android mapping

Package đề xuất:

com.fixit.feature.upload

hoặc dùng chung trong core nếu chỉ là hạ tầng upload

API đề xuất:

core/upload/data/remote/api/UploadApi.java

Màn hình Android liên quan

Register/KYC upload ảnh CCCD

Worker profile update avatar

Customer profile update avatar

Proof of work upload ảnh trước/sau sửa chữa

Chat gửi ảnh

Complaint gửi ảnh bằng chứng

Ghi chú

API này không phải màn hình riêng. Nó là API hạ tầng được nhiều feature
gọi lại.

## **5.5. Notification & Device Token --- Hiếu**

API

POST /api/v1/users/me/device-tokens

DELETE /api/v1/users/me/device-tokens/{deviceToken}

GET /api/v1/notifications

GET /api/v1/notifications/unread-count

PATCH /api/v1/notifications/{notificationId}/read

PATCH /api/v1/notifications/read-all

Android mapping

Package:

com.fixit.feature.notification

com.fixit.core.fcm

Màn hình Android liên quan

Notification screen cần tạo nếu chưa có

Worker topbar notification icon

Customer topbar notification icon

Ghi chú

device-token được gọi sau login và khi FCM token refresh.\
DELETE device-token gọi khi logout.

## **5.6. Services --- Thế Anh**

API

GET /api/v1/services/categories

GET /api/v1/services/categories/{categoryId}

GET /api/v1/services/categories/{categoryId}/items

Android mapping

Package đề xuất:

com.fixit.feature.customer.service

com.fixit.feature.customer.search

API đề xuất:

feature/customer/service/data/remote/api/ServiceApi.java

Màn hình Android liên quan

CustomerHomeFragment hoặc màn Trang chủ khách hàng cần tạo/hoàn thiện

ServiceCategoryFragment hoặc bottom sheet chọn dịch vụ

CustomerBookingCreateFragment

Ghi chú

Danh mục dịch vụ là đầu vào cho tìm thợ và tạo booking.

# **6. Đợt 2 --- Customer, Worker profile, Search, KYC**

## **6.1. Mục tiêu**

Sau đợt 2:

Khách hàng quản lý được địa chỉ.

Khách hàng tìm được thợ quanh mình.

Khách hàng lưu được thợ yêu thích.

Thợ cập nhật được hồ sơ.

Thợ bật/tắt trạng thái nhận việc.

Thợ cập nhật GPS.

Thợ nộp và nộp lại hồ sơ KYC.

## **6.2. Customer Profile & Address --- Thế Anh**

API

GET /api/v1/customers/me/profile

PATCH /api/v1/customers/me/profile

GET /api/v1/customers/me/addresses

POST /api/v1/customers/me/addresses

PATCH /api/v1/customers/me/addresses/{addressId}

DELETE /api/v1/customers/me/addresses/{addressId}

PATCH /api/v1/customers/me/addresses/{addressId}/default

Android mapping

Package đề xuất:

com.fixit.feature.customer.profile

com.fixit.feature.customer.address

API đề xuất:

feature/customer/profile/data/remote/api/CustomerProfileApi.java

feature/customer/address/data/remote/api/CustomerAddressApi.java

Màn hình Android liên quan

CustomerProfileFragment cần tạo/hoàn thiện

CustomerAddressListFragment cần tạo

CustomerAddressEditFragment cần tạo

CustomerBookingCreateFragment dùng địa chỉ mặc định

Ghi chú nghiệp vụ

Không dùng /customers/{id}/addresses nữa. Backend lấy customer từ JWT.

## **6.3. Favorite Workers --- Thế Anh**

API

GET /api/v1/customers/me/favorite-workers

POST /api/v1/customers/me/favorite-workers/{workerId}

DELETE /api/v1/customers/me/favorite-workers/{workerId}

Android mapping

Package đề xuất:

com.fixit.feature.customer.favorite

Màn hình Android liên quan

CustomerFavoriteWorkersFragment cần tạo

WorkerPublicProfileFragment có nút thêm/xóa thợ yêu thích

CustomerBookingCreateFragment ưu tiên thợ yêu thích khi đặt đơn

CustomerHistoryDetailFragment có nút thêm thợ vào yêu thích sau khi hoàn
thành đơn

## **6.4. Worker Profile, Skill, Availability --- Phú**

API

GET /api/v1/workers/me/home

GET /api/v1/workers/me/profile

PATCH /api/v1/workers/me/profile

GET /api/v1/workers/me/skills

PUT /api/v1/workers/me/skills

PATCH /api/v1/workers/me/status

PATCH /api/v1/workers/me/location

GET /api/v1/workers/me/schedule

GET /api/v1/workers/me/history

GET /api/v1/workers/me/stats

GET /api/v1/workers/me/kpi

Android mapping

Package hiện có:

com.fixit.feature.worker.home

com.fixit.feature.worker.availability

com.fixit.feature.worker.profile

com.fixit.feature.worker.stats

com.fixit.feature.worker.job

API cần tạo:

feature/worker/home/data/remote/api/WorkerHomeApi.java

feature/worker/availability/data/remote/api/WorkerAvailabilityApi.java

feature/worker/profile/data/remote/api/WorkerProfileApi.java

feature/worker/stats/data/remote/api/WorkerStatsApi.java

Màn hình Android liên quan

fragment_worker_home.xml

layout_worker_home_header.xml

layout_worker_home_toggle.xml

layout_worker_home_active_order.xml

WorkerProfile screen cần hoàn thiện

WorkerStats screen hoặc fragment_worker_home phần biểu đồ

Mapping chi tiết

GET /workers/me/home

-\> fragment_worker_home.xml

-\> WorkerHomeFragment

-\> WorkerHomeViewModel

-\> GetWorkerHomeUseCase

PATCH /workers/me/status

-\> layout_worker_home_toggle.xml

-\> WorkerStatusViewModel

-\> ToggleWorkerAvailabilityUseCase

PATCH /workers/me/location

-\> LocationTrackingService

-\> WorkerAvailabilityRepository hoặc WorkerLocationRepository

GET /workers/me/schedule

-\> fragment_worker_home.xml phần lịch hẹn hôm nay

-\> fragment_worker_orders.xml tab lịch hẹn nếu có

GET /workers/me/stats, /kpi

-\> fragment_worker_home.xml phần thống kê

-\> WorkerStatsFragment nếu tách riêng

## **6.5. Public Worker Profile --- Thế Anh**

API

GET /api/v1/workers/{workerId}/profile

GET /api/v1/workers/{workerId}/skills

GET /api/v1/workers/{workerId}/reviews

Android mapping

Package đề xuất:

com.fixit.feature.customer.workerprofile

Màn hình Android liên quan

WorkerPublicProfileFragment cần tạo

SearchNearbyWorkersFragment mở sang màn hồ sơ thợ

CustomerHistoryDetailFragment mở lại hồ sơ thợ đã từng làm

FavoriteWorkersFragment mở hồ sơ thợ yêu thích

## **6.6. Worker KYC --- Phú**

API

POST /api/v1/workers/me/kyc

GET /api/v1/workers/me/kyc/status

POST /api/v1/workers/me/kyc/resubmit

Android mapping

Package hiện có/skeleton:

com.fixit.feature.worker.kyc

API cần tạo:

feature/worker/kyc/data/remote/api/WorkerKycApi.java

Màn hình Android liên quan

WorkerKycFragment cần hoàn thiện

WorkerProfileFragment hiển thị trạng thái KYC

fragment_worker_home.xml có thể hiển thị banner nếu KYC chưa duyệt

Ghi chú nghiệp vụ

Nếu KYC bị reject, app phải hiện lý do từ chối và cho nộp lại qua
/resubmit.

## **6.7. Search Nearby Workers --- Thế Anh**

API

GET
/api/v1/workers/nearby?serviceId={id}&lat={lat}&lng={lng}&radiusKm={radiusKm}&sort=distance

Android mapping

Package đề xuất:

com.fixit.feature.customer.search

API cần tạo:

feature/customer/search/data/remote/api/WorkerSearchApi.java

Màn hình Android liên quan

CustomerHomeFragment

SearchNearbyWorkersFragment

Map/Search result screen cần tạo

CustomerBookingCreateFragment nhận worker/service được chọn

Response rút gọn đề xuất

{

\"workerId\": \"uuid\",

\"displayName\": \"Nguyễn Văn A\",

\"avatarUrl\": \"https://\...\",

\"averageRating\": 4.8,

\"totalReviews\": 120,

\"distanceKm\": 1.4,

\"available\": true,

\"serviceNames\": \[\"Sửa điện\", \"Sửa nước\"\]

}

# **7. Đợt 3 --- Booking core, điều phối thợ, báo giá, tiến độ**

## **7.1. Mục tiêu**

Sau đợt 3, hệ thống phải chạy được flow chính:

Khách tạo đơn

-\> hệ thống phát đơn cho thợ

-\> thợ nhận/từ chối/bỏ lỡ

-\> thợ di chuyển

-\> thợ đến nơi

-\> khảo sát

-\> báo giá

-\> khách duyệt giá

-\> thợ sửa

-\> upload ảnh trước/sau

-\> thợ báo hoàn thành

-\> khách nghiệm thu

## **7.2. Booking CRUD & List --- Thế Anh**

API

POST /api/v1/bookings

GET
/api/v1/bookings?role=CUSTOMER\|WORKER&status={status}&page={page}&size={size}

GET /api/v1/bookings/{bookingId}

POST /api/v1/bookings/{bookingId}/cancel

Android mapping

Customer side package đề xuất:

com.fixit.feature.customer.booking

Worker side package hiện có:

com.fixit.feature.worker.orders

API cần tạo:

feature/customer/booking/data/remote/api/CustomerBookingApi.java

feature/worker/orders/data/remote/api/WorkerOrdersApi.java

Màn hình Android liên quan

CustomerBookingCreateFragment cần tạo

CustomerBookingListFragment cần tạo

CustomerBookingDetailFragment cần tạo

fragment_worker_orders.xml

fragment_worker_order_detail.xml

item_worker_order_card.xml

bottom_sheet_cancel_reason.xml

Mapping chi tiết

POST /bookings

-\> CustomerBookingCreateFragment

GET /bookings?role=CUSTOMER

-\> CustomerBookingListFragment

-\> CustomerHistoryFragment

GET /bookings?role=WORKER

-\> fragment_worker_orders.xml

-\> WorkerOrdersViewModel

GET /bookings/{bookingId}

-\> CustomerBookingDetailFragment

-\> fragment_worker_order_detail.xml

POST /bookings/{bookingId}/cancel

-\> CustomerBookingDetailFragment nút Hủy đơn

-\> fragment_worker_order_detail.xml menu Hủy đơn

-\> bottom_sheet_cancel_reason.xml

## **7.3. Booking Assignment --- Phú**

API

GET /api/v1/workers/me/assignments/pending

POST /api/v1/bookings/{bookingId}/assignments/{assignmentId}/accept

POST /api/v1/bookings/{bookingId}/assignments/{assignmentId}/reject

POST /api/v1/bookings/{bookingId}/assignments/{assignmentId}/miss

Android mapping

Package hiện có:

com.fixit.feature.worker.orders

com.fixit.feature.worker.job

API cần tạo:

feature/worker/orders/data/remote/api/WorkerAssignmentsApi.java

Màn hình Android liên quan

dialog_incoming_order.xml

fragment_worker_home.xml

fragment_worker_orders.xml

fragment_worker_order_detail.xml

Mapping chi tiết

GET /workers/me/assignments/pending

-\> WorkerHomeFragment kiểm tra đơn đang chờ

-\> dialog_incoming_order.xml

POST /accept

-\> dialog_incoming_order.xml nút Chấp nhận

-\> mở fragment_worker_order_detail.xml

POST /reject

-\> dialog_incoming_order.xml nút Từ chối

-\> chọn lý do nếu cần

POST /miss

-\> backend gọi tự động hoặc Android fallback khi hết countdown

Ghi chú nghiệp vụ

Không dùng endpoint chung /bookings/{id}/accept vì một booking có thể
được phát cho nhiều thợ theo từng assignment. Accept/reject phải gắn với
assignment cụ thể.

## **7.4. Booking Status Action --- Phú**

API

POST /api/v1/bookings/{bookingId}/start-moving

POST /api/v1/bookings/{bookingId}/arrive

POST /api/v1/bookings/{bookingId}/start-survey

POST /api/v1/bookings/{bookingId}/start-repair

POST /api/v1/bookings/{bookingId}/worker-complete

POST /api/v1/bookings/{bookingId}/customer-complete

Android mapping

Package hiện có:

com.fixit.feature.worker.orders

com.fixit.feature.worker.job

Package customer đề xuất:

com.fixit.feature.customer.booking

Màn hình Android liên quan

fragment_worker_order_detail.xml

CustomerBookingDetailFragment cần tạo

CustomerTrackingFragment cần tạo

Mapping chi tiết

POST /start-moving

-\> fragment_worker_order_detail.xml nút Bắt đầu di chuyển

-\> CustomerTrackingFragment hiển thị thợ đang đến

POST /arrive

-\> fragment_worker_order_detail.xml nút Đã đến nơi

POST /start-survey

-\> fragment_worker_order_detail.xml nút Bắt đầu khảo sát

POST /start-repair

-\> fragment_worker_order_detail.xml nút Bắt đầu sửa chữa

POST /worker-complete

-\> fragment_worker_order_detail.xml nút Hoàn thành công việc

-\> CustomerBookingDetailFragment hiện nút Nghiệm thu

POST /customer-complete

-\> CustomerBookingDetailFragment nút Hoàn thành nghiệm thu

-\> kích hoạt payment/warranty

## **7.5. Tracking --- Phú**

API

PATCH /api/v1/bookings/{bookingId}/tracking/location

GET /api/v1/bookings/{bookingId}/tracking

Android mapping

Worker:

LocationTrackingService

fragment_worker_order_detail.xml

Customer:

CustomerTrackingFragment cần tạo

Ghi chú

API REST tracking là fallback. Sau này realtime nên dùng WebSocket hoặc
SocketManager.

## **7.6. Quotation --- Thế Anh**

API

POST /api/v1/bookings/{bookingId}/quotations

GET /api/v1/bookings/{bookingId}/quotations

GET /api/v1/bookings/{bookingId}/quotations/{quotationId}

POST /api/v1/bookings/{bookingId}/quotations/{quotationId}/approve

POST /api/v1/bookings/{bookingId}/quotations/{quotationId}/reject

Android mapping

Worker package:

com.fixit.feature.worker.orders

Customer package đề xuất:

com.fixit.feature.customer.booking

Màn hình Android liên quan

fragment_worker_order_detail.xml

Chat screen nếu báo giá hiển thị trong chat

CustomerBookingDetailFragment

QuotationBottomSheet cần tạo nếu chưa có

Mapping chi tiết

POST /quotations

-\> fragment_worker_order_detail.xml

-\> dialog_add_fee.xml nếu thêm vật tư/phụ phí

GET /quotations

-\> CustomerBookingDetailFragment

-\> fragment_worker_order_detail.xml

POST /approve

-\> CustomerBookingDetailFragment nút Đồng ý báo giá

POST /reject

-\> CustomerBookingDetailFragment nút Từ chối báo giá

## **7.7. Proof of Work --- Phú**

API

POST /api/v1/bookings/{bookingId}/proof-of-work

GET /api/v1/bookings/{bookingId}/proof-of-work

DELETE /api/v1/bookings/{bookingId}/proof-of-work/{proofId}

Android mapping

Package hiện có:

com.fixit.feature.worker.orders

API:

feature/worker/orders/data/remote/api/WorkerProofOfWorkApi.java

Màn hình Android liên quan

fragment_worker_order_detail.xml

Mapping chi tiết

POST /proof-of-work

-\> nút chụp ảnh Trước/Sau trong fragment_worker_order_detail.xml

GET /proof-of-work

-\> màn chi tiết đơn của worker/customer

-\> complaint screen dùng lại bằng chứng

DELETE /proof-of-work/{proofId}

-\> chỉ cho phép trước khi worker-complete

# **8. Đợt 4 --- Payment, Wallet, Warranty, Invoice**

## **8.1. Mục tiêu**

Sau đợt 4:

Khách thanh toán được.

Hệ thống ghi nhận payment status.

Thợ xem được ví.

Thợ nạp tiền trả nợ.

Thợ rút tiền.

Có trạng thái bảo hành 96 giờ.

Có hóa đơn/biên lai để xem lại.

## 

## **8.2. Payment & Invoice --- Thế Anh phối hợp Tiến**

API

POST /api/v1/bookings/{bookingId}/payments

GET /api/v1/bookings/{bookingId}/payment-status

GET /api/v1/bookings/{bookingId}/invoice

GET /api/v1/customers/me/invoices

GET /api/v1/workers/me/invoices

Android mapping

Customer package đề xuất:

com.fixit.feature.customer.payment

com.fixit.feature.customer.invoice

Worker package:

com.fixit.feature.worker.orders

com.fixit.feature.worker.wallet

Màn hình Android liên quan

CustomerPaymentFragment cần tạo

CustomerInvoiceFragment cần tạo

CustomerHistoryDetailFragment cần tạo

fragment_worker_order_detail.xml

fragment_worker_wallet.xml

Mapping chi tiết

POST /payments

-\> CustomerPaymentFragment chọn tiền mặt/chuyển khoản

GET /payment-status

-\> CustomerPaymentFragment polling trạng thái

-\> fragment_worker_order_detail.xml hiển thị đã thanh toán/chưa thanh
toán

GET /invoice

-\> CustomerInvoiceFragment

-\> Worker order detail nếu thợ cần xem lại hóa đơn

## **8.3. Warranty Status --- Tiến**

API

GET /api/v1/bookings/{bookingId}/warranty-status

Android mapping

Package đề xuất:

com.fixit.feature.customer.warranty

com.fixit.feature.customer.complaint

com.fixit.feature.worker.orders

Màn hình Android liên quan

CustomerBookingDetailFragment

CustomerHistoryDetailFragment

CustomerComplaintCreateFragment

fragment_worker_order_detail.xml

fragment_worker_wallet.xml nếu liên quan tiền đang giữ

Response đề xuất

{

\"bookingId\": \"uuid\",

\"warrantyActive\": true,

\"warrantyStartedAt\": \"2026-05-18T10:00:00+07:00\",

\"warrantyExpiresAt\": \"2026-05-22T10:00:00+07:00\",

\"remainingSeconds\": 345600,

\"complaintAllowed\": true,

\"complaintId\": null,

\"complaintStatus\": null

}

Mapping chi tiết

GET /warranty-status

-\> CustomerHistoryDetailFragment quyết định hiện/ẩn nút Gửi khiếu nại

-\> CustomerComplaintCreateFragment chặn nếu quá 96 giờ

-\> fragment_worker_wallet.xml hiển thị tiền đang giữ nếu còn bảo hành

## **8.4. Worker Wallet --- Phú**

API

GET /api/v1/workers/me/wallet

GET
/api/v1/workers/me/wallet/transactions?page={page}&size={size}&type={type}

POST /api/v1/workers/me/wallet/deposits

GET /api/v1/workers/me/wallet/deposits/{transactionId}

GET /api/v1/workers/me/wallet/deposits/{transactionId}/qr

POST /api/v1/workers/me/wallet/withdrawals

GET /api/v1/workers/me/wallet/withdrawals/{transactionId}

POST /api/v1/workers/me/wallet/withdrawals/{transactionId}/cancel

Android mapping

Package hiện có:

com.fixit.feature.worker.wallet

API cần tạo:

feature/worker/wallet/data/remote/api/WorkerWalletApi.java

Màn hình Android liên quan

fragment_worker_wallet.xml

item_wallet_transaction.xml

Deposit screen/bottom sheet cần tạo

Withdraw screen/bottom sheet cần tạo

QR payment screen cần tạo

Mapping chi tiết

GET /wallet

-\> fragment_worker_wallet.xml thẻ số dư

GET /wallet/transactions

-\> RecyclerView item_wallet_transaction.xml

POST /wallet/deposits

-\> màn Nạp tiền trả nợ

GET /wallet/deposits/{id}/qr

-\> màn QR VietQR

POST /wallet/withdrawals

-\> màn Rút tiền

GET /wallet/withdrawals/{id}

-\> màn trạng thái lệnh rút

## **8.5. Worker Bank Account --- Phú**

API

GET /api/v1/workers/me/bank-accounts

POST /api/v1/workers/me/bank-accounts

PATCH /api/v1/workers/me/bank-accounts/{bankAccountId}

DELETE /api/v1/workers/me/bank-accounts/{bankAccountId}

PATCH /api/v1/workers/me/bank-accounts/{bankAccountId}/default

Android mapping

Package hiện có/đề xuất:

com.fixit.feature.worker.wallet

Màn hình Android liên quan

fragment_worker_wallet.xml

WorkerBankAccountListFragment cần tạo

WorkerBankAccountEditFragment cần tạo

Withdraw screen chọn tài khoản nhận tiền

## **8.6. Payment Webhook --- Phú**

API

POST /api/v1/webhooks/sepay

POST /api/v1/webhooks/payment-gateway

Android mapping

Không nối trực tiếp Android. Đây là API backend nhận callback từ cổng
thanh toán.

Màn hình Android bị ảnh hưởng gián tiếp

CustomerPaymentFragment

fragment_worker_wallet.xml

QR payment screen

Transaction detail screen

# **9. Đợt 5 --- Chat, Review, Complaint, Support**

## **9.1. Mục tiêu**

Sau đợt 5:

Khách và thợ chat được theo booking.

Khách đánh giá được thợ.

Khách gửi khiếu nại trong 96 giờ.

Thợ phản hồi khiếu nại.

Người dùng gửi ticket hỗ trợ.

Admin xử lý ticket/khiếu nại.

## **9.2. Chat --- Tiến**

API

GET /api/v1/chat/rooms/{bookingId}

GET /api/v1/chat/rooms/{bookingId}/messages?page={page}&size={size}

POST /api/v1/chat/rooms/{bookingId}/messages

PATCH /api/v1/chat/rooms/{bookingId}/messages/{messageId}/read

Android mapping

Package hiện có/skeleton:

com.fixit.feature.worker.chat

Package customer đề xuất:

com.fixit.feature.customer.chat

Core realtime:

com.fixit.core.socket

Màn hình Android liên quan

fragment_worker_chat.xml hoặc fragment_chat_worker.xml

CustomerChatFragment cần tạo

fragment_worker_order_detail.xml nút Chat

CustomerBookingDetailFragment nút Chat

Ghi chú

Nếu hiện tại nhánh code đã đổi fragment_worker_chat.xml sang
fragment_chat_worker.xml, phải dùng đúng binding mới để tránh lỗi
ViewBinding.

## **9.3. Review --- Tiến**

API

POST /api/v1/bookings/{bookingId}/reviews

GET /api/v1/bookings/{bookingId}/reviews

GET /api/v1/workers/{workerId}/reviews

Android mapping

Package đề xuất:

com.fixit.feature.customer.review

com.fixit.feature.customer.workerprofile

Màn hình Android liên quan

CustomerReviewDialog hoặc CustomerReviewFragment cần tạo

CustomerHistoryDetailFragment

WorkerPublicProfileFragment

Mapping chi tiết

POST /reviews

-\> popup đánh giá sau khi customer-complete

GET /workers/{workerId}/reviews

-\> WorkerPublicProfileFragment

## **9.4. Complaint / Warranty Claim --- Tiến**

API

POST /api/v1/bookings/{bookingId}/complaints

GET /api/v1/bookings/{bookingId}/complaints

POST /api/v1/bookings/{bookingId}/complaints/{complaintId}/respond

POST /api/v1/bookings/{bookingId}/complaints/{complaintId}/cancel

Android mapping

Customer package đề xuất:

com.fixit.feature.customer.complaint

Worker package:

com.fixit.feature.worker.orders

com.fixit.feature.worker.chat hoặc complaint package riêng nếu tách

Màn hình Android liên quan

CustomerComplaintCreateFragment cần tạo

CustomerComplaintDetailFragment cần tạo

WorkerComplaintDetailFragment cần tạo

fragment_worker_order_detail.xml hiển thị trạng thái tranh chấp

fragment_worker_wallet.xml hiển thị tiền đang bị giữ nếu có complaint

Mapping chi tiết

POST /complaints

-\> CustomerComplaintCreateFragment

GET /complaints

-\> CustomerComplaintDetailFragment

-\> WorkerComplaintDetailFragment

POST /respond

-\> WorkerComplaintDetailFragment

## **9.5. Support / FAQ --- Tiến**

API

GET /api/v1/support/faq

GET /api/v1/support/faq/categories

POST /api/v1/support/tickets

GET /api/v1/support/tickets

GET /api/v1/support/tickets/{ticketId}

POST /api/v1/support/tickets/{ticketId}/messages

PATCH /api/v1/support/tickets/{ticketId}/close

Android mapping

Package đề xuất:

com.fixit.feature.support

hoặc tách:

com.fixit.feature.customer.support

com.fixit.feature.worker.support

Màn hình Android liên quan

SupportHomeFragment cần tạo

FaqFragment cần tạo

SupportTicketListFragment cần tạo

SupportTicketDetailFragment cần tạo

# **10. Đợt 6 --- Admin Dashboard - Đợt sau**

## **10.1. Mục tiêu**

Admin có thể quản trị user, KYC, booking, complaint, support,
transaction, config và analytics.

Admin Dashboard có thể làm web riêng. Nếu Android hiện chưa có admin app
thì các endpoint này không nối vào Android app khách/thợ, nhưng vẫn cần
backend để vận hành.

## **10.2. Admin User --- Hiếu**

### **API**

GET /api/v1/admin/users?role={role}&status={status}&q={keyword}

GET /api/v1/admin/users/{userId}

PATCH /api/v1/admin/users/{userId}/block

PATCH /api/v1/admin/users/{userId}/unblock

### **Android mapping**

Không nối vào Android app khách/thợ nếu chưa có role Admin trên mobile.

Nếu làm admin mobile/webview:

AdminUserListScreen

AdminUserDetailScreen

## **10.3. Admin KYC --- Phú**

### **API**

GET /api/v1/admin/kyc/pending

GET /api/v1/admin/kyc/{workerId}

POST /api/v1/admin/kyc/{workerId}/approve

POST /api/v1/admin/kyc/{workerId}/reject

### **Android mapping**

Không nối vào app khách/thợ.

Nối vào AdminKycPendingScreen nếu có admin dashboard.

## **10.4. Admin Booking & Complaint --- Tiến**

### **API**

GET /api/v1/admin/bookings?status={status}&from={date}&to={date}

GET /api/v1/admin/bookings/{bookingId}

GET /api/v1/admin/complaints?status={status}

GET /api/v1/admin/complaints/{complaintId}

POST /api/v1/admin/complaints/{complaintId}/resolve

### **Android mapping**

Không nối vào app khách/thợ.

Nối vào AdminBookingScreen/AdminComplaintScreen nếu làm dashboard.

## **10.5. Admin Support --- Tiến**

### **API**

GET /api/v1/admin/support/tickets?status={status}

GET /api/v1/admin/support/tickets/{ticketId}

POST /api/v1/admin/support/tickets/{ticketId}/messages

PATCH /api/v1/admin/support/tickets/{ticketId}/resolve

### **Android mapping**

Không nối vào app khách/thợ.

Nối vào AdminSupportTicketScreen nếu làm dashboard.

## **10.6. Admin Transaction --- Phú**

### **API**

GET /api/v1/admin/transactions?type={type}&status={status}

GET /api/v1/admin/transactions/{transactionId}

POST /api/v1/admin/transactions/{transactionId}/confirm-withdraw

POST /api/v1/admin/transactions/{transactionId}/reject-withdraw

### **Android mapping**

Không nối vào app khách/thợ.

Nối vào AdminTransactionScreen nếu làm dashboard.

## **10.7. Admin Analytics --- Thế Anh**

### **API**

GET /api/v1/admin/analytics/revenue?from={date}&to={date}

GET /api/v1/admin/analytics/bookings?from={date}&to={date}

GET /api/v1/admin/analytics/top-services?from={date}&to={date}

GET /api/v1/admin/analytics/workers/top-rated

### **Android mapping**

Không nối vào app khách/thợ.

Nối vào AdminAnalyticsScreen nếu làm dashboard.

## **10.8. Admin Config --- Hiếu**

### **API**

GET /api/v1/admin/config/discount-rate

PUT /api/v1/admin/config/discount-rate

GET /api/v1/admin/config/kpi-milestones

PUT /api/v1/admin/config/kpi-milestones

### **Android mapping**

Không nối vào app khách/thợ.

Nối vào AdminConfigScreen nếu làm dashboard.

## **10.9. Admin Broadcast Notification --- Hiếu**

### **API**

GET /api/v1/admin/notifications

POST /api/v1/admin/notifications

GET /api/v1/admin/notifications/{notificationId}

### **Android mapping**

Không nối vào app khách/thợ với quyền admin.

Kết quả broadcast hiển thị ở:

Notification screen

Worker topbar notification icon

Customer topbar notification icon

# **11. Bảng mapping API --- Android screen tổng hợp**

## **11.1. Auth**

  ------------------------------------------------------------------------
  **API**                 **Người    **Android screen/package**
                          chính**    
  ----------------------- ---------- -------------------------------------
  POST /auth/register     Hiếu       RegisterFragment, AuthViewModel,
                                     AuthApi

  POST /auth/login        Hiếu       LoginFragment, AuthViewModel,
                                     SessionStorage

  POST /auth/login/google Hiếu       LoginFragment, Google Sign-In flow

  POST /auth/logout       Hiếu       WorkerProfileViewModel,
                                     CustomerProfileViewModel

  POST                    Hiếu       AuthInterceptor/token refresh layer
  /auth/refresh-token                

  POST                    Hiếu       ForgotPasswordFragment
  /auth/forgot-password              

  POST                    Hiếu       ResetPasswordFragment
  /auth/reset-password               

  PATCH                   Hiếu       Profile change password screen
  /auth/change-password              

  GET /users/me           Hiếu       App startup/session check, Profile
                                     screen

  PATCH /users/me         Hiếu       Profile edit screen
  ------------------------------------------------------------------------

## **11.2. Customer/Search/Booking**

  ---------------------------------------------------------------------------------------
  **API**                                     **Người   **Android screen/package**
                                              chính**   
  ------------------------------------------- --------- ---------------------------------
  GET /services/categories                    Thế Anh   CustomerHomeFragment, service
                                                        picker

  GET /services/categories/{id}/items         Thế Anh   CustomerBookingCreateFragment,
                                                        quotation item picker

  GET /workers/nearby                         Thế Anh   SearchNearbyWorkersFragment, map
                                                        result screen

  GET /customers/me/addresses                 Thế Anh   CustomerAddressListFragment,
                                                        booking create screen

  POST /customers/me/addresses                Thế Anh   CustomerAddressEditFragment

  PATCH /customers/me/addresses/{id}          Thế Anh   CustomerAddressEditFragment

  PATCH /customers/me/addresses/{id}/default  Thế Anh   CustomerAddressListFragment

  GET /customers/me/favorite-workers          Thế Anh   CustomerFavoriteWorkersFragment

  POST                                        Thế Anh   WorkerPublicProfileFragment,
  /customers/me/favorite-workers/{workerId}             history detail

  POST /bookings                              Thế Anh   CustomerBookingCreateFragment

  GET /bookings?role=CUSTOMER                 Thế Anh   CustomerBookingListFragment,
                                                        history

  GET /bookings/{id}                          Thế       Customer detail, worker detail
                                              Anh/Phú   

  POST /bookings/{id}/cancel                  Thế       Customer detail, worker detail,
                                              Anh/Phú   cancel reason bottom sheet
  ---------------------------------------------------------------------------------------

## **11.3. Worker**

  --------------------------------------------------------------------------
  **API**                    **Người   **Android screen/package**
                             chính**   
  -------------------------- --------- -------------------------------------
  GET /workers/me/home       Phú       fragment_worker_home.xml,
                                       WorkerHomeViewModel

  PATCH /workers/me/status   Phú       layout_worker_home_toggle.xml,
                                       WorkerStatusViewModel

  PATCH /workers/me/location Phú       LocationTrackingService

  GET /workers/me/schedule   Phú       Home schedule area, orders schedule
                                       tab

  GET /workers/me/history    Phú       fragment_worker_orders.xml history
                                       tab

  GET /workers/me/stats      Phú       home stats/chart, stats screen

  GET /workers/me/kpi        Phú       home KPI widget, stats screen

  POST /workers/me/kyc       Phú       WorkerKycFragment

  GET /workers/me/kyc/status Phú       WorkerProfileFragment, home banner

  POST                       Phú       WorkerKycFragment reject/resubmit
  /workers/me/kyc/resubmit             state
  --------------------------------------------------------------------------

## **11.4. Booking lifecycle worker**

  -------------------------------------------------------------------------------
  **API**                           **Người    **Android screen/package**
                                    chính**    
  --------------------------------- ---------- ----------------------------------
  GET                               Phú        dialog_incoming_order.xml, worker
  /workers/me/assignments/pending              home

  POST /assignments/{id}/accept     Phú        incoming order dialog

  POST /assignments/{id}/reject     Phú        incoming order dialog

  POST /start-moving                Phú        fragment_worker_order_detail.xml

  POST /arrive                      Phú        fragment_worker_order_detail.xml

  POST /start-survey                Phú        fragment_worker_order_detail.xml

  POST /start-repair                Phú        fragment_worker_order_detail.xml

  POST /worker-complete             Phú        fragment_worker_order_detail.xml

  POST /customer-complete           Thế Anh    CustomerBookingDetailFragment

  POST /proof-of-work               Phú        fragment_worker_order_detail.xml
                                               ảnh trước/sau

  GET /proof-of-work                Phú/Tiến   worker detail, complaint detail
  -------------------------------------------------------------------------------

## **11.5. Wallet/payment**

  ------------------------------------------------------------------------------
  **API**                               **Người    **Android screen/package**
                                        chính**    
  ------------------------------------- ---------- -----------------------------
  GET /workers/me/wallet                Phú        fragment_worker_wallet.xml

  GET /workers/me/wallet/transactions   Phú        item_wallet_transaction.xml

  POST /workers/me/wallet/deposits      Phú        deposit screen/bottom sheet

  GET                                   Phú        QR payment screen
  /workers/me/wallet/deposits/{id}/qr              

  POST /workers/me/wallet/withdrawals   Phú        withdraw screen/bottom sheet

  GET /workers/me/bank-accounts         Phú        bank account list screen

  POST /bookings/{id}/payments          Thế Anh    CustomerPaymentFragment

  GET /bookings/{id}/invoice            Tiến       invoice/history detail screen

  GET /bookings/{id}/warranty-status    Tiến       history detail, complaint
                                                   create screen
  ------------------------------------------------------------------------------

## **11.6. Chat/review/complaint/support**

  -----------------------------------------------------------------------------
  **API**                            **Người   **Android screen/package**
                                     chính**   
  ---------------------------------- --------- --------------------------------
  GET /chat/rooms/{bookingId}        Tiến      worker/customer chat screen

  GET                                Tiến      fragment_worker_chat.xml hoặc
  /chat/rooms/{bookingId}/messages             fragment_chat_worker.xml

  POST                               Tiến      chat send message/image
  /chat/rooms/{bookingId}/messages             

  POST /bookings/{id}/reviews        Tiến      review dialog after completion

  GET /workers/{workerId}/reviews    Tiến      worker public profile

  POST /bookings/{id}/complaints     Tiến      complaint create screen

  GET /bookings/{id}/complaints      Tiến      complaint detail screen

  POST /complaints/{id}/respond      Tiến      worker complaint response screen

  GET /support/faq                   Tiến      FAQ screen

  POST /support/tickets              Tiến      support ticket create screen

  GET /support/tickets               Tiến      support ticket list screen
  -----------------------------------------------------------------------------

# **12. Thứ tự làm thực tế theo tuần/đợt**

## **Đợt 1**

Hiếu:

\- AuthApi

\- Login/Register/Forgot/Reset/Change Password

\- Users/me

\- UploadApi

\- Device token + notification base

Thế Anh:

\- ServiceApi

\- Service category/item response DTO

Phú:

\- Chuẩn bị WorkerHomeApi/WorkerAvailabilityApi contract

Tiến:

\- Soát DB còn thiếu cho chat/support/invoice

## **Đợt 2**

Phú:

\- Worker profile/status/location

\- Worker KYC

\- Worker home/schedule/history/stats/kpi

Thế Anh:

\- Customer address

\- Favorite worker

\- Search nearby worker

\- Worker public profile

Hiếu:

\- Hoàn thiện token/session/logout

Tiến:

\- Chuẩn bị complaint/warranty response contract

## **Đợt 3**

Thế Anh:

\- Booking create/list/detail/cancel

\- Quotation create/approve/reject

Phú:

\- Assignment accept/reject/miss

\- Booking status lifecycle

\- Tracking

\- Proof of work

Tiến:

\- Chat skeleton nếu DB đã có

Hiếu:

\- Security rule theo role CUSTOMER/WORKER/ADMIN

## **Đợt 4**

Phú:

\- Wallet

\- Transactions

\- Deposit

\- Withdraw

\- Bank account

\- Webhook SePay

Thế Anh:

\- Payment request/status

Tiến:

\- Warranty status

\- Invoice display

Hiếu:

\- Admin config base

## **Đợt 5**

Tiến:

\- Chat

\- Review

\- Complaint

\- Support FAQ/Ticket

Phú:

\- Worker complaint response

\- Wallet hold/release liên quan complaint

Thế Anh:

\- History/invoice integration từ booking

Hiếu:

\- Notification broadcast nhận ở app

## **Đợt 6**

Admin Dashboard:

\- Hiếu: Admin users, config, broadcast notification

\- Phú: Admin KYC, transactions

\- Tiến: Admin complaints, support tickets

\- Thế Anh: Admin analytics

# **13. Checklist trước khi backend và Android nối thật**

## **13.1. Backend checklist**

1\. Tất cả endpoint có prefix /api/v1.

2\. Endpoint dùng /me khi thao tác với user hiện tại.

3\. Booking accept/reject dùng assignmentId.

4\. Booking status dùng action endpoint, không dùng status tùy ý từ
client.

5\. Wallet/deposit/withdraw có transaction status rõ ràng.

6\. Webhook có signature/secret.

7\. API trả response thống nhất.

8\. Có phân quyền CUSTOMER/WORKER/ADMIN.

9\. Có validation request body.

10\. Có errorCode rõ cho Android xử lý UI.

## **13.2. Android checklist**

1\. Mỗi feature có Api interface riêng.

2\. DTO nằm trong data/remote/dto.

3\. Mapper chuyển DTO sang domain model.

4\. ViewModel chỉ gọi use case.

5\. Fragment không gọi Retrofit.

6\. RepositoryImpl thay mock bằng Retrofit call.

7\. AuthInterceptor gắn Bearer token.

8\. BASE_URL dùng 10.0.2.2 khi chạy emulator.

9\. Chạy assembleDebug sau mỗi nhóm tích hợp.

10\. Không đổi domain model nếu chỉ thay response DTO.

# **14. Kết luận kỹ thuật**

Bản phân chia này nên được dùng làm contract chính giữa backend và
Android.

Trọng tâm cần giữ:

Phú làm phần worker/order/wallet nặng nhất.

Thế Anh làm phần customer/search/booking/payment.

Tiến làm phần chat/review/complaint/support/invoice.

Hiếu làm auth/account/upload/notification/admin config.

Không nên triển khai theo danh sách API cũ nguyên bản vì còn các lỗi:

\- Prefix không thống nhất.

\- Dùng {id} thay vì /me.

\- Booking accept/reject chưa gắn assignment.

\- PATCH status quá rộng.

\- Wallet thiếu trạng thái deposit/withdraw.

\- KYC thiếu resubmit.

\- Warranty cần endpoint rõ để Android hiện/ẩn nút khiếu nại.

Nếu làm theo tài liệu này, Android có thể nối từng feature theo đúng
Clean Architecture mà không phải sửa Fragment/ViewModel quá nhiều khi
đổi từ mock sang API thật.
