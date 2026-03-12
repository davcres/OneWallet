package com.davidcrespo.onewallet.data.remote.marketstack

import com.davidcrespo.onewallet.data.remote.marketstack.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.marketstack.models.MarketStockResponseList
import com.davidcrespo.onewallet.data.remote.marketstack.models.Pagination
import com.davidcrespo.onewallet.data.remote.marketstack.models.StockPriceResponse
import com.davidcrespo.onewallet.data.remote.marketstack.models.StockPriceResponseList
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MarketstackDataSourceTest {

    private val apiClient = mockk<MarketstackApiClient>()
    private val dataSource = MarketstackDataSource(apiClient)

    @Test
    fun `getStocksSymbolsByQuery devuelve la lista de data del API client`() = runTest {
        // Given
        val query = "AAPL"
        val mockResponse = MarketStockResponseList(
            pagination = Pagination(1, 0, 1, 1),
            data = listOf(
                MarketStockResponse(ticker = "AAPL", name = "Apple Inc")
            )
        )
        coEvery { apiClient.getStocksSymbolsByQuery(query) } returns mockResponse

        // When
        val result = dataSource.getStocksSymbolsByQuery(query)

        // Then
        assertEquals(1, result.size)
        assertEquals("AAPL", result[0].ticker)
    }

    @Test
    fun `getStocksSymbolsByQuery devuelve lista vacia si data es nulo`() = runTest {
        // Given
        val query = "NONE"
        val mockResponse = MarketStockResponseList(data = null)
        coEvery { apiClient.getStocksSymbolsByQuery(query) } returns mockResponse

        // When
        val result = dataSource.getStocksSymbolsByQuery(query)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getStockPrice mapea correctamente de StockPriceResponseList a InvestmentDto`() = runTest {
        // Given
        val symbol = "AAPL"
        val name = "Apple Inc"
        val mockPriceListResponse = StockPriceResponseList(
            pagination = Pagination(2, 0, 2, 2),
            data = listOf(
                createStockPriceResponse(150.0, USD),
                createStockPriceResponse(148.0, USD)
            )
        )
        coEvery { apiClient.getStockPrice(symbol) } returns mockPriceListResponse

        // When
        val result = dataSource.getStockPrice(symbol, name)

        // Then
        assertEquals(symbol, result.symbol)
        assertEquals(name, result.name)
        assertEquals(150.0, result.price, 0.0)
        assertEquals(148.0, result.previousPrice, 0.0)
        assertEquals(USD, result.currency.code)
        assertEquals(InvestmentType.STOCK, result.type)
    }

    private fun createStockPriceResponse(close: Double, currency: String): StockPriceResponse {
        return StockPriceResponse(
            open = 0.0, high = 0.0, low = 0.0, close = close, volume = 0.0,
            adjHigh = null, adjLow = null, adjClose = null, adjOpen = null, adjVolume = null,
            splitFactor = 1.0, dividend = 0.0, name = null, exchangeCode = null,
            assetType = null, priceCurrency = currency, symbol = "TEST",
            exchange = "TEST", date = "2024-03-10"
        )
    }
}
