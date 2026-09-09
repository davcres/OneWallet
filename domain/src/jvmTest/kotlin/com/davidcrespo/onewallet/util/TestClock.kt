package com.davidcrespo.onewallet.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class TestClock(private val instant: Instant) : Clock {
    override fun now(): Instant = instant
}
