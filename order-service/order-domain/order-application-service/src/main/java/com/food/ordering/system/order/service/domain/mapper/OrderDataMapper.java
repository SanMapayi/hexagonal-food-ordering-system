package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.services.domain.entity.Order;
import com.food.ordering.system.order.services.domain.entity.OrderItem;
import com.food.ordering.system.order.services.domain.entity.Product;
import com.food.ordering.system.order.services.domain.entity.Restaurant;
import com.food.ordering.system.order.services.domain.valueobject.StreetAddress;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public Restaurant CreateOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems().stream()
                        .map(orderItem -> new Product(
                                new ProductId(orderItem.getProductId())))
                        .collect(Collectors.toList()))
                .build();

    }

public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(OrderAddressToDeliveryAddress(createOrderCommand.getAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemEntities(
            @NotNull List<com.food.ordering.system.order.service.domain.dto.create.OrderItem> items) {
        return items.stream()
                .map(item -> OrderItemToOderItemEntity(item))
                .collect(Collectors.toList());
    }

    private OrderItem OrderItemToOderItemEntity(
            com.food.ordering.system.order.service.domain.dto.create.OrderItem item) {
        return OrderItem.builder()
                .product(new Product(new ProductId(item.getProductId())))
                .price(new Money(item.getPrice()))
                .quantity(item.getQuantity())
                .subTotal(new Money(item.getSubTotal()))
                .build();
    }

    private StreetAddress OrderAddressToDeliveryAddress(@NotNull OrderAddress address) {
        return StreetAddress
                .builder()
                .id(UUID.randomUUID())
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostCode()).build();
    }

    public CreateOrderResponse OrderToCreatedOrderResponse(Order order, String message) {
        return CreateOrderResponse
                .builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    // TRACKING DATA MAPPER

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse
                .builder()
                .orderStatus(order.getOrderStatus())
                .orderTrackingId(order.getTrackingId().getValue())
                .failureMessage(order.getFailureMessages())
                .build();
    }

}
