package com.fixit.domain.auth.repository;

import com.fixit.domain.auth.entity.UserSocialLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSocialLoginRepository extends JpaRepository<UserSocialLogin, UUID> {
    Optional<UserSocialLogin> findByProviderAndProviderId(String provider, String providerId);
}
