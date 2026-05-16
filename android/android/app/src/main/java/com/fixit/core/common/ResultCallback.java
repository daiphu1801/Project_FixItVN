package com.fixit.core.common;

public interface ResultCallback<T> {
    void onResult(Result<T> result);
}
