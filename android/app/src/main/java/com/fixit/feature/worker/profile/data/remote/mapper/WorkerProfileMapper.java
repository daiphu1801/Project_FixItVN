package com.fixit.feature.worker.profile.data.remote.mapper;

import com.fixit.feature.worker.profile.data.remote.dto.response.WorkerProfileResponse;
import com.fixit.feature.worker.profile.data.remote.dto.request.WorkerProfileUpdateRequest;
import com.fixit.feature.worker.profile.domain.model.WorkerProfile;
import com.fixit.feature.worker.profile.domain.model.WorkerProfileUpdateInput;

import java.math.BigDecimal;

public class WorkerProfileMapper {

    private WorkerProfileMapper() {
    }

    public static WorkerProfile toDomain(WorkerProfileResponse response) {
        if (response == null) {
            return null;
        }

        return new WorkerProfile(
                response.getWorkerId(),
                safeText(response.getFullName(), "Thợ FixIt"),
                safeText(response.getPhoneNumber(), ""),
                safeText(response.getEmail(), ""),
                response.getAvatarUrl(),
                safeText(response.getIdentityCard(), ""),
                safeText(response.getVerificationStatus(), ""),
                Boolean.TRUE.equals(response.getAvailable()),
                decimalToDouble(response.getReputationScore()),
                safeText(response.getExperienceDescription(), ""),
                safeText(response.getServiceArea(), "")
        );
    }

    public static WorkerProfileUpdateRequest toRequest(WorkerProfileUpdateInput input) {
        return new WorkerProfileUpdateRequest(
                trimToNull(input.getFullName()),
                trimToNull(input.getEmail()),
                null,
                trimToNull(input.getExperienceDescription()),
                trimToNull(input.getServiceArea())
        );
    }

    private static String safeText(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static double decimalToDouble(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }
}
