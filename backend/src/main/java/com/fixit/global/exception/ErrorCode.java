package com.fixit.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Auth & Security
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "Unauthorized access"),
    FORBIDDEN(403, HttpStatus.FORBIDDEN, "Forbidden access"),
    USER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "User not found"),
    INVALID_CREDENTIALS(400, HttpStatus.BAD_REQUEST, "Số điện thoại hoặc mật khẩu không chính xác"),
    USER_ALREADY_EXISTS(409, HttpStatus.CONFLICT, "User already exists"),
    PHONE_ALREADY_EXISTS(409, HttpStatus.CONFLICT, "Số điện thoại đã được đăng ký"),
    EMAIL_ALREADY_EXISTS(409, HttpStatus.CONFLICT, "Email đã được đăng ký"),
    USER_BLOCKED(403, HttpStatus.FORBIDDEN, "Tài khoản đã bị khóa"),
    NOTIFICATION_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Notification not found"),

    // Worker assignment
    WORKER_ASSIGNMENT_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy đơn đang phát cho thợ hiện tại"),
    WORKER_ASSIGNMENT_ALREADY_HANDLED(409, HttpStatus.CONFLICT, "Đơn đã được phản hồi trước đó"),
    WORKER_ASSIGNMENT_EXPIRED(409, HttpStatus.CONFLICT, "Đơn đã hết thời gian phản hồi"),
    BOOKING_ALREADY_TAKEN(409, HttpStatus.CONFLICT, "Đơn đã có thợ khác nhận"),

    // System
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    INVALID_REQUEST_PARAMETER(400, HttpStatus.BAD_REQUEST, "Invalid request parameter"),

    // Worker profile & skills
    WORKER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy thợ hiện tại"),
    SERVICE_CATEGORY_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy danh mục dịch vụ"),
    WORKER_SKILL_DUPLICATED(409, HttpStatus.CONFLICT, "Danh sách kỹ năng có serviceId bị trùng"),

    // Booking action
    BOOKING_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy đơn của thợ hiện tại"),
    BOOKING_INVALID_STATUS_TRANSITION(409, HttpStatus.CONFLICT, "Trạng thái đơn hiện tại không cho phép thực hiện hành động này"),
    BOOKING_PREVIOUS_ACTION_REQUIRED(409, HttpStatus.CONFLICT, "Chưa hoàn thành bước trước đó"),
    BOOKING_ACTION_ALREADY_DONE(409, HttpStatus.CONFLICT, "Hành động này đã được thực hiện trước đó"),

    // Worker bank account
    WORKER_BANK_ACCOUNT_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản ngân hàng của thợ hiện tại"),
    WORKER_BANK_ACCOUNT_IN_USE(409, HttpStatus.CONFLICT, "Tài khoản ngân hàng đã phát sinh giao dịch nên không thể xóa"),

    // File upload
    UPLOAD_INVALID_PURPOSE(400, HttpStatus.BAD_REQUEST, "Mục đích upload không hợp lệ"),
    UPLOAD_INVALID_CONTENT_TYPE(400, HttpStatus.BAD_REQUEST, "Định dạng file không được hỗ trợ"),
    UPLOAD_FILE_TOO_LARGE(400, HttpStatus.BAD_REQUEST, "Dung lượng file vượt quá giới hạn cho phép"),
    UPLOAD_FILE_SIZE_MISMATCH(400, HttpStatus.BAD_REQUEST, "Dung lượng file xác nhận không khớp với upload đã cấp"),
    UPLOAD_INVALID_FILE_NAME(400, HttpStatus.BAD_REQUEST, "Tên file không hợp lệ"),
    UPLOAD_INVALID_OBJECT_KEY(400, HttpStatus.BAD_REQUEST, "Object key không hợp lệ"),
    UPLOAD_INVALID_FILE_URL(400, HttpStatus.BAD_REQUEST, "File URL không hợp lệ"),
    UPLOAD_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy upload"),
    UPLOAD_INVALID_STATUS(409, HttpStatus.CONFLICT, "Trạng thái upload không hợp lệ"),
    UPLOAD_EXPIRED(409, HttpStatus.CONFLICT, "Upload URL đã hết hạn"),
    UPLOAD_PUBLIC_ID_MISMATCH(400, HttpStatus.BAD_REQUEST, "Public ID không khớp với upload đã cấp"),
    UPLOAD_STORAGE_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi làm việc với hệ thống lưu trữ file"),
    UPLOAD_PROVIDER_OBJECT_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy file trên hệ thống lưu trữ"),
    UPLOAD_PROVIDER_FILE_SIZE_MISMATCH(400, HttpStatus.BAD_REQUEST, "Dung lượng file trên storage không khớp với upload đã cấp"),
    UPLOAD_PROVIDER_RESOURCE_TYPE_INVALID(400, HttpStatus.BAD_REQUEST, "Loại tài nguyên trên storage không hợp lệ"),
    UPLOAD_NOT_CONFIRMED(409, HttpStatus.CONFLICT, "Upload chưa được xác nhận"),
    UPLOAD_ALREADY_USED(409, HttpStatus.CONFLICT, "Upload đã được sử dụng"),
    UPLOAD_PURPOSE_NOT_ALLOWED(400, HttpStatus.BAD_REQUEST, "Upload không đúng mục đích nghiệp vụ"),

    //eKYC
    WORKER_KYC_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ KYC của thợ hiện tại"),

    //Proof of work
    PROOF_OF_WORK_ALREADY_EXISTS(409, HttpStatus.CONFLICT, "Ảnh bằng chứng loại này đã tồn tại cho đơn hàng"),

    // Worker wallet
    WALLET_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy ví của thợ hiện tại"),
    WALLET_DEBT_NOT_FOUND(409, HttpStatus.CONFLICT, "Không có khoản nợ cần thanh toán"),
    WALLET_DEPOSIT_AMOUNT_TOO_SMALL(400, HttpStatus.BAD_REQUEST, "Số tiền nạp phải lớn hơn hoặc bằng khoản nợ hiện tại"),
    WALLET_DEPOSIT_INVALID_AMOUNT(400, HttpStatus.BAD_REQUEST, "Số tiền nạp không hợp lệ"),
    WALLET_DEPOSIT_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch nạp tiền của thợ hiện tại"),
    WALLET_DEPOSIT_INVALID_STATUS(409, HttpStatus.CONFLICT, "Trạng thái giao dịch nạp tiền không hợp lệ"),
    WALLET_DEPOSIT_PENDING_EXISTS(409, HttpStatus.CONFLICT, "Đang có giao dịch nạp tiền chờ thanh toán"),

    // Review
    BOOKING_NOT_COMPLETED(400, HttpStatus.BAD_REQUEST, "Đơn hàng chưa hoàn thành, không thể đánh giá"),
    REVIEW_ALREADY_EXISTS(409, HttpStatus.CONFLICT, "Đơn hàng này đã được đánh giá trước đó");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
