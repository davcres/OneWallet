package com.davidcrespo.onewallet.data.remote.alphaVantage

import com.davidcrespo.onewallet.data.remote.alphaVantage.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.alphaVantage.models.MarketStockResponseList
import com.davidcrespo.onewallet.data.remote.alphaVantage.models.StockPriceResponse
import com.davidcrespo.onewallet.data.remote.alphaVantage.models.StockPriceResponseObject
import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.domain.model.investment.USD
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlphaVantageDataSourceTest {

    private val apiClient = mockk<AlphaVantageApiClient>()
    private val dataSource = AlphaVantageDataSource(apiClient)

    @Test
    fun `getStocksSymbolsByQuery devuelve la lista de bestMatches del API client`() = runTest {
        // Given
        val query = "AAPL"
        val mockResponse = MarketStockResponseList(
            bestMatches = listOf(
                MarketStockResponse(symbol = "AAPL", name = "Apple Inc"),
                MarketStockResponse(symbol = "AAPL.MEX", name = "Apple Inc")
            )
        )
        coEvery { apiClient.getStocksSymbolsByQuery(query) } returns mockResponse

        // When
        val result = dataSource.getStocksSymbolsByQuery(query)

        // Then
        assertEquals(2, result.size)
        assertEquals("AAPL", result[0].symbol)
        assertEquals("AAPL.MEX", result[1].symbol)
    }

    @Test
    fun `getStocksSymbolsByQuery devuelve lista vacia si bestMatches es nulo`() = runTest {
        // Given
        val query = "UNKNOWN"
        val mockResponse = MarketStockResponseList(bestMatches = null)
        coEvery { apiClient.getStocksSymbolsByQuery(query) } returns mockResponse

        // When
        val result = dataSource.getStocksSymbolsByQuery(query)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getStockPrice mapea la respuesta de globalQuote a InvestmentDto`() = runTest {
        // Given
        val symbol = "AAPL"
        val name = "Apple Inc"
        val currency = CurrencyDto(USD)
        val mockResponse = StockPriceResponseObject(
            globalQuote = StockPriceResponse(
                symbol = "AAPL",
                price = "150.50",
                previousClose = "148.20"
            )
        )
        coEvery { apiClient.getStockPrice(symbol) } returns mockResponse

        // When
        val result = dataSource.getStockPrice(symbol, name, currency)

        // Then
        assertEquals(symbol, result?.symbol)
        assertEquals(name, result?.name)
        assertEquals(150.50, result?.price ?: 0.0, 0.0)
        assertEquals(148.20, result?.previousPrice ?: 0.0, 0.0)
        assertEquals(currency, result?.currency)
    }

    @Test
    fun `getStockPrice devuelve null si globalQuote es nulo`() = runTest {
        // Given
        val symbol = "FAIL"
        val mockResponse = StockPriceResponseObject(globalQuote = null)
        coEvery { apiClient.getStockPrice(symbol) } returns mockResponse

        // When
        val result = dataSource.getStockPrice(symbol, "Name", CurrencyDto(USD))

        // Then
        assertNull(result)
    }
}
