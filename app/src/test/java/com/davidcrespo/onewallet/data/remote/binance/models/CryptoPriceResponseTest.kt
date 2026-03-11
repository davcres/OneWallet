package com.davidcrespo.onewallet.data.remote.binance.models

import com.davidcrespo.onewallet.data.remote.dto.CurrencyDto
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.Assert.assertEquals
import org.junit.Test

class CryptoPriceResponseTest {

    @Test
    fun `toInvestDto mapea correctamente todos los campos con EUR`() {
        // Given
        val response = CryptoPriceResponse(
            symbol = "BTCEUR",
            lastPrice = "60000.50",
            prevClosePrice = "59000.20"
        )

        // When
        val dto = response.toInvestDto()

        // Then
        assertEquals("BTCEUR", dto.symbol)
        assertEquals("", dto.name)
        assertEquals(60000.50, dto.price, 0.0)
        assertEquals(59000.20, dto.previousPrice, 0.0)
        assertEquals(CurrencyDto(EUR), dto.currency)
        assertEquals(InvestmentType.CRYPTO, dto.type)
        assertEquals(0, dto.year)
        assertEquals(0, dto.month)
    }

    @Test
    fun `toInvestDto mapea correctamente todos los campos con USD`() {
        // Given
        val response = CryptoPriceResponse(
            symbol = "BTCUSD",
            lastPrice = "65000.00",
            prevClosePrice = "64000.00"
        )

        // When
        val dto = response.toInvestDto()

        // Then
        assertEquals("BTCUSD", dto.symbol)
        assertEquals(CurrencyDto(USD), dto.currency)
    }

    @Test
    fun `toInvestDto usa USD por defecto si el simbolo no termina en EUR o USD`() {
        // Given
        val response = CryptoPriceResponse(
            symbol = "BTCUSDT",
            lastPrice = "65000.00",
            prevClosePrice = "64000.00"
        )

        // When
        val dto = response.toInvestDto()

        // Then
        assertEquals(CurrencyDto(USD), dto.currency)
    }

    @Test
    fun `toInvestDto maneja precios invalidos usando 0_0`() {
        // Given
        val response = CryptoPriceResponse(
            symbol = "BTCEUR",
            lastPrice = "invalid",
            prevClosePrice = "invalid"
        )

        // When
        val dto = response.toInvestDto()

        // Then
        assertEquals(0.0, dto.price, 0.0)
        assertEquals(0.0, dto.previousPrice, 0.0)
    }
}
