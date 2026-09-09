package com.davidcrespo.onewallet.domain.usecase.history

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExportHistoryUseCaseTest {

    private val portfolioRepository = mockk<PortfolioRepository>()
    private lateinit var useCase: ExportHistoryUseCase

    @BeforeEach
    fun setUp() {
        useCase = ExportHistoryUseCase(portfolioRepository)
    }

    @Test
    fun `cuando hay datos, genera un CSV correcto con formato espanol`() = runTest {
        // Given
        val mockHistory = listOf(
            Investment("AAPL", "Apple", 10.5, 150.75, 140.25, Currency(EUR), InvestmentType.STOCK, 2024, 3, category = InvestmentCategory.Other)
        )
        coEvery { portfolioRepository.getMonthsPortfolio() } returns mockHistory

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        val expected = "Symbol;Name;Quantity;Price;PreviousPrice;Currency;Type;Year;Month;Category\nAAPL;Apple;10,5;150,75;140,25;EUR;STOCK;2024;3;other\n"
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `export csv should not have precision artifacts`() = runTest {
        // Given
        val mockInvestment = Investment(
            symbol = "AAPL",
            name = "Apple",
            quantity = 11.902000000000001, 
            price = 14.373000000000001,
            previousPrice = 14.373000000000001,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            year = 2024,
            month = 5,
            category = InvestmentCategory.Other
        )
        coEvery { portfolioRepository.getMonthsPortfolio() } returns listOf(mockInvestment)
        
        // When
        val result = useCase().getOrThrow()
        
        // Then
        // Should NOT contain the reported artifact
        assertFalse(result.contains("11,902000000000001"), "Should NOT contain the precision artifact")
        assertFalse(result.contains("14,373000000000001"), "Should NOT contain the precision artifact")
        
        // Should contain the correctly formatted numbers
        assertTrue(result.contains("11,902"), "Should contain correctly formatted quantity")
        assertTrue(result.contains("14,373"), "Should contain correctly formatted price")
    }
}
