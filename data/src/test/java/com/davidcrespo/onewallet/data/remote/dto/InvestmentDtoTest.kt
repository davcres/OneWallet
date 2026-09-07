package com.davidcrespo.onewallet.data.remote.dto

import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InvestmentDtoTest {

    private val validDto = InvestmentDto(
        symbol = "AAPL",
        name = "Apple Inc",
        quantity = 10.0,
        price = 150.0,
        previousPrice = 148.0,
        currency = CurrencyDto(USD),
        type = InvestmentType.STOCK,
    )

    @Test
    fun `isValidName devuelve true para nombres no vacios`() {
        assertTrue(validDto.isValidName())
    }

    @Test
    fun `isValidName devuelve false para nombres vacios o con espacios`() {
        val invalidDto = validDto.copy(name = "   ")
        assertFalse(invalidDto.isValidName())
    }

    @Test
    fun `isValidPrice devuelve true para precios mayores a cero`() {
        assertTrue(validDto.isValidPrice())
    }

    @Test
    fun `isValidPrice devuelve false para precios iguales o menores a cero`() {
        assertFalse(validDto.copy(price = 0.0).isValidPrice())
        assertFalse(validDto.copy(price = -1.0).isValidPrice())
    }

    @Test
    fun `toDomain mapea todos los campos correctamente`() {
        // When
        val domain = validDto.toDomain()

        // Then
        assertEquals(validDto.symbol, domain.symbol)
        assertEquals(validDto.name, domain.name)
        assertEquals(validDto.quantity, domain.quantity, 0.0)
        assertEquals(validDto.price, domain.price, 0.0)
        assertEquals(validDto.previousPrice, domain.previousPrice, 0.0)
        assertEquals(validDto.currency.code, domain.currency.code)
        assertEquals(validDto.type, domain.type)
    }

    @Test
    fun `toEntity mapea todos los campos correctamente`() {
        // When
        val entity = validDto.toEntity()

        // Then
        assertEquals(validDto.symbol, entity.symbol)
        assertEquals(validDto.name, entity.name)
        assertEquals(validDto.quantity, entity.quantity, 0.0)
        assertEquals(validDto.price, entity.price, 0.0)
        assertEquals(validDto.previousPrice, entity.previousPrice ?: 0.0, 0.0)
        assertEquals(validDto.currency.code, entity.currency.code)
        assertEquals(validDto.type, entity.type)
    }
}
