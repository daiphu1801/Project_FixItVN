package com.fixit.feature.worker.profile.data.remote.mapper;

import com.fixit.feature.worker.profile.data.remote.dto.response.WorkerSkillResponse;
import com.fixit.feature.worker.profile.data.remote.dto.request.WorkerSkillUpsertItemRequest;
import com.fixit.feature.worker.profile.domain.model.WorkerSkill;
import com.fixit.feature.worker.profile.presentation.SpecializationItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WorkerSkillMapper {

    private WorkerSkillMapper() {
    }

    public static WorkerSkill toDomain(WorkerSkillResponse response) {
        if (response == null) {
            return null;
        }

        return new WorkerSkill(
                response.getServiceId() == null ? 0 : response.getServiceId(),
                response.getServiceName() == null ? "" : response.getServiceName(),
                response.getBasePrice() == null ? 0.0 : response.getBasePrice().doubleValue()
        );
    }

    public static List<WorkerSkill> toDomainList(List<WorkerSkillResponse> responses) {
        List<WorkerSkill> result = new ArrayList<>();

        if (responses == null) {
            return result;
        }

        for (WorkerSkillResponse response : responses) {
            WorkerSkill skill = toDomain(response);
            if (skill != null) {
                result.add(skill);
            }
        }

        return result;
    }

    public static WorkerSkillUpsertItemRequest toRequestItem(WorkerSkill skill) {
        return new WorkerSkillUpsertItemRequest(
                skill.getServiceId(),
                BigDecimal.valueOf(skill.getBasePrice())
        );
    }

    public static List<WorkerSkillUpsertItemRequest> toRequestItems(List<WorkerSkill> skills) {
        List<WorkerSkillUpsertItemRequest> result = new ArrayList<>();

        if (skills == null) {
            return result;
        }

        for (WorkerSkill skill : skills) {
            result.add(toRequestItem(skill));
        }

        return result;
    }

    public static SpecializationItem toPresentationItem(WorkerSkill skill) {
        return new SpecializationItem(
                skill.getServiceId(),
                skill.getServiceName(),
                true,
                skill.getBasePrice()
        );
    }

    public static WorkerSkill fromPresentationItem(SpecializationItem item) {
        return new WorkerSkill(
                item.getId(),
                item.getName(),
                item.getBasePrice() == null ? 0.0 : item.getBasePrice()
        );
    }
}