package com.food.ordering.system.kafka.consumer.service.impl;

import com.food.ordering.system.kafka.consumer.service.KafkaConsumer;
import org.apache.avro.specific.SpecificRecordBase;

import java.util.List;

public class KafkaConsumerImpl<T extends SpecificRecordBase> implements KafkaConsumer<T> {
    @Override
    public void receive(List<T> messages, List<Long> keys, List<Integer> partitions, List<Long> offsets) {

    }
}
