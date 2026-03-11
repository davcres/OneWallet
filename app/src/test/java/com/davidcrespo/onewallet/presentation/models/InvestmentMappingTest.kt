package com.davidcrespo.onewallet.presentation.models

import com.davidcrespo.onewallet.R
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.model.investment.USD
import org.junit.Assert.assertEquals
import org.junit.Test

class InvestmentMappingTest {

    @Test
    fun `Investment toUI mapea correctamente todos los campos y calcula el porcentaje de cambio`() {
        // Given
        val domain = Investment(
            symbol = "AAPL",
            name = "Apple",
            quantity = 10.0,
            price = 150.0,
            previousPrice = 120.0,
            currency = Currency(USD),
            type = InvestmentType.STOCK,
            year = 2024,
            month = 3
        )

        // When
        val ui = domain.toUI()

        // Then
        assertEquals("AAPL", ui.symbol)
        assertEquals("Apple", ui.name)
        assertEquals(10.0, ui.quantity, 0.0)
        assertEquals(150.0, ui.displayPrice, 0.0)
        assertEquals(120.0, ui.displayPreviousPrice, 0.0)
        assertEquals(150.0, ui.originalPrice, 0.0)
        assertEquals(120.0, ui.originalPreviousPrice, 0.0)
        assertEquals(USD, ui.originalCurrency.code)
        // (150 - 120) / 120 * 100 = 30 / 120 * 100 = 25%
        assertEquals(25.0, ui.changePercent, 0.0)
        assertEquals(InvestmentType.STOCK, ui.type)
        assertEquals(2024, ui.year)
        assertEquals(3, ui.month)
    }

    @Test
    fun `Investment toUI maneja previousPrice cero devolviendo 0 de cambio`() {
        // Given
        val domain = Investment(
            symbol = "NEW",
            name = "New",
            quantity = 1.0,
            price = 100.0,
            previousPrice = 0.0,
            currency = Currency(EUR),
            type = InvestmentType.CRYPTO,
            year = 2024,
            month = 3
        )

        // When
        val ui = domain.toUI()

        // Then
        assertEquals(0.0, ui.changePercent, 0.0)
    }

    @Test
    fun `InvestmentView toDomain mapea de vuelta correctamente`() {
        // Given
        val ui = InvestmentView(
            symbol = "BTC",
            name = "Bitcoin",
            quantity = 0.5,
            displayPrice = 60000.0,
            displayPreviousPrice = 58000.0,
            originalPrice = 60000.0,
            originalPreviousPrice = 58000.0,
            originalCurrency = CurrencyView.get(EUR),
            changePercent = 3.44,
            type = InvestmentType.CRYPTO,
            year = 2024,
            month = 3
        )

        // When
        val domain = ui.toDomain()

        // Then
        assertEquals(ui.symbol, domain.symbol)
        assertEquals(ui.name, domain.name)
        assertEquals(ui.quantity, domain.quantity, 0.0)
        assertEquals(ui.originalPrice, domain.price, 0.0)
        assertEquals(ui.originalPreviousPrice, domain.previousPrice, 0.0)
        assertEquals(ui.originalCurrency.code, domain.currency.code)
        assertEquals(ui.type, domain.type)
        assertEquals(ui.year, domain.year)
        assertEquals(ui.month, domain.month)
    }

    @Test
    fun `getIconRes devuelve el recurso correcto para cada tipo`() {
        val baseUi = InvestmentView(
            "S", "N", 1.0, 1.0, 1.0, 1.0, 1.0, 
            CurrencyView.get(USD), 0.0, InvestmentType.STOCK, 1, 2024
        )

        assertEquals(R.drawable.ic_stacked_line_chart, baseUi.copy(type = InvestmentType.STOCK).getIconRes())
        assertEquals(R.drawable.ic_currency_bitcoin, baseUi.copy(type = InvestmentType.CRYPTO).getIconRes())
        assertEquals(R.drawable.ic_account_balance, baseUi.copy(type = InvestmentType.FUND).getIconRes())
        assertEquals(R.drawable.ic_query_stats, baseUi.copy(type = InvestmentType.ETF).getIconRes())
        assertEquals(R.drawable.ic_savings, baseUi.copy(type = InvestmentType.BANK).getIconRes())
        assertEquals(R.drawable.ic_category, baseUi.copy(type = InvestmentType.OTHER).getIconRes())
    }

    @Test
    fun `toString y toInvestmentView mantienen la integridad de los datos`() {
        // Given
        val original = InvestmentView(
            symbol = "AAPL",
            name = "Apple Inc",
            quantity = 10.5,
            displayPrice = 150.2,
            displayPreviousPrice = 148.5,
            originalPrice = 150.2,
            originalPreviousPrice = 148.5,
            originalCurrency = CurrencyView.get(USD),
            changePercent = 1.14,
            type = InvestmentType.STOCK,
            year = 2024,
            month = 3
        )
        val serialized = original.toString()

        // When
        val restored = serialized.toInvestmentView()

        // Then
        // CurrencyView.icon es un ImageVector, que no implementa equals por valor de forma fiable,
        // pero CurrencyView.get(code) devuelve la misma instancia de icon para códigos conocidos.
        assertEquals(original, restored)
    }
}
