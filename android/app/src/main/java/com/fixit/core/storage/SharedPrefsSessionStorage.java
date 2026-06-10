package com.fixit.core.storage;

import android.content.SharedPreferences;

import com.fixit.core.common.Constants;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.model.User;
import com.fixit.feature.auth.domain.model.UserRole;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharedPrefsSessionStorage implements SessionStorage {
    private final SharedPreferences prefs;

    @Inject
    public SharedPrefsSessionStorage(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public void saveSession(Session session) {
        if (session == null || session.getUser() == null) {
            return;
        }

        prefs.edit()
                .putString(Constants.PREF_ACCESS_TOKEN, session.getAccessToken())
                .putString(Constants.PREF_REFRESH_TOKEN, session.getRefreshToken())
                .putString(Constants.PREF_USER_ID, session.getUser().getId())
                .putString(Constants.PREF_USER_ROLE, session.getUser().getRole().name())
                .putString(Constants.PREF_USER_NAME, session.getUser().getFullName())
                .putString(Constants.PREF_USER_PHONE, session.getUser().getPhone())
                .apply();
    }

    @Override
    public String getAccessToken() {
        return prefs.getString(Constants.PREF_ACCESS_TOKEN, null);
    }

    @Override
    public String getRefreshToken() {
        return prefs.getString(Constants.PREF_REFRESH_TOKEN, null);
    }

    @Override
    public String getUserId() {
        return prefs.getString(Constants.PREF_USER_ID, null);
    }

    @Override
    public String getUserRole() {
        return prefs.getString(Constants.PREF_USER_ROLE, null);
    }

    @Override
    public Session getSession() {
        String accessToken = getAccessToken();
        String refreshToken = getRefreshToken();
        String userId = getUserId();
        String userRole = getUserRole();
        if (accessToken == null || userId == null || userRole == null) {
            return null;
        }

        String userName = prefs.getString(Constants.PREF_USER_NAME, null);
        String userPhone = prefs.getString(Constants.PREF_USER_PHONE, null);
        User user = new User(userId, userPhone, userName, UserRole.from(userRole));
        return new Session(accessToken, refreshToken, user);
    }

    @Override
    public void clear() {
        prefs.edit().clear().apply();
    }
}
