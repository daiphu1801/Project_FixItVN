package com.fixit.domain.auth.controller;

import com.fixit.domain.auth.dto.request.UserAvatarUpdateRequest;
import com.fixit.domain.auth.dto.response.UserMeResponse;
import com.fixit.domain.auth.service.UserMeService;
import com.fixit.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserMeController {

    private final UserMeService userMeService;

    @GetMapping
    public ApiResponse<UserMeResponse> getMe() {
        return ApiResponse.success(userMeService.getMe());
    }

    @PatchMapping("/avatar")
    public ApiResponse<UserMeResponse> updateAvatar(
            @Valid @RequestBody UserAvatarUpdateRequest request
    ) {
        UserMeResponse response = userMeService.updateAvatar(request);
        return ApiResponse.success(response, "Cập nhật avatar thành công");
    }
}