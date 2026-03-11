package com.davidcrespo.onewallet.presentation.portfolio

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyConverterTest {

    private val converter = CurrencyConverter()

    @Test
    fun `convert devuelve el mismo monto si la moneda origen y destino son iguales`() {
        val amount = 100.0
        val result = converter.convert(amount, "USD", "USD", 1.5) // Rate should be ignored
        assertEquals(amount, result, 0.0)
    }

    @Test
    fun `convert aplica el rate si la moneda origen y destino son diferentes`() {
        val amount = 100.0
        val rate = 0.92
        val result = converter.convert(amount, "USD", "EUR", rate)
        assertEquals(92.0, result, 0.0)
    }

    @Test
    fun `convert maneja montos de cero correctamente`() {
        val result = converter.convert(0.0, "USD", "EUR", 0.92)
        assertEquals(0.0, result, 0.0)
    }

    @Test
    fun `convert maneja tasas de cero correctamente`() {
        val result = converter.convert(100.0, "USD", "EUR", 0.0)
        assertEquals(0.0, result, 0.0)
    }
}
