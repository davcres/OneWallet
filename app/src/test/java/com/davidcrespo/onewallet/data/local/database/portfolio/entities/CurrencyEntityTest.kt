package com.davidcrespo.onewallet.data.local.database.portfolio.entities

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyEntityTest {

    @Test
    fun `Currency toEntity mapea correctamente el codigo`() {
        // Given
        val domain = Currency(EUR)

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals(EUR, entity.code)
    }

    @Test
    fun `CurrencyEntity toDomain mapea correctamente el codigo`() {
        // Given
        val entity = CurrencyEntity(USD)

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(USD, domain.code)
    }
}
