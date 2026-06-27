package com.fixit.global.util;

import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    private SecurityUtil() {
        // Prevent instantiation
    }

    /**
     * Gets the phone number (username) of the currently authenticated user.
     *
     * @return User's phone number
     * @throws AppException if no user is authenticated
     */
    public static String getCurrentUserPhone() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }

        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    /**
     * Gets the UUID of the currently authenticated user.
     *
     * @return User's UUID
     * @throws AppException if no user is authenticated
     */
    public static java.util.UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof com.fixit.domain.auth.entity.User) {
            return ((com.fixit.domain.auth.entity.User) principal).getId();
        }

        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
