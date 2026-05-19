package com.fixit.domain.worker.entity;

import com.fixit.domain.service_categories.entity.ServiceCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "worker_services")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerService {

    @EmbeddedId
    private WorkerServiceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("workerId")
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private ServiceCategory serviceCategory;

    @Column(name = "base_price", precision = 12, scale = 2)
    private BigDecimal basePrice;
}
