package com.davidcrespo.onewallet.data.remote.justEtf

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.data.remote.justEtf.models.Etf
import com.davidcrespo.onewallet.data.remote.justEtf.models.JustEtfDetailResponse
import com.davidcrespo.onewallet.data.remote.justEtf.models.JustEtfResponse
import com.davidcrespo.onewallet.data.remote.justEtf.models.QuoteValue
import com.davidcrespo.onewallet.data.remote.justEtf.models.ValueWithLocalized
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class JustEtfDataSourceTest {

    private val apiClient = mockk<JustEtfApiClient>()
    private val dataSource = JustEtfDataSource(apiClient)

    @Test
    fun `getEtfDetail mapea correctamente de JustEtfDetailResponse a InvestmentDto`() = runTest {
        // Given
        val symbol = "IE00B4L5Y983"
        val currency = CurrencyDto(EUR)
        val mockDetailResponse = JustEtfDetailResponse(
            etfs = listOf(
                Etf(
                    name = "iShares Core MSCI World",
                    isin = symbol,
                    previousQuote = ValueWithLocalized(raw = 84.0, localized = "84.0"),
                    latestQuote = ValueWithLocalized(raw = 85.0, localized = "85.0")
                )
            )
        )
        coEvery { apiClient.getEtfDetail(symbol, currency.code) } returns mockDetailResponse

        // When
        val result = dataSource.getEtfDetail(symbol, currency)

        // Then
        assertEquals(symbol, result.symbol)
        assertEquals("iShares Core MSCI World", result.name)
        assertEquals(85.0, result.price, 0.0)
        assertEquals(84.0, result.previousPrice, 0.0)
        assertEquals(currency, result.currency)
        assertEquals(InvestmentType.ETF, result.type)
    }

    @Test
    fun `getEtfPrice mapea correctamente de JustEtfResponse a InvestmentDto`() = runTest {
        // Given
        val symbol = "IE00B4L5Y983"
        val currency = CurrencyDto(EUR)
        val mockPriceResponse = JustEtfResponse(
            latestQuote = QuoteValue(raw = 85.50, localized = "85.50"),
            previousQuote = QuoteValue(raw = 84.50, localized = "84.50")
        )
        coEvery { apiClient.getEtfPrice(symbol, currency.code) } returns mockPriceResponse

        // When
        val result = dataSource.getEtfPrice(symbol, currency)

        // Then
        assertEquals(symbol, result.symbol)
        assertEquals(85.50, result.price, 0.0)
        assertEquals(84.50, result.previousPrice, 0.0)
        assertEquals(currency, result.currency)
    }
}
