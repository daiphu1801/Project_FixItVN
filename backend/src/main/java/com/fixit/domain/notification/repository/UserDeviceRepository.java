package com.fixit.domain.notification.repository;

import com.fixit.domain.notification.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {
    
    Optional<UserDevice> findByDeviceToken(String deviceToken);
    
    java.util.List<UserDevice> findByUserId(UUID userId);
    
    void deleteByUserIdAndDeviceToken(UUID userId, String deviceToken);
}
