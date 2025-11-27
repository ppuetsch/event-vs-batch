package com.example.demo.kafka

import com.example.demo.service.Enricher
import com.example.demo.service.KafkaService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class Processor(
    val enricher: Enricher,
    val kafkaService: KafkaService
) {
    @KafkaListener(
        topics = ["base"],
        groupId = "demo",
        batch = "false",
    )
    fun processBase(record: ConsumerRecord<String, String>) {
        enricher.enrichBase(record.key())
        kafkaService.send("enriched", record.key(), "${record.key()} Is enriched now!")
    }

    @KafkaListener(
        topics = ["enriched"],
        groupId = "demo",
        batch = "false",
    )
    fun processEnriched(record: ConsumerRecord<String, String>) {
        enricher.enrichEnriched(record.key())
        kafkaService.send("twice-enriched", record.key(), "${record.key()} Is twice enriched now!")
    }

    @KafkaListener(
        topics = ["twice-enriched"],
        groupId = "demo",
        batch = "false",
    )
    fun processTwiceEnriched(record: ConsumerRecord<String, String>) {
        enricher.enrichTwiceEnriched(record.key())
        kafkaService.send("triple-enriched", record.key(), "${record.key()} Is triple enriched now!")
    }

}
