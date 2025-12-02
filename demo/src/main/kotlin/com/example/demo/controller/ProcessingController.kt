package com.example.demo.controller

import com.example.demo.infrastructure.model.BaseEntity
import com.example.demo.infrastructure.repository.BaseEntityRepository
import com.example.demo.infrastructure.repository.EnrichedBaseEntityRepository
import com.example.demo.infrastructure.repository.TripleEnrichedBaseEntityRepository
import com.example.demo.infrastructure.repository.TwiceEnrichedBaseEntityRepository
import com.example.demo.service.KafkaService
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.parameters.JobParametersBuilder
import org.springframework.batch.core.launch.JobOperator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RequestMapping(path = ["/"], produces = ["application/json;charset=UTF-8"])
@RestController
class ProcessingController(
    val baseEntityRepository: BaseEntityRepository,
    val enrichedBaseEntityRepository: EnrichedBaseEntityRepository,
    val twiceEnrichedBaseEntityRepository: TwiceEnrichedBaseEntityRepository,
    val tripleEnrichedBaseEntityRepository: TripleEnrichedBaseEntityRepository,
    val kafkaService: KafkaService,
    val enrichmentJob: Job
) {

    @GetMapping("/status/")
    fun status(): StatusEntity {
        return StatusEntity(
            baseEntityRepository.findAll().count(),
            enrichedBaseEntityRepository.findAll().count(),
            twiceEnrichedBaseEntityRepository.findAll().count(),
            tripleEnrichedBaseEntityRepository.findAll().count()
        )
    }

    @PostMapping("/startProcessingEventBased")
    fun startProcessingEventBased() {
        val entitiesToBeProcessed = baseEntityRepository.findAll()
        entitiesToBeProcessed.forEach {
            kafkaService.send("base", it.id!!, "${it.id} Is ready for processing")
        }
    }

    @PostMapping("/startProcessingBatched")
    fun startProcessingBatched() {
        val jobOperator: JobOperator = DefaultBatchConfiguration().jobOperator(DefaultBatchConfiguration().jobRepository())

        val jobParameters = JobParametersBuilder()
            .addLong("startAt", System.currentTimeMillis())
            .toJobParameters()
        jobOperator.start(enrichmentJob, jobParameters)
    }


    @PostMapping("/initializeDatabase")
    fun initializeDatabase(@RequestParam(value = "anzahl") anzahl: Int) {
        val newEntities = List(anzahl) { BaseEntity(UUID.randomUUID().toString()) }
        baseEntityRepository.saveAll(newEntities)
    }

    @PostMapping("/sendKafkaMessage")
    fun postMessage(
        @RequestParam(value = "topic") topic: String,
        @RequestParam(value = "key") key: String,
        @RequestParam(value = "value") value: String
    ) {
        kafkaService.send(topic, key, value)
    }


    class StatusEntity(
        val nrBaseEntity: Int,
        val nrEnrichedEntity: Int,
        val nrTwiceEnrichedEntity: Int,
        val nrTripleEnrichedBaseEntity: Int
    )

}
