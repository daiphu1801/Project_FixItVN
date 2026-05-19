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
    INVALID_REQUEST_PARAMETER(400, HttpStatus.BAD_REQUEST, "Invalid request parameter");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}