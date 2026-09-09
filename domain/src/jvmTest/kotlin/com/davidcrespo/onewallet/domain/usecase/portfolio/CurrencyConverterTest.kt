package com.davidcrespo.onewallet.domain.usecase.portfolio

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CurrencyConverterTest {

    private lateinit var converter: CurrencyConverter

    @BeforeEach
    fun setUp() {
        converter = CurrencyConverter()
    }

    @Test
    fun `convert returns same amount when from and to currencies are equal`() {
        val result = converter.convert(100.0, "EUR", "EUR", 1.1)
        assertEquals(100.0, result, 0.001)
    }

    @Test
    fun `convert multiplies amount by rate when currencies differ`() {
        val result = converter.convert(100.0, "USD", "EUR", 0.85)
        assertEquals(85.0, result, 0.001)
    }
}
