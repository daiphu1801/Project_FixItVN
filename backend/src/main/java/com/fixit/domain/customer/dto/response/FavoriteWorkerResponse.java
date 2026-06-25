package com.fixit.domain.customer.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteWorkerResponse {
    private UUID workerId;
    private String fullName;
    private String avatarUrl;
    private BigDecimal rating;
    private List<String> skills;
    private Boolean available;
}
