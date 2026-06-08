package com.fixit.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp_codes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone_number", length = 100)
    private String phoneNumber;

    @Column(name = "otp_code", length = 6)
    private String otpCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 50)
    private OtpActionType actionType;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Builder.Default
    @Column(name = "is_used")
    private Boolean used = false;
}
