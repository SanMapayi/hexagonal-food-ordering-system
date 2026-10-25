package com.food.ordering.system.order.service.dataaccess.order.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntityId implements Serializable {

    private Long id;
    private UUID order;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof OrderItemEntityId that)) return false;

        return getId().equals(that.getId()) && getOrder().equals(that.getOrder());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getOrder().hashCode();
        return result;
    }
}
