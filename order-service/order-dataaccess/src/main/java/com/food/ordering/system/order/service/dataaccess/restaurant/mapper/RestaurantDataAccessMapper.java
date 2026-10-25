package com.food.ordering.system.order.service.dataaccess.restaurant.mapper;


import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.dataaccess.restaurant.entity.RestaurantProductViewEntity;
import com.food.ordering.system.order.service.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.food.ordering.system.order.services.domain.entity.Product;
import com.food.ordering.system.order.services.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class RestaurantDataAccessMapper {


    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map( p -> p.getId().getValue())
                .collect(Collectors.toList());
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantProductViewEntity> restaurantProductViewEntities) {
        RestaurantProductViewEntity restaurantProductViewEntity = restaurantProductViewEntities
                .stream().findFirst().orElseThrow(
                        () -> new RestaurantDataAccessException("Restaurant could not be found")
                );

        return Restaurant.builder()
                .restaurantId(new RestaurantId(restaurantProductViewEntity.getRestaurantId()))
                .products(this.mapRestaurantAndProductEntitiesToProducts(restaurantProductViewEntities))
                .active(restaurantProductViewEntity.isRestaurantActive())
                .build();
    }

    private List<Product> mapRestaurantAndProductEntitiesToProducts(List<RestaurantProductViewEntity> restaurantProductViewEntities) {
        return restaurantProductViewEntities
                .stream().map(
                        restaurantProductViewEntity ->
                                new Product(new ProductId(restaurantProductViewEntity.getProductId()),
                                        restaurantProductViewEntity.getProductName(), new Money(restaurantProductViewEntity.getProductPrice()))
                ).collect(Collectors.toList());
    }

}
