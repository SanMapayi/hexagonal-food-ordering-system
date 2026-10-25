package com.food.ordering.system.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderApplicationServiceConfig;
import com.food.ordering.system.order.service.domain.port.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.order.services.domain.event.OrderPaidEvent;
import com.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderApplicationServiceConfig orderApplicationServiceConfig;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final OrderKafkaMessageHelper orderKafkaMessageHelper;

    @Override
    public void publish(OrderPaidEvent orderPaidEvent) {
        String orderId = orderPaidEvent.getOrder().getId().getValue().toString();
        log.info("Receive orderPaidEvent with order id {}", orderId);

        try {
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel = orderMessagingDataMapper
                    .orderPaidEventToRestaurantRequestApprovalModel(orderPaidEvent);
            log.info("The order was payment will be published as {}", orderPaidEvent.getOrder().getOrderStatus().toString());
            kafkaProducer.send(
                    orderApplicationServiceConfig.getRestaurantApprovalRequestTopicName(), orderId, restaurantApprovalRequestAvroModel
            ). whenComplete(
                    (result, throwable) -> {
                        orderKafkaMessageHelper.getKafkaCallback(
                            result, restaurantApprovalRequestAvroModel, orderApplicationServiceConfig.getRestaurantApprovalRequestTopicName(),  orderId, "RestaurantApprovalRequestAvroModel", throwable
                        );
                    }
            );
            log.info("restaurantApprovalRequestAvroModel sent to kafka for order id {}", restaurantApprovalRequestAvroModel.getOrderId());
        } catch (Exception e) {
            log.info("Error while sending the restaurantApprovalRequestAvroModel");
        }

    }
}
