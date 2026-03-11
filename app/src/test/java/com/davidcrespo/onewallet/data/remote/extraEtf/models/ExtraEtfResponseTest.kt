package com.davidcrespo.onewallet.data.remote.extraEtf.models

import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.UNKNOWN
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExtraEtfResponseTest {

    private val yesterdayStr = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    @Test
    fun `toInvestDto mapea correctamente el primer resultado`() {
        // Given
        val response = ExtraEtfResponse(
            results = listOf(
                AssetResult(
                    isin = "IE00B4L5Y983",
                    fondName = "iShares Core MSCI World",
                    currency = EUR,
                    nav = 85.50,
                    navDate = "2024-03-10",
                    lastQuote = LastQuote(ask = 86.0, bid = 85.0, mid = 85.75, currency = EUR),
                    returns = mapOf(
                        "yesterday" to ReturnDetail(closePrice = 84.0, currency = EUR, priceDate = yesterdayStr)
                    )
                ),
                AssetResult(isin = "SECOND", fondName = "Second", currency = EUR, nav = 10.0, navDate = null, lastQuote = null, returns = null)
            )
        )

        // When
        val dto = response.toInvestDto()

        // Then
        assertEquals("IE00B4L5Y983", dto.symbol)
        assertEquals("iShares Core MSCI World", dto.name)
        assertEquals(85.75, dto.price, 0.0)
        assertEquals(84.0, dto.previousPrice, 0.0)
        assertEquals(EUR, dto.currency.code)
        assertEquals(InvestmentType.ETF, dto.type)
    }

    @Test
    fun `toInvestDto usa nav si mid es nulo`() {
        // Given
        val response = ExtraEtfResponse(
            results = listOf(
                AssetResult(
                    isin = "TEST",
                    fondName = "Test",
                    currency = EUR,
                    nav = 50.0,
                    navDate = null,
                    lastQuote = LastQuote(ask = null, bid = null, mid = null, currency = EUR),
                    returns = null
                )
            )
        )

        // When
        val dto = response.toInvestDto()

        // Then
        assertEquals(50.0, dto.price, 0.0)
    }

    @Test
    fun `toInvestDto determina la moneda siguiendo el orden de prioridad`() {
        // Prioridad: 1. lastQuote, 2. asset currency, 3. return currency, 4. UNKNOWN

        // Caso 1: lastQuote currency
        val res1 = ExtraEtfResponse(results = listOf(AssetResult(isin = "T1", fondName = "N1", currency = "USD", nav = 1.0, navDate = null, lastQuote = LastQuote(null, null, null, "EUR"), returns = null)))
        assertEquals("EUR", res1.toInvestDto().currency.code)

        // Caso 2: asset currency
        val res2 = ExtraEtfResponse(results = listOf(AssetResult(isin = "T2", fondName = "N2", currency = "USD", nav = 1.0, navDate = null, lastQuote = null, returns = null)))
        assertEquals("USD", res2.toInvestDto().currency.code)

        // Caso 3: return currency
        val res3 = ExtraEtfResponse(results = listOf(AssetResult(isin = "T3", fondName = "N3", currency = null, nav = 1.0, navDate = null, lastQuote = null, returns = mapOf("y" to ReturnDetail(1.0, "GBP", yesterdayStr)))))
        assertEquals("GBP", res3.toInvestDto().currency.code)

        // Caso 4: UNKNOWN
        val res4 = ExtraEtfResponse(results = listOf(AssetResult(isin = "T4", fondName = "N4", currency = null, nav = 1.0, navDate = null, lastQuote = null, returns = null)))
        assertEquals(UNKNOWN, res4.toInvestDto().currency.code)
    }

    @Test
    fun `toInvestDto ignora retornos que no son de ayer`() {
        // Given
        val longAgo = "2020-01-01"
        val response = ExtraEtfResponse(
            results = listOf(
                AssetResult(
                    isin = "TEST",
                    fondName = "Test",
                    currency = EUR,
                    nav = 100.0,
                    navDate = null,
                    lastQuote = null,
                    returns = mapOf(
                        "old" to ReturnDetail(closePrice = 50.0, currency = EUR, priceDate = longAgo)
                    )
                )
            )
        )

        // When
        val dto = response.toInvestDto()

        // Then
        assertEquals(0.0, dto.previousPrice, 0.0)
    }
}
