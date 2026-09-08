package com.davidcrespo.onewallet.data.remote.marketstack.models

import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import com.davidcrespo.onewallet.domain.model.investment.UNKNOWN
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StockPriceResponseTest {

    @Test
    fun `toInvestDto mapea correctamente los precios de la lista`() {
        // Given
        // En Marketstack, la lista suele venir ordenada por fecha. 
        // Suponiendo que el primero es el actual y el ultimo es el previo.
        val data = listOf(
            createStockPriceResponse(close = 150.0, currency = USD),
            createStockPriceResponse(close = 148.0, currency = USD)
        )
        val responseList = StockPriceResponseList(
            pagination = Pagination(limit = 10, offset = 0, count = 2, total = 2),
            data = data
        )

        // When
        val dto = responseList.toInvestDto("AAPL", "Apple Inc")

        // Then
        assertEquals(150.0, dto.price, 0.0)
        assertEquals(148.0, dto.previousPrice, 0.0)
        assertEquals(USD, dto.currency.code)
    }

    @Test
    fun `toInvestDto maneja lista vacia`() {
        // Given
        val responseList = StockPriceResponseList(
            pagination = Pagination(0, 0, 0, 0),
            data = emptyList()
        )

        // When
        val dto = responseList.toInvestDto("AAPL", "Apple Inc")

        // Then
        assertEquals(0.0, dto.price, 0.0)
        assertEquals(0.0, dto.previousPrice, 0.0)
        assertEquals(UNKNOWN, dto.currency.code)
    }

    private fun createStockPriceResponse(close: Double, currency: String?): StockPriceResponse {
        return StockPriceResponse(
            open = 0.0, high = 0.0, low = 0.0, close = close, volume = 0.0,
            adjHigh = null, adjLow = null, adjClose = null, adjOpen = null, adjVolume = null,
            splitFactor = 1.0, dividend = 0.0, name = null, exchangeCode = null,
            assetType = null, priceCurrency = currency, symbol = "TEST",
            exchange = "TEST", date = "2024-03-10"
        )
    }
}
