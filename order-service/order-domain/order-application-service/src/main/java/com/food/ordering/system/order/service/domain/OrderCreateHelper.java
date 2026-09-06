package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.port.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.port.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.port.output.repository.RestaurantRepository;
import com.food.ordering.system.order.services.domain.OrderDomainService;
import com.food.ordering.system.order.services.domain.entity.Customer;
import com.food.ordering.system.order.services.domain.entity.Order;
import com.food.ordering.system.order.services.domain.entity.Restaurant;
import com.food.ordering.system.order.services.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.services.domain.exception.OrderDomainException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreateHelper {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;

    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
        saveOrder(order);
        log.info("Order event has been created with Order at: {} ", orderCreatedEvent.getCreatedAt());
        return orderCreatedEvent;
    }


    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.CreateOrderCommandToRestaurant(createOrderCommand);
        Optional<Restaurant> optionalRestaurant =  restaurantRepository.findRestaurantInformation(restaurant);
        if (optionalRestaurant.isEmpty()) {
            log.warn("Could not find a restaurant with id: {}", createOrderCommand.getRestaurantId());
            throw new OrderDomainException("Could not find restaurant with id: " + createOrderCommand.getCustomerId());
        }
        return optionalRestaurant.get();
    }


    private void checkCustomer(@NotNull UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        customer.orElseThrow(() -> {
            log.warn("Could not find customer with customer id {}", customerId);
            return new OrderDomainException("");
        });
    }

    private Order saveOrder(Order order) {
        Order orderResult = orderRepository.save(order);
        if (orderResult == null) {
            log.error("Database issue order cannot be saved with id: {}", order.getId().getValue());
            throw new OrderDomainException("Order with order id: " + order.getId().getValue() + " Cannot be saved");
        }
        log.info("Order is saved with id: {}", orderResult.getId().getValue());
        return orderResult;
    }
}
