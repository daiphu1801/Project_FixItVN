package com.fixit.core.ui;

public class UiState<T> {
    private final boolean loading;
    private final T data;
    private final String errorMessage;

    private UiState(boolean loading, T data, String errorMessage) {
        this.loading = loading;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> UiState<T> loading() {
        return new UiState<>(true, null, null);
    }

    public static <T> UiState<T> success(T data) {
        return new UiState<>(false, data, null);
    }

    public static <T> UiState<T> error(String message) {
        return new UiState<>(false, null, message);
    }

    public boolean isLoading() {
        return loading;
    }

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
