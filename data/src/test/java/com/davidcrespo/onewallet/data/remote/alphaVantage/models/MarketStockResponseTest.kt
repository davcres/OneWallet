package com.davidcrespo.onewallet.data.remote.alphaVantage.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.GlobalMarketRegion
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MarketStockResponseTest {

    @Test
    fun `toDomain mapea correctamente todos los campos cuando hay moneda`() {
        // Given
        val response = MarketStockResponse(
            symbol = "AAPL",
            name = "Apple Inc",
            type = "Equity",
            region = "United States",
            currency = USD
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("AAPL", domain?.symbol)
        assertEquals("Apple Inc", domain?.description)
        assertEquals(Currency(USD), domain?.currency)
        assertEquals(InvestmentType.STOCK, domain?.type)
        assertEquals(GlobalMarketRegion.UNITED_STATES, domain?.region)
        assertEquals("Equity", domain?.stockType)
        assertEquals(0.0, domain?.price ?: 0.0, 0.0)
    }

    @Test
    fun `toDomain devuelve null cuando la moneda es nula`() {
        // Given
        val response = MarketStockResponse(
            symbol = "AAPL",
            currency = null
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
            symbol = "AAPL",
            currency = ""
        )

        // When
        val domain = response.toDomain()

        // Then
        assertNull(domain)
    }

    @Test
    fun `toDomain maneja simbolos nulos como cadena vacia`() {
        // Given
        val response = MarketStockResponse(
            symbol = null,
            currency = USD
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("", domain?.symbol)
    }

    @Test
    fun `toDomain mapea la region correctamente usando GlobalMarketRegion_from`() {
        // Given
        val response = MarketStockResponse(
            symbol = "SAN.MC",
            region = "Spain",
            currency = "EUR"
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals(GlobalMarketRegion.SPAIN, domain?.region)
    }
}
