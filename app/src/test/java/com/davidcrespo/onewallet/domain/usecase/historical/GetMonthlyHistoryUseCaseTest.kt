package com.davidcrespo.onewallet.domain.usecase.historical

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

class GetMonthlyHistoryUseCaseTest {

    private val portfolioRepository = mockk<PortfolioRepository>()
    private lateinit var useCase: GetMonthlyHistoryUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetMonthlyHistoryUseCase(portfolioRepository)
    }

    @Test
    fun `cuando el repositorio devuelve una lista, el UseCase retorna exito con esa lista`() = runTest {
        // Given
        val mockHistory = listOf(
            Investment("AAPL", "Apple", 10.0, 150.0, 0.0, Currency(EUR), InvestmentType.STOCK, 2024, 3),
            Investment("MSFT", "Microsoft", 5.0, 300.0, 0.0, Currency(EUR), InvestmentType.STOCK, 2024, 2)
        )
        coEvery { portfolioRepository.getMonthsPortfolio() } returns mockHistory

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockHistory, result.getOrNull())
    }

    @Test
    fun `cuando el repositorio lanza una excepcion, el UseCase retorna fallo con esa excepcion`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { portfolioRepository.getMonthsPortfolio() } throws exception

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
