package com.davidcrespo.onewallet.data.remote.yahooFinance.models

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
            symbol = "AAPL",
            longname = "Apple Inc.",
            exchange = "NASDAQ",
            quoteType = "EQUITY"
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("AAPL", domain.symbol)
        assertEquals("Apple Inc.", domain.description)
        assertEquals(Currency(UNKNOWN), domain.currency)
        assertEquals(InvestmentType.STOCK, domain.type)
        assertEquals(GlobalMarketRegion.UNITED_STATES, domain.region)
        assertEquals("EQUITY", domain.stockType)
        assertEquals(0.0, domain.price, 0.0)
    }

    @Test
    fun `toDomain maneja campos nulos`() {
        // Given
        val response = MarketStockResponse(
            symbol = null,
            longname = null,
            exchange = null,
            quoteType = null
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("", domain.symbol)
        assertEquals(null, domain.description)
        assertEquals(GlobalMarketRegion.GLOBAL, domain.region)
    }

    @Test
    fun `toDomain mapea la region correctamente usando el exchange`() {
        // Given
        val response = MarketStockResponse(
            symbol = "SAN.MC",
            exchange = "MCE" // Bolsa de Madrid
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals(GlobalMarketRegion.SPAIN, domain.region)
    }
}
