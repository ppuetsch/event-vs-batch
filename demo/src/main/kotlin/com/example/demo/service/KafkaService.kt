package com.example.demo.service

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaService (
    private val kafkaTemplate: KafkaTemplate<String, String>
){
    fun send(topic: String, key: String, value: String) {
        kafkaTemplate.send(ProducerRecord(topic, key, value))
        kafkaTemplate.flush()
    }
}