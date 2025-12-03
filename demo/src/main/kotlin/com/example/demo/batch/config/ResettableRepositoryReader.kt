package com.example.demo.batch.config

import org.springframework.batch.infrastructure.item.ExecutionContext
import org.springframework.batch.infrastructure.item.ItemStream
import org.springframework.batch.infrastructure.item.ItemStreamReader
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader


class ResettableRepositoryReader<T : Any>(
    private val delegate: RepositoryItemReader<T>
) : ItemStreamReader<T>, ItemStream {

    private var lastCommittedIndex = 0
    private var currentIndex = 0

    override fun open(executionContext: ExecutionContext) {
        delegate.open(executionContext)
        lastCommittedIndex = executionContext.getInt("lastCommittedIndex", 0)
        currentIndex = lastCommittedIndex
    }

    override fun update(executionContext: ExecutionContext) {
        executionContext.putInt("lastCommittedIndex", currentIndex)
        delegate.update(executionContext)
    }

    override fun close() {
        delegate.close()
    }

    override fun read(): T? {
        val item = delegate.read()
        if (item != null) currentIndex++
        return item
    }
}
