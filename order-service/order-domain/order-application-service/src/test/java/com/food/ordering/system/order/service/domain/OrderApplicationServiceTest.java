package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.port.input.service.OrderApplicationUseCase;
import com.food.ordering.system.order.service.domain.port.output.ai.order.noteinterpreter.OrderNoteInterpreter;
import com.food.ordering.system.order.service.domain.port.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.port.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.port.output.repository.RestaurantRepository;
import com.food.ordering.system.order.services.domain.entity.Customer;
import com.food.ordering.system.order.services.domain.entity.Order;
import com.food.ordering.system.order.services.domain.entity.Product;
import com.food.ordering.system.order.services.domain.entity.Restaurant;
import com.food.ordering.system.order.services.domain.exception.OrderDomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationUseCase orderApplicationUseCase;
    @Autowired
    private OrderDataMapper orderDataMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private OrderNoteInterpreter orderNoteInterpreter;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;
    private final UUID CUSTOMER_ID = UUID.fromString("fd0063fa-faee-4c01-8669-f7e0f68f51d4");
    private final UUID RESTAURANT_ID = UUID.fromString("a4742733-7884-4fe8-8ab1-c2e4eccf374a");
    private final UUID PRODUCT_ID = UUID.fromString("e8ede22c-12c0-47a4-85b3-2923c0772660");
    private final UUID ORDER_ID = UUID.fromString("4617f7e7-1ad9-45b0-9a67-2b5c804c5132");
    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeEach
    public void init() {

        String ORDER_NOTES = "no onions pls, with pickles, extra spicy but not too spicy. Leave it at the door";

        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("Street1")
                        .postCode("1000AB")
                        .city("Paris")
                        .build())
                .orderNotes(ORDER_NOTES)
                .price(PRICE)
                .items(
                        List.of(
                                OrderItem.builder().productId(PRODUCT_ID).quantity(1).price(new BigDecimal("50.00")).subTotal(new BigDecimal("50.00")).build(),
                                OrderItem.builder().productId(PRODUCT_ID).quantity(3).price(new BigDecimal("50.00")).subTotal(new BigDecimal("150.00")).build()
                        )
                ).build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("Street1")
                        .postCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("250.00"))
                .items(
                        List.of(
                                OrderItem.builder().productId(PRODUCT_ID).quantity(1).price(new BigDecimal("50.00")).subTotal(new BigDecimal("50.00")).build(),
                                OrderItem.builder().productId(PRODUCT_ID).quantity(3).price(new BigDecimal("50.00")).subTotal(new BigDecimal("150.00")).build()
                        )
                ).build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("Street1")
                        .postCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("210.00"))
                .items(
                        List.of(
                                OrderItem.builder().productId(PRODUCT_ID).quantity(1).price(new BigDecimal("60.00")).subTotal(new BigDecimal("60.00")).build(),
                                OrderItem.builder().productId(PRODUCT_ID).quantity(3).price(new BigDecimal("50.00")).subTotal(new BigDecimal("150.00")).build()
                        )
                ).build();

        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
                ))
                .active(true)
                .build();

        OrderPreferences orderPreferences = OrderPreferences.builder()
                .addIngredients(List.of("pickles"))
                .removeIngredients(List.of("onions"))
                .specialInstructions("Extra Spice")
                .spiceLevel(SpiceLevel.MEDIUM)
                .deliveryInstructions("Leave at the door")
                .build();


        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));
        order.updateOrderPreferences(orderPreferences);
        when(orderNoteInterpreter.interpret(ORDER_NOTES)).thenReturn(orderPreferences);



        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.CreateOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);



    }

    @Test
    public void testCreateOrder() {
       CreateOrderResponse createOrderResponse = orderApplicationUseCase.createOrder(createOrderCommand);
       assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
       assertEquals("Order Created Successfully", createOrderResponse.getMessage());
       assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test
    public void testCreatedOrderWithWrongTotalPrice() {
        OrderDomainException orderDomainException = assertThrows(
                OrderDomainException.class, () -> orderApplicationUseCase.createOrder(createOrderCommandWrongPrice)
        );

        assertEquals(orderDomainException.getMessage(), "Total price: 250.00 is not equal to order items total: 200.00!");
    }

    @Test
    public void testCreatedOrderWithWrongProductPrice() {
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationUseCase.createOrder(createOrderCommandWrongProductPrice));

        assertEquals(orderDomainException.getMessage(),
                "Order item price " + createOrderCommandWrongProductPrice.getItems().stream().findFirst().get().getPrice() + " is not valid for product " + PRODUCT_ID);
    }

    @Test
    public void testOrderWithPassiveRestaurant() {

        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
                ))
                .active(false)
                .build();

        when(restaurantRepository.findRestaurantInformation(orderDataMapper.CreateOrderCommandToRestaurant(createOrderCommand))).thenReturn(Optional.of(restaurantResponse));

        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationUseCase.createOrder(createOrderCommand));
        assertEquals( "Restaurant with id: " + RESTAURANT_ID + " is not active!", orderDomainException.getMessage());
    }



}
