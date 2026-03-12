package com.davidcrespo.onewallet.presentation.models

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.GlobalMarketRegion
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.market.MarketAsset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MarketAssetMappingTest {

    @Test
    fun `MarketAsset toUI mapea todos los campos correctamente`() {
        // Given
        val domain = MarketAsset(
            symbol = "AAPL",
            price = 150.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            description = "Apple Inc",
            figi = "BBG000B9XRY4",
            region = GlobalMarketRegion.UNITED_STATES,
            stockType = "Common Stock"
        )

        // When
        val ui = domain.toUI()

        // Then
        assertEquals(domain.symbol, ui.symbol)
        assertEquals(domain.price, ui.price, 0.0)
        assertEquals(domain.currency.code, ui.currency.code)
        assertEquals(domain.type, ui.type)
        assertEquals(domain.description, ui.description)
        assertEquals(domain.figi, ui.figi)
        assertEquals(domain.region, ui.region)
        assertEquals(domain.stockType, ui.stockType)
    }

    @Test
    fun `MarketAssetView toDomain mapea los campos de vuelta correctamente`() {
        // Given
        val ui = MarketAssetView(
            symbol = "BTCUSD",
            price = 65000.0,
            currency = CurrencyView.get("USD"),
            type = InvestmentType.CRYPTO,
            description = "Bitcoin",
            figi = "FIGI123",
            region = GlobalMarketRegion.UNITED_STATES,
            stockType = "Crypto"
        )

        // When
        val domain = ui.toDomain()

        // Then
        assertEquals(ui.symbol, domain.symbol)
        assertEquals(ui.price, domain.price, 0.0)
        assertEquals(ui.currency.code, domain.currency.code)
        assertEquals(ui.type, domain.type)
        assertEquals(ui.description, domain.description)
        assertEquals(ui.figi, domain.figi)
        assertEquals(ui.region, domain.region)
        assertEquals(ui.stockType, domain.stockType)
    }

    @Test
    fun `String toMarketAssetView reconstruye el objeto desde el formato toString`() {
        // Given
        val original = MarketAssetView(
            symbol = "NVDA",
            price = 900.0,
            currency = CurrencyView.get(EUR),
            type = InvestmentType.STOCK,
            description = "Nvidia",
            figi = "FIGI123",
            region = GlobalMarketRegion.UNITED_STATES,
            stockType = "Tech"
        )
        val serialized = original.toString()

        // When
        val restored = serialized.toMarketAssetView()

        // Then
        assertEquals(original, restored)
    }

    @Test
    fun `toString produce el formato esperado con pipes`() {
        // Given
        val ui = MarketAssetView(
            symbol = "AAPL",
            price = 150.0,
            currency = CurrencyView.get("USD"),
            type = InvestmentType.STOCK,
            description = "Apple Inc",
            figi = "FIGI123",
            region = GlobalMarketRegion.UNITED_STATES,
            stockType = "Common"
        )

        // When
        val result = ui.toString()

        // Then
        val expected = "AAPL|150.0|USD|STOCK|Apple Inc|FIGI123|UNITED_STATES|Common"
        assertEquals(expected, result)
    }

    @Test
    fun `toString maneja campos nulos correctamente`() {
        // Given
        val ui = MarketAssetView(
            symbol = "BTC",
            price = 50000.0,
            currency = CurrencyView.get("USD"),
            type = InvestmentType.CRYPTO,
            description = null,
            figi = null,
            region = null,
            stockType = null
        )

        // When
        val result = ui.toString()

        // Then
        // Nota: region.toString() cuando es null devuelve la cadena "null"
        val expected = "BTC|50000.0|USD|CRYPTO|||null|"
        assertEquals(expected, result)
    }

    @Test
    fun `String toMarketAssetView reconstruye el objeto con campos nulos`() {
        // Given
        val original = MarketAssetView(
            symbol = "BTC",
            price = 50000.0,
            currency = CurrencyView.get("USD"),
            type = InvestmentType.CRYPTO,
            description = null,
            figi = null,
            region = null,
            stockType = null
        )
        val serialized = original.toString()

        // When
        val restored = serialized.toMarketAssetView()

        // Then
        assertEquals(original, restored)
    }
}
