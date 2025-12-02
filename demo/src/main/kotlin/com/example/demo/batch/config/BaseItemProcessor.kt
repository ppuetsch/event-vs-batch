package com.example.demo.batch.config

import com.example.demo.infrastructure.model.BaseEntity
import com.example.demo.infrastructure.model.EnrichedBaseEntity
import org.springframework.batch.infrastructure.item.ItemProcessor
import org.springframework.stereotype.Component


@Component
class BaseItemProcessor : ItemProcessor<BaseEntity, EnrichedBaseEntity> {
    override fun process(baseEntity: BaseEntity): EnrichedBaseEntity {

        val enrichedBaseEntity = EnrichedBaseEntity(baseEntity, "first enrichment")
        return enrichedBaseEntity
    }
}
