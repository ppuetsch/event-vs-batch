package com.example.demo.batch.config

import com.example.demo.infrastructure.model.BaseEntity
import com.example.demo.infrastructure.model.EnrichedBaseEntity
import com.example.demo.infrastructure.model.TwiceEnrichedBaseEntity
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.Step
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.infrastructure.item.database.JpaCursorItemReader
import org.springframework.batch.infrastructure.item.database.JpaItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager


@Configuration
class BatchConfig  (
    val entityManagerFactory: EntityManagerFactory
) : DefaultBatchConfiguration() {

    @Bean
    fun baseEntityReader(): JpaCursorItemReader<BaseEntity> = JpaCursorItemReader(entityManagerFactory)

    @Bean
    fun enrichedBaseEntityReader(): JpaCursorItemReader<EnrichedBaseEntity> = JpaCursorItemReader(entityManagerFactory)

    @Bean
    fun twicheEnrichedBaseEntityReader(): JpaCursorItemReader<TwiceEnrichedBaseEntity> = JpaCursorItemReader(entityManagerFactory)


    @Bean
    fun baseEntityWriter(): JpaItemWriter<BaseEntity> = JpaItemWriter(entityManagerFactory)

    @Bean
    fun enrichedBaseEntityWriter(): JpaItemWriter<EnrichedBaseEntity> = JpaItemWriter(entityManagerFactory)

    @Bean
    fun twicheEnrichedBaseEntityWriter(): JpaItemWriter<TwiceEnrichedBaseEntity> = JpaItemWriter(entityManagerFactory)

    @Bean
    fun enrichmentJob(jobRepository: JobRepository, enrichBaseEntity: Step, twichEnrichBaseEntity: Step): Job? {
        return JobBuilder("enrichmentJob", jobRepository)
            .start(enrichBaseEntity)
            .next(twichEnrichBaseEntity)
            .build()
    }

    @Bean
    fun enrichBaseEntity(
        jobRepository: JobRepository, transactionManager: DataSourceTransactionManager,
        baseEntityReader: JpaCursorItemReader<BaseEntity>, processor: BaseItemProcessor, writer: JpaItemWriter<EnrichedBaseEntity>,
    ): Step {
        return StepBuilder(jobRepository)
            .chunk<BaseEntity, EnrichedBaseEntity>(3)
            .transactionManager(transactionManager)
            .reader(baseEntityReader)
            .processor(processor)
            .writer(writer)
            .build()
    }

    @Bean
    fun twichEnrichBaseEntity(
        jobRepository: JobRepository, transactionManager: DataSourceTransactionManager,
        enrichedBaseEntityReader: JpaCursorItemReader<EnrichedBaseEntity>, processor: EnrichedItemProcessor, writer: JpaItemWriter<TwiceEnrichedBaseEntity>,
    ): Step {
        return StepBuilder(jobRepository)
            .chunk<EnrichedBaseEntity, TwiceEnrichedBaseEntity>(3)
            .transactionManager(transactionManager)
            .reader(enrichedBaseEntityReader)
            .processor(processor)
            .writer(writer)
            .build()
    }

}
