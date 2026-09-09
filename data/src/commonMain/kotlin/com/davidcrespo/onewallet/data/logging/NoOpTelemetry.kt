package com.davidcrespo.onewallet.data.logging

import com.davidcrespo.onewallet.domain.logging.Telemetry

object NoOpTelemetry : Telemetry {
    override suspend fun log(message: String) = Unit
}