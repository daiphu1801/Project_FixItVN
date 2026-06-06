package com.fixit.domain.auth.service;

import com.fixit.domain.auth.entity.User;
import com.fixit.domain.auth.repository.UserRepository;
import com.fixit.domain.upload.entity.UploadLinkedEntityType;
import com.fixit.domain.upload.entity.UploadPurpose;
import com.fixit.domain.upload.service.ConsumedUpload;
import com.fixit.domain.upload.service.UploadConsumeService;
import com.fixit.domain.auth.dto.request.UserAvatarUpdateRequest;
import com.fixit.domain.auth.dto.response.UserMeResponse;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import com.fixit.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserMeServiceImpl implements UserMeService {

    private final UserRepository userRepository;
    private final UploadConsumeService uploadConsumeService;

    @Override
    @Transactional(readOnly = true)
    public UserMeResponse getMe() {
        User currentUser = getCurrentUser();
        return toResponse(currentUser);
    }

    @Override
    @Transactional
    public UserMeResponse updateAvatar(UserAvatarUpdateRequest request) {
        User currentUser = getCurrentUser();

        ConsumedUpload avatarUpload = uploadConsumeService.consume(
                request.getUploadId(),
                currentUser.getId(),
                UploadPurpose.AVATAR,
                UploadLinkedEntityType.USER_AVATAR,
                currentUser.getId()
        );

        currentUser.setAvatarUrl(avatarUpload.getFileUrl());
        User saved = userRepository.save(currentUser);

        return toResponse(saved);
    }

    private User getCurrentUser() {
        String phoneNumber = SecurityUtil.getCurrentUserPhone();

        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private UserMeResponse toResponse(User user) {
        return UserMeResponse.builder()
                .userId(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .avatarUrl(user.getAvatarUrl())
                .active(user.getActive())
                .build();
    }
}