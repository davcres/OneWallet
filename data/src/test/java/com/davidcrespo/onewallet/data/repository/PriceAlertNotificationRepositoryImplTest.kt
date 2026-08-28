package com.davidcrespo.onewallet.data.repository

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PriceAlertNotificationRepositoryImplTest {

    private val sharedPreferences = mockk<SharedPreferences>(relaxed = true)
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)
    private lateinit var repository: PriceAlertNotificationRepositoryImpl
    private val fixedDate = LocalDate.of(2026, 4, 25)

    @BeforeEach
    fun setUp() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns fixedDate
        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        
        repository = PriceAlertNotificationRepositoryImpl(sharedPreferences)
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic(LocalDate::class)
    }

    @Test
    fun `should return true when symbol was notified today`() = runTest {
        // Given
        val symbol = "AAPL"
        val key = "alert_last_date_$symbol"
        every { sharedPreferences.getString(key, null) } returns fixedDate.toString()

        // When
        val result = repository.wasNotifiedToday(symbol)

        // Then
        assertTrue(result)
        verify { sharedPreferences.getString(key, null) }
    }

    @Test
    fun `should return false when symbol was not notified today`() = runTest {
        // Given
        val symbol = "AAPL"
        val key = "alert_last_date_$symbol"
        every { sharedPreferences.getString(key, null) } returns "2026-04-24"

        // When
        val result = repository.wasNotifiedToday(symbol)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should return false when symbol has no notification date recorded`() = runTest {
        // Given
        val symbol = "AAPL"
        val key = "alert_last_date_$symbol"
        every { sharedPreferences.getString(key, null) } returns null

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
        val todayStr = fixedDate.toString()

        // When
        repository.markNotifiedToday(symbol)

        // Then
        verify { editor.putString(key, todayStr) }
        verify { editor.apply() }
    }
}
