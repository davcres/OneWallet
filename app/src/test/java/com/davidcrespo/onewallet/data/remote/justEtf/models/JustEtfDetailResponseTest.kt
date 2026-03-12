package com.davidcrespo.onewallet.data.remote.justEtf.models

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JustEtfDetailResponseTest {

    @Test
    fun `toInvestDto mapea correctamente el primer ETF de la lista`() {
        // Given
        val response = JustEtfDetailResponse(
            etfs = listOf(
                Etf(
                    name = "iShares Core MSCI World",
                    isin = "IE00B4L5Y983",
                    previousQuote = ValueWithLocalized(raw = 84.0, localized = "84.00"),
                    latestQuote = ValueWithLocalized(raw = 85.50, localized = "85.50")
                ),
                Etf(name = "Second", isin = "SECOND", previousQuote = null, latestQuote = null)
            )
        )
        val currency = CurrencyDto(EUR)

        // When
        val dto = response.toInvestDto(currency)

        // Then
        assertEquals("IE00B4L5Y983", dto.symbol)
        assertEquals("iShares Core MSCI World", dto.name)
        assertEquals(85.50, dto.price, 0.0)
        assertEquals(84.0, dto.previousPrice, 0.0)
        assertEquals(currency, dto.currency)
        assertEquals(InvestmentType.ETF, dto.type)
    }

    @Test
    fun `toInvestDto maneja lista vacia o nula`() {
        // Given
        val response = JustEtfDetailResponse(etfs = null)
        val currency = CurrencyDto(EUR)

        // When
        val dto = response.toInvestDto(currency)

        // Then
        assertEquals("", dto.symbol)
        assertEquals("", dto.name)
        assertEquals(0.0, dto.price, 0.0)
    }

    @Test
    fun `toInvestDto maneja campos nulos dentro del primer ETF`() {
        // Given
        val response = JustEtfDetailResponse(
            etfs = listOf(
                Etf(name = null, isin = null, previousQuote = null, latestQuote = null)
            )
        )

        // When
        val dto = response.toInvestDto(CurrencyDto(EUR))

        // Then
        assertEquals("", dto.symbol)
        assertEquals("", dto.name)
        assertEquals(0.0, dto.price, 0.0)
        assertEquals(0.0, dto.previousPrice, 0.0)
    }
}
