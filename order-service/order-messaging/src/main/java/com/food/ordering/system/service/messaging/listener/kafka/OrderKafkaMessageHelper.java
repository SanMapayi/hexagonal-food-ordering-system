package com.food.ordering.system.service.messaging.listener.kafka;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderKafkaMessageHelper {

    public <T> void getKafkaCallback(SendResult<String, T> requestAvroModelSendResult, T requestAvroModel, String responseTopicName, String orderId,
                                    String requestAvroModelName, Throwable throwable) {

        if (throwable != null) {
            //failure
            log.error(
                    "failed to send request {} to topic {} for model name {}", requestAvroModel.toString(), responseTopicName, requestAvroModelName, throwable
            );
        }
        else {
            //success
            RecordMetadata recordMetadata = requestAvroModelSendResult.getRecordMetadata();
            log.info("Received successful response from kafka with order id {} to the topic: {}" +
                            "with offset: {} " + "Partition: {} " + "Timestamp: {}",
                    orderId, recordMetadata.topic(), recordMetadata.offset(), recordMetadata.partition(), recordMetadata.timestamp());
        }
    }

}
