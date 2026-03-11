package com.davidcrespo.onewallet.data.remote.twelveData.models

import org.junit.Assert.assertEquals
import org.junit.Test

class RateResponseTest {

    @Test
    fun `toDomain mapea correctamente los campos`() {
        // Given
        val response = RateResponse(symbol = "EUR/USD", rate = 1.0850)

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("EUR/USD", domain.symbol)
        assertEquals(1.0850, domain.rate, 0.0)
    }
}
