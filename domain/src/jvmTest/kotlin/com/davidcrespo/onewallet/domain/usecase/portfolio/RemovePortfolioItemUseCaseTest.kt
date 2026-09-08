package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.cache.SymbolCache
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
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import com.davidcrespo.onewallet.util.TestClock
import kotlinx.datetime.Instant
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RemovePortfolioItemUseCaseTest {

    private val portfolioRepository = mockk<PortfolioRepository>(relaxed = true)
    private val symbolCache = mockk<SymbolCache>(relaxed = true)
    private val clock = TestClock(Instant.parse("2026-03-10T12:00:00Z"))
    private lateinit var useCase: RemovePortfolioItemUseCase

    @BeforeEach
    fun setUp() {
        useCase = RemovePortfolioItemUseCase(portfolioRepository, symbolCache, clock)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `cuando se invoca, se llama al repositorio con la inversion y la fecha actual y se borra del cache`() = runTest {
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
        verify(exactly = 1) {
            symbolCache.removeCachedInvestment("AAPL")
        }
    }
}
