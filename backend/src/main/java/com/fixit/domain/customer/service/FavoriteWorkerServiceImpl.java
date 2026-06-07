package com.fixit.domain.customer.service;

import com.fixit.domain.auth.entity.User;
import com.fixit.domain.auth.entity.UserRole;
import com.fixit.domain.customer.dto.response.FavoriteWorkerResponse;
import com.fixit.domain.customer.entity.Customer;
import com.fixit.domain.customer.entity.FavoriteWorker;
import com.fixit.domain.customer.entity.FavoriteWorkerId;
import com.fixit.domain.customer.repository.CustomerRepository;
import com.fixit.domain.customer.repository.FavoriteWorkerRepository;
import com.fixit.domain.worker.entity.Worker;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.domain.worker.repository.WorkerServiceRepository;
import com.fixit.global.exception.AppException;
import com.fixit.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteWorkerServiceImpl implements FavoriteWorkerService {

    private final FavoriteWorkerRepository favoriteWorkerRepository;
    private final CustomerRepository customerRepository;
    private final WorkerRepository workerRepository;
    private final WorkerServiceRepository workerServiceRepository;

    private UUID getCurrentCustomerId() {
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

        if (user.getRole() != UserRole.Customer) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        return user.getId();
    }

    @Override
    @Transactional
    public void addFavorite(UUID workerId) {
        UUID customerId = getCurrentCustomerId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKER_NOT_FOUND));

        FavoriteWorkerId id = new FavoriteWorkerId(customerId, workerId);

        if (favoriteWorkerRepository.existsById(id)) {
            return;
        }

        FavoriteWorker favoriteWorker = FavoriteWorker.builder()
                .id(id)
                .customer(customer)
                .worker(worker)
                .build();

        favoriteWorkerRepository.save(favoriteWorker);
    }

    @Override
    @Transactional
    public void removeFavorite(UUID workerId) {
        UUID customerId = getCurrentCustomerId();
        FavoriteWorkerId id = new FavoriteWorkerId(customerId, workerId);

        if (favoriteWorkerRepository.existsById(id)) {
            favoriteWorkerRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(UUID workerId) {
        UUID customerId = getCurrentCustomerId();
        FavoriteWorkerId id = new FavoriteWorkerId(customerId, workerId);
        return favoriteWorkerRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteWorkerResponse> getFavorites() {
        UUID customerId = getCurrentCustomerId();
        List<FavoriteWorker> favorites = favoriteWorkerRepository.findAllByCustomerId(customerId);

        return favorites.stream().map(fw -> {
            Worker worker = fw.getWorker();
            User user = worker.getUser();

            List<String> skills = workerServiceRepository.findByWorker_WorkerId(worker.getWorkerId())
                    .stream()
                    .map(ws -> ws.getServiceCategory().getServiceName())
                    .toList();

            return FavoriteWorkerResponse.builder()
                    .workerId(worker.getWorkerId())
                    .fullName(worker.getFullName())
                    .avatarUrl(user != null ? user.getAvatarUrl() : null)
                    .rating(worker.getReputationScore())
                    .skills(skills)
                    .available(worker.getAvailable())
                    .build();
        }).toList();
    }
}
