package com.fixit.core.common;

public class AppError {
    private final String message;
    private final Throwable cause;

    public AppError(String message) {
        this(message, null);
    }

    public AppError(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }
}
