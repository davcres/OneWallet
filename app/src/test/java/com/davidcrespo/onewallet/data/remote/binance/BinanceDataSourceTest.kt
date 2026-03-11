package com.davidcrespo.onewallet.data.remote.binance

import com.davidcrespo.onewallet.data.remote.binance.models.CryptoPriceResponse
import com.davidcrespo.onewallet.data.remote.binance.models.MarketCryptoResponse
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class BinanceDataSourceTest {

    private val apiClient = mockk<BinanceApiClient>()
    private val dataSource = BinanceDataSource(apiClient)

    @Test
    fun `getCryptoSymbols devuelve la lista de simbolos del API client`() = runTest {
        // Given
        val mockSymbols = listOf(
            MarketCryptoResponse(symbol = "BTCEUR", price = "60000.0"),
            MarketCryptoResponse(symbol = "ETHEUR", price = "3000.0")
        )
        coEvery { apiClient.getCryptoSymbols() } returns mockSymbols

        // When
        val result = dataSource.getCryptoSymbols()

        // Then
        assertEquals(2, result.size)
        assertEquals("BTCEUR", result[0].symbol)
        assertEquals("ETHEUR", result[1].symbol)
    }

    @Test
    fun `getCryptoPrice mapea correctamente de CryptoPriceResponse a InvestmentDto`() = runTest {
        // Given
        val symbol = "BTCEUR"
        val mockPriceResponse = CryptoPriceResponse(
            symbol = symbol,
            lastPrice = "61000.0",
            prevClosePrice = "60000.0"
        )
        coEvery { apiClient.getCryptoPrice(symbol) } returns mockPriceResponse

        // When
        val result = dataSource.getCryptoPrice(symbol)

        // Then
        assertEquals(symbol, result.symbol)
        assertEquals(61000.0, result.price, 0.0)
        assertEquals(60000.0, result.previousPrice, 0.0)
        assertEquals(EUR, result.currency.code)
        assertEquals(InvestmentType.CRYPTO, result.type)
    }
}
