package com.davidcrespo.onewallet.data.local.database.portfolio.entities

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.DataSource
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory

class InvestmentMappingTest {

    @Test
    fun `InvestmentEntity toDomain mapea todos los campos correctamente`() {
        // Given
        val entity = InvestmentEntity(
            symbol = "AAPL",
            name = "Apple Inc",
            quantity = 10.0,
            price = 150.0,
            previousPrice = 145.0,
            currency = CurrencyEntity("EUR"),
            type = InvestmentType.STOCK,
            year = 2026,
            month = 3,
            preferredApi = DataSource.YAHOO_FINANCE,
            alertThreshold = 5.0,
            category = "Tecnología"
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(entity.symbol, domain.symbol)
        assertEquals(entity.name, domain.name)
        assertEquals(entity.quantity, domain.quantity, 0.0)
        assertEquals(entity.price, domain.price, 0.0)
        assertEquals(entity.previousPrice ?: 0.0, domain.previousPrice, 0.0)
        assertEquals(entity.currency.code, domain.currency.code)
        assertEquals(entity.type, domain.type)
        assertEquals(entity.year, domain.year)
        assertEquals(entity.month, domain.month)
        assertEquals(entity.preferredApi, domain.preferredApi)
        assertEquals(entity.alertThreshold, domain.alertThreshold)
        assertEquals(InvestmentCategory.fromName(entity.category), domain.category)
    }

    @Test
    fun `InvestmentEntity toDomain maneja previousPrice nulo asignando 0_0`() {
        // Given
        val entity = InvestmentEntity(
            symbol = "NEW",
            name = "New Asset",
            quantity = 1.0,
            price = 10.0,
            previousPrice = null,
            currency = CurrencyEntity("USD"),
            type = InvestmentType.CRYPTO,
            year = 2026,
            month = 3,
            preferredApi = DataSource.BINANCE,
            alertThreshold = null,
            category = InvestmentCategory.Other.id
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(0.0, domain.previousPrice, 0.0)
        assertEquals(DataSource.BINANCE, domain.preferredApi)
        assertEquals(null, domain.alertThreshold)
        assertEquals(InvestmentCategory.Other, domain.category)
    }

    @Test
    fun `Investment toEntity mapea todos los campos correctamente`() {
        // Given
        val domain = Investment(
            symbol = "BTC",
            name = "Bitcoin",
            quantity = 0.5,
            price = 60000.0,
            previousPrice = 59000.0,
            currency = Currency("EUR"),
            type = InvestmentType.CRYPTO,
            year = 2026,
            month = 3,
            preferredApi = DataSource.BINANCE,
            alertThreshold = 2.5,
            category = InvestmentCategory.fromName("Otros")
        )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals(domain.symbol, entity.symbol)
        assertEquals(domain.name, entity.name)
        assertEquals(domain.quantity, entity.quantity, 0.0)
        assertEquals(domain.price, entity.price, 0.0)
        assertEquals(domain.previousPrice, entity.previousPrice ?: 0.0, 0.0)
        assertEquals(domain.currency.code, entity.currency.code)
        assertEquals(domain.type, entity.type)
        assertEquals(domain.year, entity.year)
        assertEquals(domain.month, entity.month)
        assertEquals(domain.preferredApi, entity.preferredApi)
        assertEquals(domain.alertThreshold, entity.alertThreshold)
        assertEquals(domain.category.id, entity.category)
    }

    @Test
    fun `String toInvestmentEntity reconstruye la entidad desde el formato toString`() {
        // Given
        val originalEntity = InvestmentEntity(
            symbol = "MSFT",
            name = "Microsoft",
            quantity = 5.0,
            price = 400.0,
            previousPrice = 390.0,
            currency = CurrencyEntity("USD"),
            type = InvestmentType.STOCK,
            year = 2026,
            month = 3,
            preferredApi = DataSource.FINNHUB,
            alertThreshold = 1.0,
            category = "Tecnología"
        )
        val serialized = originalEntity.toString()

        // When
        val restoredEntity = serialized.toInvestmentEntity()

        // Then
        assertEquals(originalEntity, restoredEntity)
    }

    @Test
    fun `String toInvestmentEntity reconstruye la entidad con previousPrice nulo`() {
        // Given
        val originalEntity = InvestmentEntity(
            symbol = "BTC",
            name = "Bitcoin",
            quantity = 1.0,
            price = 60000.0,
            previousPrice = null,
            currency = CurrencyEntity("USD"),
            type = InvestmentType.CRYPTO,
            year = 2026,
            month = 3,
            preferredApi = null,
            alertThreshold = null,
            category = InvestmentCategory.Other.id
        )
        val serialized = originalEntity.toString()

        // When
        val restoredEntity = serialized.toInvestmentEntity()

        // Then
        assertEquals(originalEntity, restoredEntity)
    }

    @Test
    fun `toString produce el formato esperado con pipes`() {
        // Given
        val entity = InvestmentEntity(
            symbol = "AAPL",
            name = "Apple Inc",
            quantity = 10.0,
            price = 150.0,
            previousPrice = 145.0,
            currency = CurrencyEntity("EUR"),
            type = InvestmentType.STOCK,
            year = 2026,
            month = 3,
            preferredApi = DataSource.YAHOO_FINANCE,
            alertThreshold = 5.0,
            category = "Tecnología"
        )

        // When
        val result = entity.toString()

        // Then
        val expected = "AAPL|Apple Inc|10.0|150.0|145.0|EUR|STOCK|2026|3|YAHOO_FINANCE|5.0|Tecnología"
        assertEquals(expected, result)
    }
}
