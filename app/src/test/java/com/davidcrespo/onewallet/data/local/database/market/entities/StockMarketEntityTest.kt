package com.davidcrespo.onewallet.data.local.database.market.entities

import com.davidcrespo.onewallet.data.local.database.portfolio.entities.CurrencyEntity
import com.davidcrespo.onewallet.data.remote.finnhub.models.MarketStockResponse
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StockMarketEntityTest {

    @Test
    fun `toDomain mapea correctamente de Entity a Domain`() {
        // Given
        val entity = StockMarketEntity(
            symbol = "AAPL",
            description = "Apple Inc",
            currency = CurrencyEntity(USD),
            figi = "FIGI123",
            type = "Common Stock"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("AAPL", domain.symbol)
        assertEquals(0.0, domain.price, 0.0)
        assertEquals(Currency(USD), domain.currency)
        assertEquals(InvestmentType.STOCK, domain.type)
        assertEquals("Apple Inc", domain.description)
        assertEquals("FIGI123", domain.figi)
        assertEquals("Common Stock", domain.stockType)
    }

    @Test
    fun `toStockEntity mapea correctamente de Response a Entity cuando hay moneda`() {
        // Given
        val response = MarketStockResponse(
            symbol = "MSFT",
            description = "Microsoft Corp",
            currency = USD,
            figi = "FIGI456",
            type = "Common Stock"
        )

        // When
        val entity = response.toStockEntity()

        // Then
        assertEquals("MSFT", entity?.symbol)
        assertEquals(CurrencyEntity(USD), entity?.currency)
        assertEquals("Microsoft Corp", entity?.description)
        assertEquals("FIGI456", entity?.figi)
        assertEquals("Common Stock", entity?.type)
    }

    @Test
    fun `toStockEntity devuelve null cuando la moneda esta vacia`() {
        // Given
        val response = MarketStockResponse(
            symbol = "INVALID",
            description = "No Currency",
            currency = "",
            figi = "",
            type = ""
        )

        // When
        val entity = response.toStockEntity()

        // Then
        assertNull(entity)
    }
}
