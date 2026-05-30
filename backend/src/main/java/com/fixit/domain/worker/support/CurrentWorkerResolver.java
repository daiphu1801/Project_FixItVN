package com.fixit.domain.worker.support;

import com.fixit.domain.auth.entity.User;
import com.fixit.domain.auth.entity.UserRole;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentWorkerResolver {

    public UUID getCurrentWorkerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User user)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (user.getRole() != UserRole.Worker) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        return user.getId();
    }
}