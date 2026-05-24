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
        addDebugWorkerHeader(builder);

        return chain.proceed(builder.build());
    }

    /**
     * AUTH THẬT:
     * Sau khi backend auth/JWT hoàn thiện, app sẽ lấy access token thật từ SessionStorage
     * và gửi lên backend bằng header:
     *
     * Authorization: Bearer <access_token>
     *
     * Lưu ý:
     * - Không gửi token mock lên backend.
     * - Token mock không phải JWT hợp lệ nên backend sẽ báo MalformedJwtException.
     */
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

    /**
     * DEV ONLY:
     * Header này chỉ dùng tạm trong giai đoạn backend chưa lấy được workerId từ JWT thật.
     *
     * Backend đang dùng:
     * X-Debug-Worker-Id: <worker_id>
     *
     * Sau khi auth thật hoàn thiện:
     * 1. Xóa DEBUG_WORKER_ID trong Constants.java.
     * 2. Xóa method addDebugWorkerHeader().
     * 3. Xóa dòng gọi addDebugWorkerHeader(builder).
     * 4. Backend lấy workerId từ SecurityContext/JWT thay vì X-Debug-Worker-Id.
     */
    private void addDebugWorkerHeader(Request.Builder builder) {
        if (Constants.DEBUG_WORKER_ID == null || Constants.DEBUG_WORKER_ID.trim().isEmpty()) {
            return;
        }

        builder.header("X-Debug-Worker-Id", Constants.DEBUG_WORKER_ID);
    }
}