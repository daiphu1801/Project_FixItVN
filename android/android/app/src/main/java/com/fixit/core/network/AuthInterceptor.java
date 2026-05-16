package com.fixit.core.network;

import com.fixit.core.storage.SessionStorage;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public class AuthInterceptor implements Interceptor {
    private final SessionStorage sessionStorage;

    @Inject
    public AuthInterceptor(SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = sessionStorage.getAccessToken();
        if (token == null || token.isEmpty()) {
            return chain.proceed(original);
        }

        Request authenticated = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(authenticated);
    }
}
