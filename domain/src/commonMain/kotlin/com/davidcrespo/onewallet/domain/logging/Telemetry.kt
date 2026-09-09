package com.davidcrespo.onewallet.domain.logging

interface Telemetry {
    suspend fun log(message: String)
}
