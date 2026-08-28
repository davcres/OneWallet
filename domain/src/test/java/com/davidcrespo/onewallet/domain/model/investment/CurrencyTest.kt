package com.davidcrespo.onewallet.domain.model.investment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CurrencyTest {

    @Test
    fun `Currency inicializa correctamente el codigo`() {
        // Given
        val code = "GBP"

        // When
        val currency = Currency(code)

        // Then
        assertEquals(code, currency.code)
    }

    @Test
    fun `las constantes de moneda tienen los valores esperados`() {
        assertEquals("USD", USD)
        assertEquals("EUR", EUR)
        assertEquals("UNKNOWN", UNKNOWN)
    }
}
