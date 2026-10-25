package com.food.ordering.system.order.service.dataaccess.restaurant.adapter;

import com.food.ordering.system.order.service.dataaccess.restaurant.entity.RestaurantProductViewEntity;
import com.food.ordering.system.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.food.ordering.system.order.service.domain.port.output.repository.RestaurantRepository;
import com.food.ordering.system.order.services.domain.entity.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RestaurantRepositoryAdapter implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;


    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {

        List<UUID> restaurantProducts = restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);

        Optional<List<RestaurantProductViewEntity>> restaurantProductViewEntities =
        restaurantJpaRepository.findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(),
                restaurantProducts);

       return restaurantProductViewEntities.map(restaurantDataAccessMapper::restaurantEntityToRestaurant);

    }
}
