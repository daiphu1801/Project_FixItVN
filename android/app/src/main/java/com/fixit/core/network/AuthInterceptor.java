package com.fixit.core.network;

import com.fixit.core.common.Constants;
import com.fixit.core.storage.SessionStorage;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public class AuthInterceptor implements Interceptor {

    private static final String MOCK_ACCESS_TOKEN = "mock_access_token_xyz";

    private final SessionStorage sessionStorage;

    @Inject
    public AuthInterceptor(SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        addAuthorizationHeader(builder);

        return chain.proceed(builder.build());
    }

    private void addAuthorizationHeader(Request.Builder builder) {
        String token = sessionStorage.getAccessToken();

        if (token == null || token.trim().isEmpty()) {
            return;
        }

        if (MOCK_ACCESS_TOKEN.equals(token)) {
            return;
        }

        builder.header("Authorization", "Bearer " + token);
    }
}