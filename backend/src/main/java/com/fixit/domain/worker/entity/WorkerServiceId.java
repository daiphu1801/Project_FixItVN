package com.fixit.domain.worker.entity;

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
public class WorkerServiceId implements Serializable {

    @Column(name = "worker_id")
    private UUID workerId;

    @Column(name = "service_id")
    private Integer serviceId;
}
