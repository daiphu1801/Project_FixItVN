package com.fixit.core.common;

public class Result<T> {
    private final T data;
    private final AppError error;

    private Result(T data, AppError error) {
        this.data = data;
        this.error = error;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, null);
    }

    public static <T> Result<T> error(AppError error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public T getData() {
        return data;
    }

    public AppError getError() {
        return error;
    }
}
