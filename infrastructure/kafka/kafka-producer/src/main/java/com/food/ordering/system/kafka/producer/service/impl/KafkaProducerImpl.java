package com.food.ordering.system.kafka.producer.service.impl;

import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;




    @Override
    public CompletableFuture<SendResult<K, V>> send(String topicName, K key, V message) {
        log.info("sending message={} to topic={}", message, topicName);
        try {
          return  kafkaTemplate
                    .send(topicName, key, message);

        } catch (KafkaException e) {
            log.error("Error on kafka producer with key: {}, message: {} and exception: {}", key, message, e.getMessage());
            throw new KafkaException("Error on Kafka producer with Key: " + key + " and message: " + message);
        }

    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Closing the Kafka producer");
            kafkaTemplate.destroy();
        }
    }
}
