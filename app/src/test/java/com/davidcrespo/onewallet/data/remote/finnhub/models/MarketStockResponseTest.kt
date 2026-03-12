package com.davidcrespo.onewallet.data.remote.finnhub.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MarketStockResponseTest {

    @Test
    fun `toDomain mapea correctamente cuando la moneda es USD`() {
        // Given
        val response = MarketStockResponse(
            symbol = "AAPL",
            description = "Apple Inc",
            currency = USD,
            figi = "FIGI123",
            type = "Common Stock"
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("AAPL", domain?.symbol)
        assertEquals(Currency(USD), domain?.currency)
        assertEquals(InvestmentType.STOCK, domain?.type)
        assertEquals("Apple Inc", domain?.description)
        assertEquals("FIGI123", domain?.figi)
        assertEquals("Common Stock", domain?.stockType)
    }

    @Test
    fun `toDomain devuelve null cuando la moneda no es USD`() {
        // Given
        val response = MarketStockResponse(
            symbol = "SAN.MC",
            description = "Santander",
            currency = EUR,
            figi = "FIGI456",
            type = "Common Stock"
        )

        // When
        val domain = response.toDomain()

        // Then
        assertNull(domain)
    }

    @Test
    fun `toDomain devuelve null cuando la moneda esta vacia`() {
        // Given
        val response = MarketStockResponse(
            symbol = "TEST",
            description = "Test",
            currency = "",
            figi = "",
            type = ""
        )

        // When
        val domain = response.toDomain()

        // Then
        assertNull(domain)
    }
}
