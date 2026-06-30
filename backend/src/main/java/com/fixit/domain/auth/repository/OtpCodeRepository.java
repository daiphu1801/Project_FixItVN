package com.fixit.domain.auth.repository;

import com.fixit.domain.auth.entity.OtpCode;
import com.fixit.domain.auth.entity.OtpActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, UUID> {
    
    Optional<OtpCode> findByPhoneNumberAndActionTypeAndUsedFalse(String phoneNumber, OtpActionType actionType);
    
    Optional<OtpCode> findByEmailAndActionTypeAndUsedFalse(String email, OtpActionType actionType);
    
    @Modifying
    @Query("DELETE FROM OtpCode o WHERE o.expiresAt < :now")
    void deleteByExpiresAtBefore(OffsetDateTime now);
}
