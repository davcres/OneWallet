package com.davidcrespo.onewallet.data.remote.finnhub

import com.davidcrespo.onewallet.data.remote.finnhub.models.MarketStockResponse
import com.davidcrespo.onewallet.data.remote.finnhub.models.StockPriceResponse
import com.davidcrespo.onewallet.domain.model.investment.USD
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FinnhubDataSourceTest {

    private val apiClient = mockk<FinnhubApiClient>()
    private val dataSource = FinnhubDataSource(apiClient)

    @Test
    fun `getStocksSymbols devuelve la lista de simbolos del API client`() = runTest {
        // Given
        val exchange = "US"
        val mockSymbols = listOf(
            MarketStockResponse(symbol = "AAPL", description = "Apple Inc", currency = "USD", figi = "F1", type = "T1"),
            MarketStockResponse(symbol = "MSFT", description = "Microsoft Corp", currency = "USD", figi = "F2", type = "T2")
        )
        coEvery { apiClient.getStocksSymbols(exchange) } returns mockSymbols

        // When
        val result = dataSource.getStocksSymbols(exchange)

        // Then
        assertEquals(2, result.size)
        assertEquals("AAPL", result[0].symbol)
        assertEquals("MSFT", result[1].symbol)
    }

    @Test
    fun `getStockPrice mapea correctamente de StockPriceResponse a InvestmentDto`() = runTest {
        // Given
        val symbol = "AAPL"
        val name = "Apple Inc"
        val mockPriceResponse = StockPriceResponse(
            c = 150.0,
            pc = 148.0
        )
        coEvery { apiClient.getStockPrice(symbol) } returns mockPriceResponse

        // When
        val result = dataSource.getStockPrice(symbol, name)

        // Then
        assertEquals(symbol, result.symbol)
        assertEquals(name, result.name)
        assertEquals(150.0, result.price, 0.0)
        assertEquals(148.0, result.previousPrice, 0.0)
        assertEquals(USD, result.currency.code)
    }
}
