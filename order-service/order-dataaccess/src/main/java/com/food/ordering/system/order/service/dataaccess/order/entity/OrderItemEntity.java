package com.food.ordering.system.order.service.dataaccess.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(OrderItemEntityId.class)
@Table(name = "oder_items")
public class OrderItemEntity {
    @Id
    private Long id;

    @Id
    @JoinColumn(name = "ORDER_ID")
    @ManyToOne(cascade = CascadeType.ALL)
    private OrderEntity order;

    private UUID productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subTotal;


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof OrderItemEntity that)) return false;

        return getId().equals(that.getId()) && getOrder().equals(that.getOrder());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getOrder().hashCode();
        return result;
    }
}
