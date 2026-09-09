package com.davidcrespo.onewallet.data.repository

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PriceAlertNotificationRepositoryImplTest {

    private lateinit var settings: MapSettings
    private lateinit var repository: PriceAlertNotificationRepositoryImpl
    private val clock = object : Clock {
        override fun now(): Instant = Instant.parse("2026-04-25T12:00:00Z")
    }

    @BeforeEach
    fun setUp() {
        settings = MapSettings()
        repository = PriceAlertNotificationRepositoryImpl(settings, clock)
    }

    @Test
    fun `should return true when symbol was notified today`() = runTest {
        // Given
        val symbol = "AAPL"
        val key = "alert_last_date_$symbol"
        settings.putString(key, "2026-04-25")

        // When
        val result = repository.wasNotifiedToday(symbol)

        // Then
        assertTrue(result)
    }

    @Test
    fun `should return false when symbol was not notified today`() = runTest {
        // Given
        val symbol = "AAPL"
        val key = "alert_last_date_$symbol"
        settings.putString(key, "2026-04-24")

        // When
        val result = repository.wasNotifiedToday(symbol)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should return false when symbol has no notification date recorded`() = runTest {
        // Given
        val symbol = "AAPL"

        // When
        val result = repository.wasNotifiedToday(symbol)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should mark symbol as notified today`() = runTest {
        // Given
        val symbol = "AAPL"
        val key = "alert_last_date_$symbol"

        // When
        repository.markNotifiedToday(symbol)

        // Then
        assertTrue(settings.getStringOrNull(key) == "2026-04-25")
    }
}
