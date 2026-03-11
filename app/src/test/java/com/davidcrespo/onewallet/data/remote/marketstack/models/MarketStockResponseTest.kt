package com.davidcrespo.onewallet.data.remote.marketstack.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.GlobalMarketRegion
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.UNKNOWN
import org.junit.Assert.assertEquals
import org.junit.Test

class MarketStockResponseTest {

    @Test
    fun `toDomain mapea correctamente todos los campos`() {
        // Given
        val response = MarketStockResponse(
            ticker = "AAPL",
            name = "Apple Inc",
            stockExchange = StockExchange(mic = "XNAS")
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("AAPL", domain.symbol)
        assertEquals("Apple Inc", domain.description)
        assertEquals(Currency(UNKNOWN), domain.currency)
        assertEquals(InvestmentType.STOCK, domain.type)
        assertEquals(GlobalMarketRegion.UNITED_STATES, domain.region)
        assertEquals(0.0, domain.price, 0.0)
    }

    @Test
    fun `toDomain maneja campos nulos`() {
        // Given
        val response = MarketStockResponse(
            ticker = null,
            name = null,
            stockExchange = null
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("", domain.symbol)
        assertEquals(null, domain.description)
        assertEquals(GlobalMarketRegion.GLOBAL, domain.region)
    }

    @Test
    fun `toDomain mapea la region correctamente usando el MIC`() {
        // Given
        val response = MarketStockResponse(
            ticker = "SAN.MC",
            stockExchange = StockExchange(mic = "XMCE") // Bolsa de Madrid
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals(GlobalMarketRegion.SPAIN, domain.region)
    }
}
