package com.davidcrespo.onewallet.data.remote.alphaVantage.models

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StockPriceResponseTest {

    @Test
    fun `toInvestDto mapea correctamente los campos basicos`() {
        // Given
        val response = StockPriceResponse(
            symbol = "AAPL",
            price = "150.50",
            previousClose = "148.20"
        )
        val symbol = "AAPL"
        val name = "Apple Inc"
        val currencyDto = CurrencyDto(USD)

        // When
        val dto = response.toInvestDto(symbol, name, currencyDto)

        // Then
        assertEquals(symbol, dto.symbol)
        assertEquals(name, dto.name)
        assertEquals(150.50, dto.price, 0.0)
        assertEquals(148.20, dto.previousPrice, 0.0)
        assertEquals(currencyDto, dto.currency)
        assertEquals(InvestmentType.STOCK, dto.type)
        assertEquals(0.0, dto.quantity, 0.0)
    }

    @Test
    fun `toInvestDto maneja precios nulos o invalidos usando 0_0`() {
        // Given
        val response = StockPriceResponse(
            symbol = "AAPL",
            price = null,
            previousClose = "invalid"
        )
        val currencyDto = CurrencyDto(USD)

        // When
        val dto = response.toInvestDto("AAPL", "Apple", currencyDto)

        // Then
        assertEquals(0.0, dto.price, 0.0)
        assertEquals(0.0, dto.previousPrice, 0.0)
    }

    @Test
    fun `toInvestDto usa los parametros symbol y name proporcionados en lugar de los de la respuesta`() {
        // Given
        val response = StockPriceResponse(
            symbol = "IGNORED",
            price = "100.0"
        )
        val expectedSymbol = "REAL_SYMBOL"
        val expectedName = "Real Name"

        // When
        val dto = response.toInvestDto(expectedSymbol, expectedName, CurrencyDto(USD))

        // Then
        assertEquals(expectedSymbol, dto.symbol)
        assertEquals(expectedName, dto.name)
    }
}
