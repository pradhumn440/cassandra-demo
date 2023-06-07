package com.springcassandra.cassandrademo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public NewTopic cassandraTopic() {
        return TopicBuilder.name("cassandra_topic")
                .build();

    }
}
