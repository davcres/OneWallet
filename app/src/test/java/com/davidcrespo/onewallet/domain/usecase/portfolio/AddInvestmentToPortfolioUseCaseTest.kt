package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddInvestmentToPortfolioUseCaseTest {

    private val repository = mockk<PortfolioRepository>(relaxed = true)
    private lateinit var useCase: AddInvestmentToPortfolioUseCase

    @BeforeEach
    fun setUp() {
        useCase = AddInvestmentToPortfolioUseCase(repository)
    }

    @Test
    fun `cuando se invoca, delega la llamada al repositorio con la inversion proporcionada`() = runTest {
        // Given
        val investment = Investment(
            symbol = "AAPL",
            name = "Apple",
            quantity = 10.0,
            price = 150.0,
            previousPrice = 145.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            year = 2026,
            month = 3
        )

        // When
        useCase(investment)

        // Then
        coVerify(exactly = 1) { repository.addOrUpdateItem(investment) }
    }
}
