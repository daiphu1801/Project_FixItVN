package com.fixit.feature.auth.data.remote.mapper;

import com.fixit.feature.auth.data.remote.dto.AuthResponse;
import com.fixit.feature.auth.domain.model.Session;
import com.fixit.feature.auth.domain.model.User;
import com.fixit.feature.auth.domain.model.UserRole;

public class AuthMapper {
    private AuthMapper() {
    }

    public static Session toSession(AuthResponse response) {
        if (response == null || response.getUser() == null) {
            return null;
        }

        User user = new User(
                response.getUser().getId(),
                response.getUser().getPhone(),
                response.getUser().getFullName(),
                UserRole.from(response.getUser().getRole())
        );

        return new Session(response.getAccessToken(), response.getRefreshToken(), user);
    }
}
