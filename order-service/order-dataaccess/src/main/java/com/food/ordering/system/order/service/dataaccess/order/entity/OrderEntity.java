package com.food.ordering.system.order.service.dataaccess.order.entity;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID uuid;
    private UUID customerId;
    private UUID restaurantId;
    private UUID trackingId;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private String failureMessages;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private OrderAddressEntity address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderItemEntity> items;


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof OrderEntity that)) return false;

        return getUuid().equals(that.getUuid());
    }

    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }
}
