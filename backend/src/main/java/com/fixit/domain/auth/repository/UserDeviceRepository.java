package com.fixit.domain.auth.repository;

import com.fixit.domain.auth.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {
    
    Optional<UserDevice> findByDeviceToken(String deviceToken);
    
    Optional<UserDevice> findByUserIdAndDeviceToken(UUID userId, String deviceToken);
    
    void deleteByDeviceToken(String deviceToken);
    
    void deleteByUserIdAndDeviceToken(UUID userId, String deviceToken);
}
