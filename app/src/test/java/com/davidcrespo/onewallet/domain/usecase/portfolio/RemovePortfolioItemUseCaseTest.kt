package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class RemovePortfolioItemUseCaseTest {

    private val portfolioRepository = mockk<PortfolioRepository>(relaxed = true)
    private lateinit var useCase: RemovePortfolioItemUseCase
    private val fixedDate = LocalDate.of(2026, 3, 10)

    @Before
    fun setUp() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns fixedDate
        useCase = RemovePortfolioItemUseCase(portfolioRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `cuando se invoca, se llama al repositorio con la inversion y la fecha actual`() = runTest {
        // Given
        val investment = Investment(
            symbol = "AAPL",
            name = "Apple",
            quantity = 10.0,
            price = 150.0,
            previousPrice = 145.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            year = 2024,
            month = 1
        )

        // When
        useCase(investment)

        // Then
        coVerify(exactly = 1) { 
            portfolioRepository.removeItem(investment, 2026, 3) 
        }
    }
}
