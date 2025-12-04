package com.example.demo.infrastructure.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id


@Entity
open class BaseEntity {
    @Id
    open var id: String? = null

    constructor()

    constructor(id: String?) {
        this.id = id
    }
}

@Entity
open class EnrichedBaseEntity {
    @Id
    open var id: String? = null

    @Column(nullable = false)
    open var enrichment: String? = null

    constructor()

    constructor(entity: BaseEntity, enrichment: String?) {
        this.id = entity.id
        this.enrichment = enrichment
    }
}

@Entity
open class TwiceEnrichedBaseEntity {
    @Id
    open var id: String? = null

    @Column(nullable = false)
    open var enrichment: String? = null

    @Column(nullable = false)
    open var enrichment2: String? = null

    constructor()

    constructor(entity: EnrichedBaseEntity, enrichment2: String?) {
        this.id = entity.id
        this.enrichment = entity.enrichment
        this.enrichment2 = enrichment2
    }
}

@Entity
open class TripleEnrichedBaseEntity {
    @Id
    open var id: String? = null

    @Column(nullable = false)
    open var enrichment: String? = null

    @Column(nullable = false)
    open var enrichment2: String? = null

    @Column(nullable = false)
    open var enrichment3: String? = null

    constructor()

    constructor(entity: TwiceEnrichedBaseEntity, enrichment3: String?) {
        this.id = entity.id
        this.enrichment = entity.enrichment
        this.enrichment2 = entity.enrichment2
        this.enrichment3 = enrichment3
    }
}
