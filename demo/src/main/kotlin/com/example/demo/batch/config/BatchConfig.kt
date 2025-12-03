package com.example.demo.batch.config

import com.example.demo.infrastructure.model.BaseEntity
import com.example.demo.infrastructure.model.EnrichedBaseEntity
import com.example.demo.infrastructure.model.TripleEnrichedBaseEntity
import com.example.demo.infrastructure.model.TwiceEnrichedBaseEntity
import com.example.demo.infrastructure.repository.BaseEntityRepository
import com.example.demo.infrastructure.repository.EnrichedBaseEntityRepository
import com.example.demo.infrastructure.repository.TripleEnrichedBaseEntityRepository
import com.example.demo.infrastructure.repository.TwiceEnrichedBaseEntityRepository
import org.slf4j.LoggerFactory
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.listener.ItemProcessListener
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.Step
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort


@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository
class BatchConfig  (
    val baseEntityRepository: BaseEntityRepository,
    val enrichedBaseEntityRepository: EnrichedBaseEntityRepository,
    val twiceEnrichedBaseEntityRepository: TwiceEnrichedBaseEntityRepository,
    val tripleEnrichedBaseEntityRepository: TripleEnrichedBaseEntityRepository,
    val jobRepository: JobRepository
){
    companion object {
        private val logger = LoggerFactory.getLogger(BatchConfig::class.java)
    }

    val chunkSize = 1

    @Bean
    fun baseEntityReader(): RepositoryItemReader<BaseEntity>
        = setReaderMethodAndSize(RepositoryItemReader(baseEntityRepository, mapByIdAsc()))

    @Bean
    fun enrichedBaseEntityReader(): RepositoryItemReader<EnrichedBaseEntity> =
        setReaderMethodAndSize(RepositoryItemReader(enrichedBaseEntityRepository, mapByIdAsc()))

    @Bean
    fun twiceEnrichedBaseEntityReader(): RepositoryItemReader<TwiceEnrichedBaseEntity> =
        setReaderMethodAndSize(RepositoryItemReader(twiceEnrichedBaseEntityRepository, mapByIdAsc()))


    private fun <T : Any> setReaderMethodAndSize(reader: RepositoryItemReader<T>): RepositoryItemReader<T> {
        reader.setMethodName("findAll")
        reader.setPageSize(chunkSize)
        return reader
    }

    private fun mapByIdAsc(): Map<String, Sort.Direction> = mapOf("id" to Sort.Direction.ASC)

    @Bean
    fun baseEntityWriter(): RepositoryItemWriter<BaseEntity> = RepositoryItemWriter(baseEntityRepository)

    @Bean
    fun enrichedBaseEntityWriter(): RepositoryItemWriter<EnrichedBaseEntity> = RepositoryItemWriter(enrichedBaseEntityRepository)

    @Bean
    fun twiceEnrichedBaseEntityWriter(): RepositoryItemWriter<TwiceEnrichedBaseEntity> = RepositoryItemWriter(twiceEnrichedBaseEntityRepository)

    @Bean
    fun tripleEnrichedBaseEntityWriter(): RepositoryItemWriter<TripleEnrichedBaseEntity> = RepositoryItemWriter(tripleEnrichedBaseEntityRepository)


    @Bean
    fun enrichmentJob(): Job? {
        return JobBuilder("enrichmentJob", jobRepository)
            .start(enrichBaseStep())
            .next(twiceEnrichStep())
            .next(tripleEnrichStep())
            .build()
    }

    @Bean
    fun enrichBaseStep(
    ): Step {
        return StepBuilder(jobRepository)
            .chunk<BaseEntity, EnrichedBaseEntity>(chunkSize)
            .reader(baseEntityReader())
            .processor(BaseItemProcessor())
            .writer(enrichedBaseEntityWriter())
            .build()
    }

    @Bean
    fun twiceEnrichStep(): Step {
        return StepBuilder("twiceEnrichStep", jobRepository)
            .chunk<EnrichedBaseEntity, TwiceEnrichedBaseEntity>(chunkSize)
            .reader(enrichedBaseEntityReader())
            .processor(EnrichedItemProcessor())
            .writer(twiceEnrichedBaseEntityWriter())
            .build()
    }
    @Bean
    fun tripleEnrichStep(): Step {
        return StepBuilder("tripleEnrichStep", jobRepository)
            .chunk<TwiceEnrichedBaseEntity, TripleEnrichedBaseEntity>(chunkSize)
            .reader(twiceEnrichedBaseEntityReader())
            .processor(TwiceEnrichedItemProcessor())
            .listener(object : ItemProcessListener<
                    TwiceEnrichedBaseEntity,
                    TripleEnrichedBaseEntity> {

                override fun onProcessError(
                    item: TwiceEnrichedBaseEntity,
                    e: Exception
                ) {
                    logger.warn("Retry bei ${item.id} : ${e?.message}")
                }
            })
            .faultTolerant()
            .retryLimit(3)
            .retry(RetryableException::class.java)
            .writer(tripleEnrichedBaseEntityWriter())
            .build()
    }

}
