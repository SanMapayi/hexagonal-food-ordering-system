package com.food.ordering.system.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderApplicationServiceConfig;
import com.food.ordering.system.order.service.domain.port.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.services.domain.event.OrderCreatedEvent;
import com.food.ordering.system.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderApplicationServiceConfig orderApplicationServiceConfig;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderKafkaMessageHelper orderKafkaMessageHelper;


    @Override
    public void publish(OrderCreatedEvent orderCreatedEvent) {
        String orderId = orderCreatedEvent.getOrder().getId().getValue().toString();
        log.info("Receive orderCreatedEvent with order id {}", orderId);
        try {
            PaymentRequestAvroModel paymentRequestAvroModel = orderMessagingDataMapper
                    .orderCreatedEventToPaymentRequestAvroModel(orderCreatedEvent);
            log.info("The order was payment will be published as {}", orderCreatedEvent.getOrder().getOrderStatus().toString());

            kafkaProducer.send(orderApplicationServiceConfig.getPaymentRequestTopicName(), orderId, paymentRequestAvroModel
                    ).whenComplete(
                    (result, throwable) -> {
                       orderKafkaMessageHelper.getKafkaCallback(result, paymentRequestAvroModel, orderApplicationServiceConfig.getPaymentRequestTopicName(), orderId, "PaymentRequestAvroModel", throwable);
                    }
            );

            log.info("PaymentRequestAvroModel sent to kafka for order id {}", paymentRequestAvroModel.getOrderId());
        } catch (Exception e) {
           log.info("Error while sending the paymentRequestAvroMoel");
        }

    }
}
