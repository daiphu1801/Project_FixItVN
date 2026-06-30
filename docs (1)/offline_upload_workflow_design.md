# Offline Upload Workflow Design

## 1. Mục tiêu

Nâng upload Android từ hàng đợi retry confirm đơn giản thành một workflow offline hoàn chỉnh:

```text
Chọn file local
-> xin presigned-url
-> upload Cloudinary
-> confirm backend
-> gọi API nghiệp vụ
-> dọn Room và file tạm
```

Workflow mới phải chịu được các lỗi thường gặp:

- Mất mạng trước khi xin presigned-url.
- Mất mạng sau khi có presigned-url nhưng chưa upload Cloudinary.
- Upload Cloudinary xong nhưng confirm backend lỗi.
- Confirm xong nhưng gọi API nghiệp vụ lỗi.
- App bị kill/crash giữa chừng.
- Nhiều ảnh cùng lúc, đặc biệt là KYC/proof-of-work.

## 2. Hiện trạng cũ

Room trước đây chỉ lưu `pending_upload_confirms`, nghĩa là chỉ xử lý được case:

```text
Cloudinary upload thành công
-> confirm backend lỗi
-> lưu Room
-> retry confirm
```

Hạn chế:

- Không lưu `localFilePath`, nên không resume được nếu chưa upload Cloudinary.
- Không lưu `uploadUrl`, `formData`, `expiresAt`, nên không retry được presigned ticket.
- Không có `status` workflow đầy đủ.
- Không biết ảnh thuộc nghiệp vụ nào, ví dụ avatar, KYC, proof-of-work.
- Confirm thành công xong là xóa Room, nên nếu crash trước API nghiệp vụ thì mất khả năng retry.

## 3. Kiến trúc mới

### 3.1. Thành phần Android

- `UploadFilePreparer`: copy ảnh từ `Uri` vào internal storage tại `files/pending_uploads/`.
- `PendingUploadEntity`: lưu toàn bộ trạng thái upload trong Room.
- `PendingUploadDao`: truy vấn FIFO, group KYC, update/delete record.
- `UploadWorkflowProcessor`: state machine xử lý từng bước upload.
- `UploadRetryWorker`: WorkManager worker chạy khi có mạng.
- `UploadWorkManagerScheduler`: enqueue unique work `offline_upload_retry`.
- `UploadViewModel`: expose API upload cũ và API upload có target nghiệp vụ.

### 3.2. Thành phần backend

- `/api/v1/uploads/presigned-url`: tạo upload ticket và `uploaded_files.status = PENDING`.
- `/api/v1/uploads/confirm`: idempotent confirm.
- `/api/v1/users/me/avatar`: consume upload avatar.
- `/api/v1/workers/me/kyc`: consume group KYC.
- `/api/v1/bookings/{bookingId}/proof-of-work`: consume proof before/after repair.
- `UploadConsumeService.consume()`: idempotent khi retry cùng upload và cùng target.

## 4. Room Schema

Bảng mới: `pending_uploads`.

| Field | Ý nghĩa |
|---|---|
| `id` | Local primary key |
| `localFilePath` | File đã copy vào internal storage |
| `originalFileName` | Tên file gốc |
| `contentType` | MIME type, ví dụ `image/jpeg` |
| `fileSize` | Kích thước file |
| `purpose` | `AVATAR`, `WORKER_KYC_FRONT`, `PROOF_BEFORE_REPAIR`, ... |
| `uploadId` | ID backend trả từ presigned-url |
| `objectKey` | Cloudinary public id |
| `uploadUrl` | Cloudinary upload endpoint |
| `fileUrl` | Secure URL sau upload |
| `formDataJson` | Form data ký bởi backend |
| `presignedExpiresAt` | Thời điểm hết hạn ticket local millis |
| `status` | Trạng thái workflow |
| `targetType` | `USER_AVATAR`, `WORKER_KYC`, `PROOF_OF_WORK`, ... |
| `targetEntityId` | ID nghiệp vụ, ví dụ bookingId |
| `groupId` | Nhóm nhiều ảnh, ví dụ một lần submit KYC |
| `slotKey` | Vai trò ảnh trong group, ví dụ `front`, `back`, `certificate` |
| `extraPayloadJson` | Metadata phụ, ví dụ proofType |
| `retryCount` | Số lần retry |
| `lastError` | Lỗi gần nhất |
| `createdAt` | Thời điểm tạo record |
| `updatedAt` | Thời điểm update |
| `lastAttemptAt` | Thời điểm thử gần nhất |

## 5. State Machine

Các trạng thái chuẩn:

```text
LOCAL_SELECTED
PRESIGNED_CREATED
CLOUDINARY_UPLOAD_FAILED
CLOUDINARY_UPLOADED
CONFIRM_FAILED
CONFIRMED
CONSUME_FAILED
CONSUMED
```

Retry rule:

| Status | Hành động tiếp theo |
|---|---|
| `LOCAL_SELECTED` | Gọi `/uploads/presigned-url` |
| `PRESIGNED_CREATED` | Upload Cloudinary nếu ticket còn hạn |
| `PRESIGNED_CREATED` hết hạn | Gọi lại `/uploads/presigned-url` |
| `CLOUDINARY_UPLOAD_FAILED` | Upload Cloudinary lại hoặc xin ticket mới nếu hết hạn |
| `CLOUDINARY_UPLOADED` | Gọi `/uploads/confirm` |
| `CONFIRM_FAILED` | Gọi `/uploads/confirm` lại |
| `CONFIRMED` | Gọi API nghiệp vụ nếu có `targetType` |
| `CONSUME_FAILED` | Gọi API nghiệp vụ lại |
| `CONSUMED` | Xóa Room record và file local |

