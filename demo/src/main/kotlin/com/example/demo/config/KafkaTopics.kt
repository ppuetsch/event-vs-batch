package com.example.demo.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopics {
    @Bean
    fun baseProducerTopic(): NewTopic {
        return TopicBuilder.name("base")
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun enrichedProducerTopic(): NewTopic {
        return TopicBuilder.name("enriched")
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun twiceEnrichedProducerTopic(): NewTopic {
        return TopicBuilder.name("twice-enriched")
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun tripleEnrichedProducerTopic(): NewTopic {
        return TopicBuilder.name("triple-enriched")
            .partitions(3)
            .replicas(1)
            .build()
    }

}
