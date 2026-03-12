package com.davidcrespo.onewallet.data.local.database.market.entities

import com.davidcrespo.onewallet.data.remote.binance.models.MarketCryptoResponse
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CryptoMarketEntityTest {

    @Test
    fun `toDomain mapea correctamente de Entity a Domain con EUR`() {
        // Given
        val entity = CryptoMarketEntity(symbol = "BTCEUR", price = 60000.0)

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("BTCEUR", domain.symbol)
        assertEquals(60000.0, domain.price, 0.0)
        assertEquals(Currency(EUR), domain.currency)
        assertEquals(InvestmentType.CRYPTO, domain.type)
    }

    @Test
    fun `toDomain mapea correctamente de Entity a Domain con USD`() {
        // Given
        val entity = CryptoMarketEntity(symbol = "BTCUSD", price = 65000.0)

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("BTCUSD", domain.symbol)
        assertEquals(Currency(USD), domain.currency)
    }

    @Test
    fun `toDomain usa USD por defecto si el simbolo no termina en EUR o USD`() {
        // Given
        val entity = CryptoMarketEntity(symbol = "BTCUSDT", price = 65000.0)

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(Currency(USD), domain.currency)
    }

    @Test
    fun `toCryptoEntity mapea correctamente de Response a Entity`() {
        // Given
        val response = MarketCryptoResponse(symbol = "ETHUSD", price = "3500.50")

        // When
        val entity = response.toCryptoEntity()

        // Then
        assertEquals("ETHUSD", entity.symbol)
        assertEquals(3500.50, entity.price, 0.0)
    }
}
