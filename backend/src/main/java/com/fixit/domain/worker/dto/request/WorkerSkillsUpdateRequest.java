package com.fixit.domain.worker.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkerSkillsUpdateRequest {

    @NotNull(message = "skills không được để trống")
    @Valid
    private List<WorkerSkillUpsertItemRequest> skills;
}