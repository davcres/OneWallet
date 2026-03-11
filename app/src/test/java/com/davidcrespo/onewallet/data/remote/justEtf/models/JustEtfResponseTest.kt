package com.davidcrespo.onewallet.data.remote.justEtf.models

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import org.junit.Assert.assertEquals
import org.junit.Test

class JustEtfResponseTest {

    @Test
    fun `toInvestDto mapea correctamente todos los campos`() {
        // Given
        val response = JustEtfResponse(
            latestQuote = QuoteValue(raw = 85.50, localized = "85,50 €"),
            previousQuote = QuoteValue(raw = 84.0, localized = "84,00 €")
        )
        val isin = "IE00B4L5Y983"
        val currency = CurrencyDto(EUR)

        // When
        val dto = response.toInvestDto(isin, currency)

        // Then
        assertEquals(isin, dto.symbol)
        assertEquals("", dto.name)
        assertEquals(85.50, dto.price, 0.0)
        assertEquals(84.0, dto.previousPrice, 0.0)
        assertEquals(currency, dto.currency)
        assertEquals(InvestmentType.ETF, dto.type)
        assertEquals(0, dto.year)
        assertEquals(0, dto.month)
    }

    @Test
    fun `toInvestDto maneja quotes nulos usando 0_0`() {
        // Given
        val response = JustEtfResponse(
            latestQuote = null,
            previousQuote = null
        )

        // When
        val dto = response.toInvestDto("ISIN", CurrencyDto(EUR))

        // Then
        assertEquals(0.0, dto.price, 0.0)
        assertEquals(0.0, dto.previousPrice, 0.0)
    }

    @Test
    fun `toInvestDto maneja campos raw nulos dentro de los quotes`() {
        // Given
        val response = JustEtfResponse(
            latestQuote = QuoteValue(raw = null, localized = "N/A"),
            previousQuote = QuoteValue(raw = null, localized = "N/A")
        )

        // When
        val dto = response.toInvestDto("ISIN", CurrencyDto(EUR))

        // Then
        assertEquals(0.0, dto.price, 0.0)
        assertEquals(0.0, dto.previousPrice, 0.0)
    }
}
