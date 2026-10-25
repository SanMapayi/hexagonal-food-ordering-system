package com.food.ordering.system.kafka.consumer.exception;

public class KafkaConsumerException extends RuntimeException {

    public KafkaConsumerException(String message) {
        super(message);
    }
}
