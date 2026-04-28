package com.davidcrespo.onewallet.domain.usecase.history

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun `cuando hay datos, genera un CSV correcto`() = runTest {
        // Given
        val mockHistory = listOf(
            Investment("AAPL", "Apple", 10.0, 150.0, 140.0, Currency(EUR), InvestmentType.STOCK, 2024, 3)
        )
        coEvery { portfolioRepository.getMonthsPortfolio() } returns mockHistory

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        val expected = "Symbol;Name;Quantity;Price;PreviousPrice;Currency;Type;Year;Month\nAAPL;Apple;10.0;150.0;140.0;EUR;STOCK;2024;3\n"
        assertEquals(expected, result.getOrNull())
    }
}
