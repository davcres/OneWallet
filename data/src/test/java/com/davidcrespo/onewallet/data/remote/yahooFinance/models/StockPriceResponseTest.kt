package com.davidcrespo.onewallet.data.remote.yahooFinance.models

import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StockPriceResponseTest {

    @Test
    fun `toInvestDto mapea correctamente todos los campos`() {
        // Given
        val meta = StockPriceResponseMeta(
            currency = "EUR",
            regularMarketPrice = 150.50,
            chartPreviousClose = 148.20
        )
        val symbol = "AAPL"
        val name = "Apple Inc"

        // When
        val dto = meta.toInvestDto(symbol, name)

        // Then
        assertEquals(symbol, dto.symbol)
        assertEquals(name, dto.name)
        assertEquals(150.50, dto.price, 0.0)
        assertEquals(148.20, dto.previousPrice, 0.0)
        assertEquals("EUR", dto.currency.code)
        assertEquals(InvestmentType.STOCK, dto.type)
    }

    @Test
    fun `toInvestDto maneja campos nulos y usa USD por defecto para la moneda`() {
        // Given
        val meta = StockPriceResponseMeta(
            currency = null,
            regularMarketPrice = null,
            chartPreviousClose = null
        )

        // When
        val dto = meta.toInvestDto("TEST", "Test")

        // Then
        assertEquals(0.0, dto.price, 0.0)
        assertEquals(0.0, dto.previousPrice, 0.0)
        assertEquals(USD, dto.currency.code)
    }
}
