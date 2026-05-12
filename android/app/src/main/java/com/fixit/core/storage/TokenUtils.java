package com.fixit.core.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.fixit.core.common.Constants;

public class TokenUtils {
    private final SharedPreferences prefs;

    public TokenUtils(Context context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveTokens(String accessToken, String refreshToken) {
        prefs.edit()
            .putString(Constants.PREF_ACCESS_TOKEN, accessToken)
            .putString(Constants.PREF_REFRESH_TOKEN, refreshToken)
            .apply();
    }

    public String getAccessToken() {
        return prefs.getString(Constants.PREF_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(Constants.PREF_REFRESH_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public void saveUserId(String userId) {
        prefs.edit().putString(Constants.PREF_USER_ID, userId).apply();
    }

    public String getUserId() {
        return prefs.getString(Constants.PREF_USER_ID, null);
    }

    public void saveUserRole(String role) {
        prefs.edit().putString(Constants.PREF_USER_ROLE, role).apply();
    }

    public String getUserRole() {
        return prefs.getString(Constants.PREF_USER_ROLE, null);
    }

    public boolean isWorker() {
        return Constants.ROLE_WORKER.equals(getUserRole());
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
