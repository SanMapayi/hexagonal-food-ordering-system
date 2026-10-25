package com.food.ordering.system.order.service.dataaccess.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RestaurantProductEntityId.class)
@Table(name = "order_restaurant_m_view", schema ="restaurant")
public class RestaurantProductViewEntity {
    @Id
    private UUID restaurantId;
    @Id
    private UUID productId;
    private String restaurantName;
    private boolean restaurantActive;
    private String productName;
    private BigDecimal productPrice;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof RestaurantProductViewEntity that)) return false;

        return getRestaurantId().equals(that.getRestaurantId()) && getProductId().equals(that.getProductId());
    }

    @Override
    public int hashCode() {
        int result = getRestaurantId().hashCode();
        result = 31 * result + getProductId().hashCode();
        return result;
    }
}
