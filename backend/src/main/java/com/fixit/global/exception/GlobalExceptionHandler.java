package com.fixit.global.exception;

import com.fixit.global.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(AppException.class)
        public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
                ErrorCode errorCode = ex.getErrorCode();

                ApiResponse<Void> response = ApiResponse.error(
                                errorCode.getCode(),
                                errorCode.getMessage());

                return new ResponseEntity<>(response, errorCode.getHttpStatus());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Void>> handleValidationException(
                        MethodArgumentNotValidException ex) {
                String message = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.joining("; "));

                ApiResponse<Void> response = ApiResponse.error(
                                ErrorCode.INVALID_REQUEST_PARAMETER.getCode(),
                                message);

                return new ResponseEntity<>(response, ErrorCode.INVALID_REQUEST_PARAMETER.getHttpStatus());
        }

        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
                log.warn("Illegal state exception: {}", ex.getMessage());
                ApiResponse<Void> response = ApiResponse.error(400, ex.getMessage());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
                log.warn("Illegal argument exception: {}", ex.getMessage());
                ApiResponse<Void> response = ApiResponse.error(400, ex.getMessage());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
                log.error("UNHANDLED_EXCEPTION", ex);

                ApiResponse<Void> response = ApiResponse.error(
                                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                                ErrorCode.INTERNAL_SERVER_ERROR.getMessage());

                return new ResponseEntity<>(response, ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus());
        }

        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
                        HttpRequestMethodNotSupportedException ex) {
                ApiResponse<Void> response = ApiResponse.error(
                                405,
                                "Phương thức HTTP không được hỗ trợ. Hãy kiểm tra GET/POST/PATCH/DELETE.");

                return ResponseEntity
                                .status(HttpStatus.METHOD_NOT_ALLOWED)
                                .body(response);
        }
}