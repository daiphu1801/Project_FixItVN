package com.fixit.domain.customer.entity;

import com.fixit.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @Column(name = "customer_id")
    private UUID customerId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "customer_id")
    private User user;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "dob", length = 20)
    private String dob;
}
