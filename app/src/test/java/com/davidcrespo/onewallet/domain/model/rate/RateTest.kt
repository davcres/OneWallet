package com.davidcrespo.onewallet.domain.model.rate

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RateTest {

    @Test
    fun `Rate inicializa correctamente los campos`() {
        // Given
        val symbol = "EUR/USD"
        val rateValue = 1.085

        // When
        val rate = Rate(symbol, rateValue)

        // Then
        assertEquals(symbol, rate.symbol)
        assertEquals(rateValue, rate.rate, 0.0)
    }
}
