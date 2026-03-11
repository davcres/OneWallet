package com.davidcrespo.onewallet.data.remote.dto

import com.davidcrespo.onewallet.data.local.database.portfolio.entities.CurrencyEntity
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyDtoTest {

    @Test
    fun `toDomain mapea correctamente el codigo`() {
        // Given
        val dto = CurrencyDto(EUR)

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(EUR, domain.code)
    }

    @Test
    fun `toEntity mapea correctamente el codigo`() {
        // Given
        val dto = CurrencyDto(USD)

        // When
        val entity = dto.toEntity()

        // Then
        assertEquals(USD, entity.code)
    }

    @Test
    fun `Currency toDto mapea correctamente el codigo`() {
        // Given
        val domain = Currency(EUR)

        // When
        val dto = domain.toDto()

        // Then
        assertEquals(EUR, dto.code)
    }
}
