package com.davidcrespo.onewallet.domain.usecase.portfolio

import app.cash.turbine.test
import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetPortfolioItemsUseCaseTest {

    private val repository = mockk<PortfolioRepository>()
    private lateinit var useCase: GetPortfolioItemsUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetPortfolioItemsUseCase(repository)
    }

    @Test
    fun `cuando el repositorio emite una lista, el UseCase emite la misma lista`() = runTest {
        // Given
        val mockInvestments = listOf(
            Investment("AAPL", "Apple", 10.0, 150.0, 145.0, Currency(EUR), InvestmentType.STOCK, 2024, 3)
        )
        every { repository.getPortfolioItems() } returns MutableStateFlow(mockInvestments)

        // When & Then
        useCase().test {
            assertEquals(mockInvestments, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `cuando el repositorio emite actualizaciones, el UseCase las propaga`() = runTest {
        // Given
        val flow = MutableStateFlow<List<Investment>>(emptyList())
        every { repository.getPortfolioItems() } returns flow

        // When & Then
        useCase().test {
            // Emisión inicial
            assertEquals(emptyList<Investment>(), awaitItem())
            
            // Nueva emisión
            val newInvestments = listOf(
                Investment("BTC", "Bitcoin", 0.1, 60000.0, 59000.0, Currency(EUR), InvestmentType.CRYPTO, 2024, 3)
            )
            flow.value = newInvestments
            
            assertEquals(newInvestments, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
