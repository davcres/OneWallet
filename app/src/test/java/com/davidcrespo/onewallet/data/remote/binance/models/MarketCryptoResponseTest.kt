package com.davidcrespo.onewallet.data.remote.binance.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class MarketCryptoResponseTest {

    @Test
    fun `toDomain mapea correctamente todos los campos con EUR`() {
        // Given
        val response = MarketCryptoResponse(
            symbol = "BTCEUR",
            price = "60000.50"
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("BTCEUR", domain.symbol)
        assertEquals(60000.50, domain.price, 0.0)
        assertEquals(Currency(EUR), domain.currency)
        assertEquals(InvestmentType.CRYPTO, domain.type)
        assertEquals(null, domain.description)
    }

    @Test
    fun `toDomain mapea correctamente todos los campos con USD`() {
        // Given
        val response = MarketCryptoResponse(
            symbol = "BTCUSD",
            price = "65000.00"
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals("BTCUSD", domain.symbol)
        assertEquals(Currency(USD), domain.currency)
    }

    @Test
    fun `toDomain usa USD por defecto si el simbolo no termina en EUR o USD`() {
        // Given
        val response = MarketCryptoResponse(
            symbol = "BTCUSDT",
            price = "65000.00"
        )

        // When
        val domain = response.toDomain()

        // Then
        assertEquals(Currency(USD), domain.currency)
    }

    @Test
    fun `toDomain lanza excepcion si el precio no es un numero valido`() {
        // Given
        val response = MarketCryptoResponse(
            symbol = "BTCEUR",
            price = "invalid"
        )

        // When & Then
        assertThrows(NumberFormatException::class.java) {
            response.toDomain()
        }
    }
}
