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

class SaveMonthlyPortfolioUseCaseTest {

    private val portfolioRepository = mockk<PortfolioRepository>(relaxed = true)
    private lateinit var useCase: SaveMonthlyPortfolioUseCase
    private val fixedDate = LocalDate.of(2026, 3, 10)

    @Before
    fun setUp() {
        mockkStatic(LocalDate::class)
        every { LocalDate.now() } returns fixedDate
        useCase = SaveMonthlyPortfolioUseCase(portfolioRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `cuando la lista esta vacia, se borra el portfolio del mes actual`() = runTest {
        // When
        useCase(emptyList())

        // Then
        coVerify(exactly = 1) { 
            portfolioRepository.deleteMonthPortfolio(2026, 3) 
        }
        coVerify(exactly = 0) { 
            portfolioRepository.updateMonthPortfolio(any(), any(), any()) 
        }
    }

    @Test
    fun `cuando hay inversiones, se actualiza el portfolio del mes con las fechas corregidas`() = runTest {
        // Given
        val investment = Investment(
            symbol = "AAPL",
            name = "Apple",
            quantity = 10.0,
            price = 150.0,
            previousPrice = 0.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            year = 2020, // Fecha antigua
            month = 1
        )
        val items = listOf(investment)

        // When
        useCase(items)

        // Then
        coVerify(exactly = 1) {
            portfolioRepository.updateMonthPortfolio(
                year = 2026,
                month = 3,
                investments = match { list ->
                    list.size == 1 && 
                    list[0].symbol == "AAPL" && 
                    list[0].year == 2026 && 
                    list[0].month == 3
                }
            )
        }
        coVerify(exactly = 0) { 
            portfolioRepository.deleteMonthPortfolio(any(), any()) 
        }
    }
}
