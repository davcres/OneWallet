package com.davidcrespo.onewallet.data.remote.yahooFinance

import com.davidcrespo.onewallet.data.remote.yahooFinance.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.yahooFinance.models.MarketStockResponseList
import com.davidcrespo.onewallet.data.remote.yahooFinance.models.StockPriceResponse
import com.davidcrespo.onewallet.data.remote.yahooFinance.models.StockPriceResponseChart
import com.davidcrespo.onewallet.data.remote.yahooFinance.models.StockPriceResponseMeta
import com.davidcrespo.onewallet.data.remote.yahooFinance.models.StockPriceResponseResult
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class YahooFinanceDataSourceTest {

    private val apiClient = mockk<YahooFinanceApiClient>()
    private val dataSource = YahooFinanceDataSource(apiClient)

    @Test
    fun `getStocksSymbolsByQuery devuelve la lista de quotes del API client`() = runTest {
        // Given
        val query = "AAPL"
        val mockResponse = MarketStockResponseList(
            quotes = listOf(
                MarketStockResponse(symbol = "AAPL", longname = "Apple Inc")
            )
        )
        coEvery { apiClient.getStocksSymbolsByQuery(query) } returns mockResponse

        // When
        val result = dataSource.getStocksSymbolsByQuery(query)

        // Then
        assertEquals(1, result.size)
        assertEquals("AAPL", result[0].symbol)
    }

    @Test
    fun `getStocksSymbolsByQuery devuelve lista vacia si quotes es nulo`() = runTest {
        // Given
        val query = "NONE"
        val mockResponse = MarketStockResponseList(quotes = null)
        coEvery { apiClient.getStocksSymbolsByQuery(query) } returns mockResponse

        // When
        val result = dataSource.getStocksSymbolsByQuery(query)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getStockPrice mapea correctamente el meta del primer resultado a InvestmentDto`() = runTest {
        // Given
        val symbol = "AAPL"
        val name = "Apple Inc"
        val mockPriceResponse = StockPriceResponse(
            chart = StockPriceResponseChart(
                result = listOf(
                    StockPriceResponseResult(
                        meta = StockPriceResponseMeta(
                            currency = USD,
                            regularMarketPrice = 150.0,
                            chartPreviousClose = 148.0
                        )
                    )
                )
            )
        )
        coEvery { apiClient.getStockPrice(symbol) } returns mockPriceResponse

        // When
        val result = dataSource.getStockPrice(symbol, name)

        // Then
        assertEquals(symbol, result?.symbol)
        assertEquals(name, result?.name)
        assertEquals(150.0, result?.price ?: 0.0, 0.0)
        assertEquals(148.0, result?.previousPrice ?: 0.0, 0.0)
        assertEquals(USD, result?.currency?.code)
        assertEquals(InvestmentType.STOCK, result?.type)
    }

    @Test
    fun `getStockPrice devuelve null si no hay resultados en el chart`() = runTest {
        // Given
        val symbol = "FAIL"
        val mockPriceResponse = StockPriceResponse(
            chart = StockPriceResponseChart(result = null)
        )
        coEvery { apiClient.getStockPrice(symbol) } returns mockPriceResponse

        // When
        val result = dataSource.getStockPrice(symbol, "Name")

        // Then
        assertNull(result)
    }
}
