package com.example.demo.service

import com.example.demo.infrastructure.model.EnrichedBaseEntity
import com.example.demo.infrastructure.model.TripleEnrichedBaseEntity
import com.example.demo.infrastructure.model.TwiceEnrichedBaseEntity
import com.example.demo.infrastructure.repository.BaseEntityRepository
import com.example.demo.infrastructure.repository.EnrichedBaseEntityRepository
import com.example.demo.infrastructure.repository.TripleEnrichedBaseEntityRepository
import com.example.demo.infrastructure.repository.TwiceEnrichedBaseEntityRepository
import org.springframework.stereotype.Service

@Service
class Enricher(
    val baseEntityRepository: BaseEntityRepository,
    val enrichedBaseEntityRepository: EnrichedBaseEntityRepository,
    val twiceEnrichedBaseEntityRepository: TwiceEnrichedBaseEntityRepository,
    val tripleEnrichedBaseEntityRepository: TripleEnrichedBaseEntityRepository,
) {
    var countProcessedBase = 0
    var countProcessedEnriched = 0
    var countProcessedTwiceEnriched = 0

    fun enrichBase(id: String) {
        Thread.sleep(400)
        countProcessedBase++
        if(countProcessedBase % 33 == 0) {
            throw Exception("Oh no - I could not enrich again!")
        }
        enrichedBaseEntityRepository.save(
            EnrichedBaseEntity(
                baseEntityRepository.findById(id).get(),
                "I have been enriched",
            )
        )
    }

    fun enrichEnriched(id: String) {
        Thread.sleep(400)
        countProcessedEnriched++
        if(countProcessedEnriched % 33 == 0) {
            throw Exception("Oh no - I could not enrich again!")
        }
        twiceEnrichedBaseEntityRepository.save(
            TwiceEnrichedBaseEntity(
                enrichedBaseEntityRepository.findById(id).get(),
                "I have been enriched two times",
            )
        )
    }

    fun enrichTwiceEnriched(id: String) {
        Thread.sleep(400)
        countProcessedTwiceEnriched++
        if(countProcessedTwiceEnriched % 33 == 0) {
            throw Exception("Oh no - I could not enrich again!")
        }
        tripleEnrichedBaseEntityRepository.save(
            TripleEnrichedBaseEntity(
                twiceEnrichedBaseEntityRepository.findById(id).get(),
                "I have been enriched even three times",
            )
        )
    }

}