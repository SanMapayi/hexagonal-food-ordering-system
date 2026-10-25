package com.food.ordering.system.order.service.dataaccess.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_address")
public class OrderAddressEntity {
    @Id
    private UUID id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_ID")
    private OrderEntity order;

    private String street;
    private String postcode;
    private String city;


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof OrderAddressEntity that)) return false;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