Nếu upload không có `targetType`, workflow dừng ở `CONFIRMED`. Đây là fallback cho các purpose chưa có endpoint nghiệp vụ rõ ràng.

## 6. Business Consume

### Avatar

Input Android:

```text
purpose = AVATAR
targetType = USER_AVATAR
slotKey = avatar
```

API nghiệp vụ:

```http
PATCH /api/v1/users/me/avatar
```

Body:

```json
{
  "uploadId": "..."
}
```

### Proof of Work

Input Android:

```text
purpose = PROOF_BEFORE_REPAIR hoặc PROOF_AFTER_REPAIR
targetType = PROOF_OF_WORK
targetEntityId = bookingId
slotKey = before hoặc after
```

API nghiệp vụ:

```http
POST /api/v1/bookings/{bookingId}/proof-of-work
```

Body:

```json
{
  "uploadId": "...",
  "proofType": "BEFORE_REPAIR"
}
```

hoặc:

```json
{
  "uploadId": "...",
  "proofType": "AFTER_REPAIR"
}
```

### Worker KYC

Input Android:

```text
targetType = WORKER_KYC
groupId = UUID của lần submit KYC
slotKey = front | back | certificate
```

Worker chỉ submit KYC khi trong cùng `groupId` có:

- `front` đã `CONFIRMED`.
- `back` đã `CONFIRMED`.
- `certificate` nếu có thì cũng đã `CONFIRMED`.

API nghiệp vụ:

```http
POST /api/v1/workers/me/kyc
```

Body:

```json
{
  "frontImageUploadId": "...",
  "backImageUploadId": "...",
  "certificateUploadIds": ["..."]
}
```

## 7. Idempotency Backend

### Confirm upload

`/uploads/confirm` idempotent:

- Nếu upload còn `PENDING`, backend verify Cloudinary rồi chuyển `CONFIRMED`.
- Nếu upload đã `CONFIRMED`, backend validate `objectKey`, `fileUrl`, `contentType`, `fileSize` khớp rồi trả success.

### Consume upload

`UploadConsumeService.consume()` idempotent:

- Nếu upload đã linked đúng cùng `owner`, `purpose`, `targetType`, `targetEntityId`, trả success.
- Nếu upload đã linked sang target khác, trả `UPLOAD_ALREADY_USED`.
- Nếu upload chưa `CONFIRMED`, trả `UPLOAD_NOT_CONFIRMED`.

### Proof-of-work

`POST /bookings/{bookingId}/proof-of-work` idempotent:

- Nếu proof đã tồn tại và cùng uploadId/cùng imageUrl, trả proof cũ.
- Nếu proof đã tồn tại nhưng upload khác, trả conflict.

## 8. WorkManager

Worker config:

```text
Worker: UploadRetryWorker
Unique work name: offline_upload_retry
Constraint: NetworkType.CONNECTED
Backoff: exponential, 30 seconds
```

Schedule khi:

- App start.
- Có upload record mới.
- Một bước workflow fail và cần retry.

Worker xử lý FIFO theo `createdAt`, giới hạn số bước mỗi run để tránh chạy vô hạn.

## 9. Cleanup

Khi workflow thành công:

```text
status = CONSUMED
-> xóa file local trong files/pending_uploads/
-> xóa Room record
```

Nếu workflow dừng ở `CONFIRMED` vì chưa có target nghiệp vụ, file local vẫn có thể được dọn sau khi app đã lấy được `fileUrl` hoặc sau một cleanup policy riêng.

## 10. Test Checklist

### Android

- Chọn ảnh rồi mất mạng trước presign: record giữ `LOCAL_SELECTED`.
- Có mạng lại: worker xin presigned-url và upload tiếp.
- Mất mạng sau presign: record giữ `PRESIGNED_CREATED`.
- Ticket hết hạn: worker gọi lại `/uploads/presigned-url`.
- Cloudinary upload xong nhưng confirm lỗi: worker retry confirm, không upload lại Cloudinary.
- Confirm xong nhưng consume lỗi: worker retry API nghiệp vụ, không upload lại Cloudinary hoặc confirm lại.
- App bị kill sau khi chọn ảnh: mở app lại vẫn retry.
- Avatar update thành công sau offline retry.
- KYC nhiều ảnh cùng `groupId` chỉ submit khi đủ front/back.
- Proof before/after gắn đúng `bookingId`.
- `CONSUMED` xóa file local và Room record.

### Backend

- Confirm idempotent với status `CONFIRMED`.
- Consume idempotent với cùng upload và cùng target.
- Consume khác target bị reject.
- Proof retry cùng upload không tạo duplicate.
- Proof khác upload cùng proofType vẫn conflict.

### Build

- Android: `gradlew assembleDebug`.
- Backend: chạy Maven test/build khi môi trường có `mvn` hoặc Maven wrapper.

## 11. Ghi chú triển khai

- Android build đã pass với Room schema mới, Hilt, Retrofit và WorkManager.
- Backend Maven test cần môi trường có `mvn`; nếu máy không có Maven thì chưa verify được compile backend bằng command.
- Các purpose chưa rõ endpoint nghiệp vụ như chat, complaint, booking issue nên giữ ở `CONFIRMED` cho đến khi contract rõ ràng.
