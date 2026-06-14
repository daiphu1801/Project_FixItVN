package com.fixit.feature.customer.workerprofile.data.remote.mapper;

import com.fixit.feature.customer.workerprofile.data.remote.dto.response.PublicWorkerProfileResponse;
import com.fixit.feature.customer.workerprofile.data.remote.dto.response.PublicWorkerSkillResponse;
import com.fixit.feature.customer.workerprofile.domain.model.PublicWorkerProfile;
import com.fixit.feature.customer.workerprofile.domain.model.PublicWorkerSkill;

import java.util.ArrayList;
import java.util.List;

public class PublicWorkerMapper {

    public static PublicWorkerProfile toDomain(PublicWorkerProfileResponse response) {
        if (response == null) return null;
        return new PublicWorkerProfile(
                response.getWorkerId(),
                response.getFullName(),
                response.getAvatarUrl(),
                response.getReputationScore(),
                response.getTotalReviews(),
                response.getExperienceDescription(),
                response.getServiceArea(),
                response.getAvailable()
        );
    }

    public static PublicWorkerSkill toDomain(PublicWorkerSkillResponse response) {
        if (response == null) return null;
        return new PublicWorkerSkill(
                response.getServiceId(),
                response.getServiceName(),
                response.getIconUrl(),
                response.getBasePrice()
        );
    }

    public static List<PublicWorkerSkill> toSkillDomainList(List<PublicWorkerSkillResponse> responses) {
        List<PublicWorkerSkill> result = new ArrayList<>();
        if (responses != null) {
            for (PublicWorkerSkillResponse r : responses) {
                result.add(toDomain(r));
            }
        }
        return result;
    }
}
