package com.fixit.domain.worker.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerSkillsResponse {

    private UUID workerId;

    private Integer totalItems;

    private List<WorkerSkillResponse> skills;
}