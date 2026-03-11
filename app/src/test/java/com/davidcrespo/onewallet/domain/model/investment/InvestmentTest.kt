package com.davidcrespo.onewallet.domain.model.investment

import org.junit.Assert.assertEquals
import org.junit.Test

class InvestmentTest {

    @Test
    fun `setDate actualiza correctamente el mes y el año`() {
        // Given
        val investment = Investment(
            symbol = "AAPL",
            name = "Apple Inc",
            quantity = 10.0,
            price = 150.0,
            previousPrice = 148.0,
            currency = Currency(USD),
            type = InvestmentType.STOCK,
            year = 2024,
            month = 1
        )
        val newYear = 2026
        val newMonth = 3

        // When
        val updatedInvestment = investment.setDate(newMonth, newYear)

        // Then
        assertEquals(newYear, updatedInvestment.year)
        assertEquals(newMonth, updatedInvestment.month)
        // Verificar que el resto de campos no han cambiado
        assertEquals(investment.symbol, updatedInvestment.symbol)
        assertEquals(investment.name, updatedInvestment.name)
        assertEquals(investment.quantity, updatedInvestment.quantity, 0.0)
        assertEquals(investment.price, updatedInvestment.price, 0.0)
        assertEquals(investment.previousPrice, updatedInvestment.previousPrice, 0.0)
        assertEquals(investment.currency, updatedInvestment.currency)
        assertEquals(investment.type, updatedInvestment.type)
    }
}
