package com.fixit.core.storage;

import com.fixit.feature.auth.domain.model.Session;

public interface SessionStorage {
    void saveSession(Session session);

    String getAccessToken();

    String getRefreshToken();

    String getUserId();

    String getUserRole();

    Session getSession();

    void clear();
}
