package com.fixit.domain.worker.service;

import com.fixit.domain.auth.entity.User;
import com.fixit.domain.service_categories.entity.ServiceCategory;
import com.fixit.domain.service_categories.repository.ServiceCategoryRepository;
import com.fixit.domain.worker.dto.request.WorkerProfileUpdateRequest;
import com.fixit.domain.worker.dto.request.WorkerSkillUpsertItemRequest;
import com.fixit.domain.worker.dto.request.WorkerSkillsUpdateRequest;
import com.fixit.domain.worker.dto.response.WorkerProfileResponse;
import com.fixit.domain.worker.dto.response.WorkerSkillResponse;
import com.fixit.domain.worker.dto.response.WorkerSkillsResponse;
import com.fixit.domain.worker.entity.Worker;
import com.fixit.domain.worker.entity.WorkerService;
import com.fixit.domain.worker.entity.WorkerServiceId;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.domain.worker.repository.WorkerServiceRepository;
import com.fixit.domain.worker.support.CurrentWorkerResolver;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkerProfileServiceImpl implements WorkerProfileService {

    private final CurrentWorkerResolver currentWorkerResolver;
    private final WorkerRepository workerRepository;
    private final WorkerServiceRepository workerServiceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public WorkerProfileResponse getMyProfile() {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        Worker worker = getWorker(workerId);

        return toProfileResponse(worker);
    }

    @Override
    @Transactional
    public WorkerProfileResponse updateMyProfile(WorkerProfileUpdateRequest request) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        Worker worker = getWorker(workerId);

        if (request.getFullName() != null) {
            worker.setFullName(trimToNull(request.getFullName()));
        }

        if (request.getExperienceDescription() != null) {
            worker.setExperienceDescription(trimToNull(request.getExperienceDescription()));
        }

        if (request.getServiceArea() != null) {
            worker.setServiceArea(trimToNull(request.getServiceArea()));
        }

        User user = worker.getUser();

        if (user != null) {
            if (request.getEmail() != null) {
                user.setEmail(trimToNull(request.getEmail()));
            }

            // Avatar is updated only through /api/v1/users/me/avatar with an uploadId.
        }

        return toProfileResponse(worker);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerSkillsResponse getMySkills() {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();

        List<WorkerSkillResponse> skills = workerServiceRepository
                .findByWorker_WorkerId(workerId)
                .stream()
                .map(this::toSkillResponse)
                .toList();

        return WorkerSkillsResponse.builder()
                .workerId(workerId)
                .totalItems(skills.size())
                .skills(skills)
                .build();
    }

    @Override
    @Transactional
    public WorkerSkillsResponse updateMySkills(WorkerSkillsUpdateRequest request) {
        UUID workerId = currentWorkerResolver.getCurrentWorkerId();
        Worker worker = getWorker(workerId);

        List<WorkerSkillUpsertItemRequest> requestedSkills =
                request.getSkills() == null ? Collections.emptyList() : request.getSkills();

        validateNoDuplicatedServiceIds(requestedSkills);

        List<Integer> serviceIds = requestedSkills.stream()
                .map(WorkerSkillUpsertItemRequest::getServiceId)
                .toList();

        Map<Integer, ServiceCategory> serviceCategoryMap = serviceCategoryRepository
                .findAllById(serviceIds)
                .stream()
                .collect(Collectors.toMap(ServiceCategory::getId, Function.identity()));

        validateAllServiceCategoriesExist(serviceIds, serviceCategoryMap);

        /*
         * PUT semantics:
         * Xóa toàn bộ skill cũ rồi ghi lại skill mới.
         */
        workerServiceRepository.deleteByWorker_WorkerId(workerId);

        List<WorkerService> newSkills = requestedSkills.stream()
                .map(item -> {
                    ServiceCategory serviceCategory = serviceCategoryMap.get(item.getServiceId());

                    return WorkerService.builder()
                            .id(new WorkerServiceId(workerId, item.getServiceId()))
                            .worker(worker)
                            .serviceCategory(serviceCategory)
                            .basePrice(item.getBasePrice())
                            .build();
                })
                .toList();

        workerServiceRepository.saveAll(newSkills);

        List<WorkerSkillResponse> responseSkills = newSkills.stream()
                .map(this::toSkillResponse)
                .toList();

        return WorkerSkillsResponse.builder()
                .workerId(workerId)
                .totalItems(responseSkills.size())
                .skills(responseSkills)
                .build();
    }

    private Worker getWorker(UUID workerId) {
        return workerRepository.findById(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_NOT_FOUND));
    }

    private WorkerProfileResponse toProfileResponse(Worker worker) {
        User user = worker.getUser();

        return WorkerProfileResponse.builder()
                .workerId(worker.getWorkerId())
                .fullName(worker.getFullName())
                .phoneNumber(user != null ? user.getPhoneNumber() : null)
                .email(user != null ? user.getEmail() : null)
                .avatarUrl(user != null ? user.getAvatarUrl() : null)
                .identityCard(worker.getIdentityCard())
                .verificationStatus(worker.getVerificationStatus() != null
                        ? worker.getVerificationStatus().name().toUpperCase(Locale.ROOT)
                        : null)
                .available(worker.getAvailable())
                .reputationScore(worker.getReputationScore())
                .latitude(worker.getLatitude())
                .longitude(worker.getLongitude())
                .experienceDescription(worker.getExperienceDescription())
                .serviceArea(worker.getServiceArea())
                .build();
    }

    private WorkerSkillResponse toSkillResponse(WorkerService workerService) {
        ServiceCategory serviceCategory = workerService.getServiceCategory();

        return WorkerSkillResponse.builder()
                .serviceId(serviceCategory != null ? serviceCategory.getId() : null)
                .serviceName(serviceCategory != null ? serviceCategory.getServiceName() : null)
                .basePrice(workerService.getBasePrice())
                .build();
    }

    private void validateNoDuplicatedServiceIds(List<WorkerSkillUpsertItemRequest> skills) {
        Set<Integer> seen = new HashSet<>();

        for (WorkerSkillUpsertItemRequest item : skills) {
            if (item.getServiceId() == null) {
                continue;
            }

            if (!seen.add(item.getServiceId())) {
                throw new AppException(ErrorCode.WORKER_SKILL_DUPLICATED);
            }
        }
    }

    private void validateAllServiceCategoriesExist(
            List<Integer> requestedServiceIds,
            Map<Integer, ServiceCategory> serviceCategoryMap
    ) {
        for (Integer serviceId : requestedServiceIds) {
            if (!serviceCategoryMap.containsKey(serviceId)) {
                throw new AppException(ErrorCode.SERVICE_CATEGORY_NOT_FOUND);
            }
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
