package com.example.demo.batch.config

import com.example.demo.infrastructure.model.TripleEnrichedBaseEntity
import com.example.demo.infrastructure.model.TwiceEnrichedBaseEntity
import org.slf4j.LoggerFactory
import org.springframework.batch.infrastructure.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class TwiceEnrichedItemProcessor : ItemProcessor<TwiceEnrichedBaseEntity, TripleEnrichedBaseEntity> {
    companion object {
        private val logger = LoggerFactory.getLogger(BatchConfig::class.java)
    }
    var exceptionsCreateIterator = 0

    override fun process(baseEntity: TwiceEnrichedBaseEntity): TripleEnrichedBaseEntity {
        exceptionsCreateIterator++
        if(exceptionsCreateIterator % 99 == 0) {
            var retryableException = RetryableException("Oh no - Bussiness Retry! ${exceptionsCreateIterator} bei ${baseEntity.id}")
            logger.warn(retryableException.message)
            exceptionsCreateIterator++
            throw retryableException
        }
        if(exceptionsCreateIterator == 999 ) {
            var exception = Exception("⛔️ ${exceptionsCreateIterator} bei ${baseEntity.id}")
            logger.warn(exception.message)
            throw exception
        }
        val enrichedBaseEntity = TripleEnrichedBaseEntity(baseEntity, "third enrichment")
        return enrichedBaseEntity
    }
}
