package com.fixit.domain.customer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FavoriteWorkerId implements Serializable {

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "worker_id")
    private UUID workerId;
}
