package com.fixit.domain.auth.repository;

import com.fixit.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findTopByUserIdAndRevokedFalseOrderByCreatedAtDesc(UUID userId);

    @Modifying
    @Query("""
            UPDATE RefreshToken r
            SET r.revoked = true
            WHERE r.user.id = :userId
              AND r.revoked = false
            """)
    void revokeAllActiveTokensByUserId(@Param("userId") UUID userId);
}