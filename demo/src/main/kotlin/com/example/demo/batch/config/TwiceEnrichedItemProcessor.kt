package com.example.demo.batch.config

import com.example.demo.infrastructure.model.TripleEnrichedBaseEntity
import com.example.demo.infrastructure.model.TwiceEnrichedBaseEntity
import org.springframework.batch.infrastructure.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class TwiceEnrichedItemProcessor : ItemProcessor<TwiceEnrichedBaseEntity, TripleEnrichedBaseEntity> {
    var exceptionsCreateIterator = 0

    override fun process(baseEntity: TwiceEnrichedBaseEntity): TripleEnrichedBaseEntity {
        exceptionsCreateIterator++
        if(exceptionsCreateIterator % 33 == 0) {
            exceptionsCreateIterator++
            throw RetryableException("Oh no - I could not enrich again!")
        }
        val enrichedBaseEntity = TripleEnrichedBaseEntity(baseEntity, "third enrichment")
        return enrichedBaseEntity
    }
}
