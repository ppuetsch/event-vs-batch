package com.example.demo.infrastructure.repository

import com.example.demo.infrastructure.model.BaseEntity
import com.example.demo.infrastructure.model.EnrichedBaseEntity
import com.example.demo.infrastructure.model.TripleEnrichedBaseEntity
import com.example.demo.infrastructure.model.TwiceEnrichedBaseEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BaseEntityRepository : JpaRepository<BaseEntity, String>

interface EnrichedBaseEntityRepository : JpaRepository<EnrichedBaseEntity, String>

interface TwiceEnrichedBaseEntityRepository : JpaRepository<TwiceEnrichedBaseEntity, String>

interface TripleEnrichedBaseEntityRepository : JpaRepository<TripleEnrichedBaseEntity, String>
