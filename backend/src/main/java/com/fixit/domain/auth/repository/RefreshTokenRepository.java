package com.fixit.domain.auth.repository;

import com.fixit.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    
    @Query("SELECT r FROM RefreshToken r WHERE r.user.id = :userId AND r.revoked = false")
    List<RefreshToken> findAllValidTokenByUserId(UUID userId);
    
    @Query("SELECT r FROM RefreshToken r WHERE r.user.id = :userId")
    List<RefreshToken> findAllByUserId(UUID userId);
    
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.user.id = :userId")
    void deleteByUserId(UUID userId);
}
