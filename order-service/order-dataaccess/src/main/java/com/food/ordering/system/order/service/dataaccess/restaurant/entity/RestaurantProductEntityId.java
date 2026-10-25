package com.food.ordering.system.order.service.dataaccess.restaurant.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantProductEntityId implements Serializable {

    private UUID restaurantId;
    private UUID productId;


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof RestaurantProductEntityId that)) return false;

        return getRestaurantId().equals(that.getRestaurantId()) && getProductId().equals(that.getProductId());
    }

    @Override
    public int hashCode() {
        int result = getRestaurantId().hashCode();
        result = 31 * result + getProductId().hashCode();
        return result;
    }
}
