package com.fixit.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Auth & Security
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "Unauthorized access"),
    FORBIDDEN(403, HttpStatus.FORBIDDEN, "Forbidden access"),
    USER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "User not found"),
    INVALID_CREDENTIALS(400, HttpStatus.BAD_REQUEST, "Invalid phone number or password"),
    USER_ALREADY_EXISTS(409, HttpStatus.CONFLICT, "User already exists"),

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

    // Worker wallet
    WALLET_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy ví của thợ hiện tại"),
    WALLET_DEBT_NOT_FOUND(409, HttpStatus.CONFLICT, "Không có khoản nợ cần thanh toán"),
    WALLET_DEPOSIT_AMOUNT_TOO_SMALL(400, HttpStatus.BAD_REQUEST, "Số tiền nạp phải lớn hơn hoặc bằng khoản nợ hiện tại"),
    WALLET_DEPOSIT_INVALID_AMOUNT(400, HttpStatus.BAD_REQUEST, "Số tiền nạp không hợp lệ"),
    WALLET_DEPOSIT_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch nạp tiền của thợ hiện tại"),
    WALLET_DEPOSIT_INVALID_STATUS(409, HttpStatus.CONFLICT, "Trạng thái giao dịch nạp tiền không hợp lệ"),
    WALLET_DEPOSIT_PENDING_EXISTS(409, HttpStatus.CONFLICT, "Đang có giao dịch nạp tiền chờ thanh toán");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}