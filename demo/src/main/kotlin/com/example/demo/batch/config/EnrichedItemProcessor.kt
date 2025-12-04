package com.example.demo.batch.config

import com.example.demo.infrastructure.model.EnrichedBaseEntity
import com.example.demo.infrastructure.model.TwiceEnrichedBaseEntity
import org.springframework.batch.infrastructure.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class EnrichedItemProcessor : ItemProcessor<EnrichedBaseEntity, TwiceEnrichedBaseEntity> {
    override fun process(baseEntity: EnrichedBaseEntity): TwiceEnrichedBaseEntity {

        val enrichedBaseEntity = TwiceEnrichedBaseEntity(baseEntity, "second enrichment")
        return enrichedBaseEntity
    }
}
