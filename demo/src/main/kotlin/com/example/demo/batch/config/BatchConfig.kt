package com.example.demo.batch.config

import com.example.demo.infrastructure.model.BaseEntity
import com.example.demo.infrastructure.model.EnrichedBaseEntity
import com.example.demo.infrastructure.model.TripleEnrichedBaseEntity
import com.example.demo.infrastructure.model.TwiceEnrichedBaseEntity
import com.example.demo.infrastructure.repository.BaseEntityRepository
import com.example.demo.infrastructure.repository.EnrichedBaseEntityRepository
import com.example.demo.infrastructure.repository.TripleEnrichedBaseEntityRepository
import com.example.demo.infrastructure.repository.TwiceEnrichedBaseEntityRepository
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.builder.JobBuilder
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
    val tripleEnrichedBaseEntityRepository: TripleEnrichedBaseEntityRepository
){

    val chunkSize = 100

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
    fun enrichmentJob(jobRepository: JobRepository): Job? {
        return JobBuilder("enrichmentJob", jobRepository)
            .start(enrichBaseStep(jobRepository))
            .next(twichEnrichStep(jobRepository))
            .next(tripleEnrichStep(jobRepository))
            .build()
    }

    @Bean
    fun enrichBaseStep(
        jobRepository: JobRepository,

    ): Step {
        return StepBuilder(jobRepository)
            .chunk<BaseEntity, EnrichedBaseEntity>(3)
            .reader(baseEntityReader())
            .processor(BaseItemProcessor())
            .writer(enrichedBaseEntityWriter())
            .build()
    }

    @Bean
    fun twichEnrichStep(jobRepository: JobRepository): Step {
        return StepBuilder(jobRepository)
            .chunk<EnrichedBaseEntity, TwiceEnrichedBaseEntity>(3)
            .reader(enrichedBaseEntityReader())
            .processor(EnrichedItemProcessor())
            .writer(twiceEnrichedBaseEntityWriter())
            .build()
    }
    @Bean
    fun tripleEnrichStep(jobRepository: JobRepository): Step {
        return StepBuilder(jobRepository)
            .chunk<TwiceEnrichedBaseEntity, TripleEnrichedBaseEntity>(3)
            .reader(twiceEnrichedBaseEntityReader())
            .processor(TwiceEnrichedItemProcessor())
            .writer(tripleEnrichedBaseEntityWriter())
            .faultTolerant()
            .retryLimit(3)
            .retry(RetryableException::class.java)
            .build()
    }

}
