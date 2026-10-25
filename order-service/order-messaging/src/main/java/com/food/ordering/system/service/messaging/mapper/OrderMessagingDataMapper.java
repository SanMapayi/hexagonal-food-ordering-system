package com.food.ordering.system.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.*;
import com.food.ordering.system.order.services.domain.entity.Order;
import com.food.ordering.system.order.services.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.services.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.services.domain.event.OrderPaidEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

    public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {

        Order order = orderCreatedEvent.getOrder();
        return PaymentRequestAvroModel
                .newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.fromString(""))
                .setCustomerId(order.getCustomerId().getValue())
                .setOrderId(order.getId().getValue())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();

    }


    public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {

        Order order = orderCancelledEvent.getOrder();
        return PaymentRequestAvroModel
                .newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.fromString(""))
                .setCustomerId(order.getCustomerId().getValue())
                .setOrderId(order.getId().getValue())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
                .build();

    }

    public RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantRequestApprovalModel(OrderPaidEvent orderPaidEvent) {
        Order order = orderPaidEvent.getOrder();
        return RestaurantApprovalRequestAvroModel
                .newBuilder()
                .setId(UUID.randomUUID())
                .setSagaId(UUID.fromString(""))
                .setOrderId(order.getId().getValue())
                .setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
                .setRestaurantId(order.getRestaurantId().getValue())
                //.setRestaurantOrderStatus(RestaurantOrderStatus.valueOf(order.getOrderStatus().toString()))
                .setProducts(order.getItems().stream()
                        .map(orderItem -> {
                           return Product.newBuilder().setId(orderItem.getProduct().getId().getValue().toString())
                                   .setQuantity(orderItem.getQuantity())
                                   .build();
                        })
                        .collect(Collectors.toList()))
                .setPrice(order.getPrice().getAmount())
                .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
                .build();

    }

}
