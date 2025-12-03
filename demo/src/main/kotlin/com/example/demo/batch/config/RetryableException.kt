package com.example.demo.batch.config

class RetryableException: RuntimeException {
    constructor(message: String) : super(message)
}
