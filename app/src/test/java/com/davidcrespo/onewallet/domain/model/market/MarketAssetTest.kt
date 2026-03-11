package com.davidcrespo.onewallet.domain.model.market

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import org.junit.Assert.assertEquals
import org.junit.Test

class MarketAssetTest {

    @Test
    fun `toInvestment mapea correctamente todos los campos`() {
        // Given
        val asset = MarketAsset(
            symbol = "AAPL",
            price = 150.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            description = "Apple Inc",
            figi = "FIGI123",
            region = null,
            stockType = "Common"
        )
        val year = 2024
        val month = 3

        // When
        val investment = asset.toInvestment(InvestmentType.STOCK, year, month)

        // Then
        assertEquals(asset.symbol, investment.symbol)
        assertEquals(asset.description, investment.name)
        assertEquals(0.0, investment.quantity, 0.0)
        assertEquals(asset.price, investment.price, 0.0)
        assertEquals(0.0, investment.previousPrice, 0.0)
        assertEquals(asset.currency, investment.currency)
        assertEquals(InvestmentType.STOCK, investment.type)
        assertEquals(year, investment.year)
        assertEquals(month, investment.month)
    }

    @Test
    fun `toInvestment maneja la descripcion nula convirtiendola en cadena vacia`() {
        // Given
        val asset = MarketAsset(
            symbol = "BTC",
            price = 50000.0,
            currency = Currency(EUR),
            type = InvestmentType.CRYPTO,
            description = null,
            stockType = null
        )

        // When
        val investment = asset.toInvestment(InvestmentType.CRYPTO, 2024, 3)

        // Then
        assertEquals("", investment.name)
    }
}
