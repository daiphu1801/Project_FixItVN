package com.fixit.feature.auth.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.auth.domain.model.Session;

public interface AuthRepository {
    void login(String phone, String password, String role, ResultCallback<Session> callback);

    void register(String phone,String email, String password, String fullName, String role, ResultCallback<Session> callback);

    void logout(ResultCallback<Void> callback);

    void refreshToken(String refreshToken, ResultCallback<Session> callback);

    Session getCurrentSession();

    void changePassword(String oldPassword, String newPassword, ResultCallback<Void> callback);
}
