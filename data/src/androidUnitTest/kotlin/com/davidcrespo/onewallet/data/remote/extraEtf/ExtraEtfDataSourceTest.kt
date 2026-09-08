package com.davidcrespo.onewallet.data.remote.extraEtf

import com.davidcrespo.onewallet.data.remote.extraEtf.models.AssetResult
import com.davidcrespo.onewallet.data.remote.extraEtf.models.ExtraEtfResponse
import com.davidcrespo.onewallet.data.remote.extraEtf.models.LastQuote
import com.davidcrespo.onewallet.data.remote.extraEtf.models.ReturnDetail
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExtraEtfDataSourceTest {

    private val apiClient = mockk<ExtraEtfApiClient>()
    private val dataSource = ExtraEtfDataSource(apiClient)

    @Test
    fun `getEtfPrice mapea correctamente de ExtraEtfResponse a InvestmentDto`() = runTest {
        // Given
        val symbol = "IE00B4L5Y983"
        val yesterdayStr = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val mockResponse = ExtraEtfResponse(
            results = listOf(
                AssetResult(
                    isin = symbol,
                    fondName = "iShares Core MSCI World",
                    currency = EUR,
                    nav = 85.0,
                    navDate = "2024-03-10",
                    lastQuote = LastQuote(ask = 86.0, bid = 85.0, mid = 85.50, currency = EUR),
                    returns = mapOf(
                        "yesterday" to ReturnDetail(closePrice = 84.0, currency = EUR, priceDate = yesterdayStr)
                    )
                )
            )
        )
        coEvery { apiClient.getEtfPrice(symbol) } returns mockResponse

        // When
        val result = dataSource.getEtfPrice(symbol)

        // Then
        assertEquals(symbol, result.symbol)
        assertEquals("iShares Core MSCI World", result.name)
        assertEquals(85.50, result.price, 0.0)
        assertEquals(84.0, result.previousPrice, 0.0)
        assertEquals(EUR, result.currency.code)
        assertEquals(InvestmentType.ETF, result.type)
    }
}
