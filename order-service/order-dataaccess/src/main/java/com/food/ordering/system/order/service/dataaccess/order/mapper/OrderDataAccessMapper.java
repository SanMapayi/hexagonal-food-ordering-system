package com.food.ordering.system.order.service.dataaccess.order.mapper;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import com.food.ordering.system.order.services.domain.entity.Order;
import com.food.ordering.system.order.services.domain.entity.OrderItem;
import com.food.ordering.system.order.services.domain.entity.Product;
import com.food.ordering.system.order.services.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.services.domain.valueobject.TrackingId;
import com.food.ordering.system.order.services.domain.valueobject.StreetAddress;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrderDataAccessMapper {

    @Mapping(source = "id.value", target = "uuid")
    @Mapping(source = "customerId.value", target = "customerId")
    @Mapping(source = "restaurantId.value", target = "restaurantId")
    @Mapping(source = "trackingId.value", target = "trackingId")
    @Mapping(source = "price.amount", target = "price")
    @Mapping(source = "deliveryAddress", target = "address")
    @Mapping(
            source = "failureMessages",
            target = "failureMessages",
            qualifiedByName = "joinFailureMessages"
    )
    OrderEntity orderToOrderEntity(Order order);

    @Mapping(source = "postalCode", target = "postcode")
    @Mapping(target = "order", ignore = true)
    OrderAddressEntity streetAddressToOrderAddressEntity(
            StreetAddress address
    );

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "product.id.value", target = "productId")
    @Mapping(source = "price.amount", target = "price")
    @Mapping(source = "subTotal.amount", target = "subTotal")
    @Mapping(target = "order", ignore = true)
    OrderItemEntity orderItemToOrderItemEntity(OrderItem item);

    @BeanMapping(builder = @Builder(disableBuilder = false))
    @Mapping(source = "uuid", target = "orderId")
    @Mapping(source = "address", target = "deliveryAddress")
    @Mapping(source = "failureMessages", target = "failureMessages",
            qualifiedByName = "splitFailureMessages")
    // Preferences are not currently persisted by OrderEntity.
    @Mapping(target = "orderPreferences", ignore = true)
    Order orderEntityToOrder(OrderEntity entity);

    @BeanMapping(builder = @Builder(disableBuilder = false))
    @Mapping(source = "postcode", target = "postalCode")
    StreetAddress orderAddressEntityToStreetAddress(OrderAddressEntity entity);

    @BeanMapping(builder = @Builder(disableBuilder = false))
    @Mapping(source = "order.uuid", target = "orderId")
    @Mapping(source = "productId", target = "product")
    OrderItem orderItemEntityToOrderItem(OrderItemEntity entity);

    default OrderId toOrderId(UUID id) {
        return id == null ? null : new OrderId(id);
    }

    default CustomerId toCustomerId(UUID id) {
        return id == null ? null : new CustomerId(id);
    }

    default RestaurantId toRestaurantId(UUID id) {
        return id == null ? null : new RestaurantId(id);
    }

    default TrackingId toTrackingId(UUID id) {
        return id == null ? null : new TrackingId(id);
    }

    default OrderItemId toOrderItemId(Long id) {
        return id == null ? null : new OrderItemId(id);
    }

    default Money toMoney(BigDecimal amount) {
        return amount == null ? null : new Money(amount);
    }

    default Product toProduct(UUID id) {
        // Only the product ID is stored with an order item.
        return id == null ? null : new Product(new ProductId(id));
    }

    @Named("splitFailureMessages")
    default List<String> splitFailureMessages(String messages) {
        if (messages == null) {
            return null;
        }
        return messages.isEmpty() ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(messages.split(",", -1)));
    }

    @Named("joinFailureMessages")
    default String joinFailureMessages(List<String> messages) {
        return messages == null ? null : String.join(",", messages);
    }

    @AfterMapping
    default void linkOrderRelationships(
            @MappingTarget OrderEntity orderEntity
    ) {
        if (orderEntity.getAddress() != null) {
            orderEntity.getAddress().setOrder(orderEntity);
        }

        if (orderEntity.getItems() != null) {
            for (OrderItemEntity item : orderEntity.getItems()) {
                if (item != null) {
                    item.setOrder(orderEntity);
                }
            }
        }
    }
}
