package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.port.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.services.domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCreatedCommandHandler {


    private final OrderDataMapper orderDataMapper;
    private final OrderCreateHelper orderCreatedHelper;
    private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;

    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {

        OrderCreatedEvent orderCreatedEvent = orderCreatedHelper.persistOrder(createOrderCommand);
        log.info("Order has been created with Order id: {} ", orderCreatedEvent.getOrder().getId().getValue());
        orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);

        return orderDataMapper.OrderToCreatedOrderResponse(orderCreatedEvent.getOrder(), "Order Created Successfully");
    }


}
