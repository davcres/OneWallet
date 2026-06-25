package com.davidcrespo.onewallet.data.remote.finnhub.models

import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StockPriceResponseTest {

    @Test
    fun `toInvestDto mapea correctamente todos los campos`() {
        // Given
        val response = StockPriceResponse(
            c = 150.50,
            pc = 148.20,
            d = 2.30,
            dp = 1.55,
            h = 152.0,
            l = 147.0,
            o = 148.0,
            t = 1710115200L
        )
        val symbol = "AAPL"
        val name = "Apple Inc"

        // When
        val dto = response.toInvestDto(symbol, name)

        // Then
        assertEquals(symbol, dto.symbol)
        assertEquals(name, dto.name)
        assertEquals(150.50, dto.price, 0.0)
        assertEquals(148.20, dto.previousPrice, 0.0)
        assertEquals(USD, dto.currency.code)
        assertEquals(InvestmentType.STOCK, dto.type)
    }

    @Test
    fun `toInvestDto maneja previousClose nulo usando 0_0`() {
        // Given
        val response = StockPriceResponse(
            c = 100.0,
            pc = null
        )

        // When
        val dto = response.toInvestDto("TEST", "Test")

        // Then
        assertEquals(100.0, dto.price, 0.0)
        assertEquals(0.0, dto.previousPrice, 0.0)
    }
}
