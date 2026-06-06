package com.fixit.domain.auth.service;

import com.fixit.domain.auth.dto.request.UserAvatarUpdateRequest;
import com.fixit.domain.auth.dto.response.UserMeResponse;

public interface UserMeService {

    UserMeResponse getMe();

    UserMeResponse updateAvatar(UserAvatarUpdateRequest request);
}