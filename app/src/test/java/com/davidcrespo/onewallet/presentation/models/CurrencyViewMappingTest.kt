package com.davidcrespo.onewallet.presentation.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Euro
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyViewMappingTest {

    @Test
    fun `CurrencyView get devuelve el objeto correcto para USD`() {
        val result = CurrencyView.get(USD)
        assertEquals(USD, result.code)
        assertEquals("$", result.symbol)
        assertEquals(Icons.Filled.AttachMoney, result.icon)
    }

    @Test
    fun `CurrencyView get devuelve el objeto correcto para EUR`() {
        val result = CurrencyView.get(EUR)
        assertEquals(EUR, result.code)
        assertEquals("€", result.symbol)
        assertEquals(Icons.Filled.Euro, result.icon)
    }

    @Test
    fun `CurrencyView get devuelve un objeto por defecto para una moneda desconocida`() {
        val unknownCode = "JPY"
        val result = CurrencyView.get(unknownCode)
        assertEquals(unknownCode, result.code)
        assertEquals(unknownCode, result.symbol)
        assertEquals(Icons.Filled.CurrencyExchange, result.icon)
    }

    @Test
    fun `Currency toUI mapea correctamente`() {
        val domain = Currency(USD)
        val ui = domain.toUI()
        assertEquals(USD, ui.code)
        assertEquals("$", ui.symbol)
    }

    @Test
    fun `CurrencyView toDomain mapea correctamente`() {
        val ui = CurrencyView(EUR, "€", Icons.Filled.Euro)
        val domain = ui.toDomain()
        assertEquals(EUR, domain.code)
    }
}
