package com.fixit.core.network;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    public static ApiResponse<?> parseError(retrofit2.Response<?> response) {
        try {
            if (response == null || response.errorBody() == null) {
                return null;
            }
            String rawJson = response.errorBody().string();
            return new com.google.gson.Gson().fromJson(rawJson, ApiResponse.class);
        } catch (Exception e) {
            return null;
        }
    }
}