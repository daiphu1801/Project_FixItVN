package com.fixit.feature.auth.presentation;

public class AuthUiState {
    private final boolean loading;
    private final String errorMessage;

    private AuthUiState(boolean loading, String errorMessage) {
        this.loading = loading;
        this.errorMessage = errorMessage;
    }

    public static AuthUiState idle() {
        return new AuthUiState(false, null);
    }

    public static AuthUiState loading() {
        return new AuthUiState(true, null);
    }

    public static AuthUiState error(String message) {
        return new AuthUiState(false, message);
    }

    public static AuthUiState success() {
        return new AuthUiState(false, null);
    }

    public boolean isLoading() {
        return loading;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
