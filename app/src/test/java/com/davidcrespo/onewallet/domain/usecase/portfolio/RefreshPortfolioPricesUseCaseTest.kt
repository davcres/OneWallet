package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RefreshPortfolioPricesUseCaseTest {

    private val getPortfolioItemsUseCase = mockk<GetPortfolioItemsUseCase>()
    private val getInvestmentPriceUseCase = mockk<GetInvestmentPriceUseCase>()
    private val financialRepository = mockk<FinancialRepository>()
    private val saveMonthlyPortfolioUseCase = mockk<SaveMonthlyPortfolioUseCase>()

    private lateinit var useCase: RefreshPortfolioPricesUseCase

    private val selectedCurrency = Currency("EUR")

    @BeforeEach
    fun setUp() {
        useCase = RefreshPortfolioPricesUseCase(
            getPortfolioItemsUseCase,
            getInvestmentPriceUseCase,
            financialRepository,
            saveMonthlyPortfolioUseCase
        )
        every { financialRepository.getSelectedCurrency() } returns selectedCurrency
        coEvery { saveMonthlyPortfolioUseCase(any()) } returns Unit
    }

    @Test
    fun `should return empty list when portfolio is empty`() = runTest {
        // Given
        every { getPortfolioItemsUseCase() } returns flowOf(emptyList())

        // When
        val result = useCase()

        // Then
        assertEquals(0, result.size)
        coVerify(exactly = 0) { saveMonthlyPortfolioUseCase(any()) }
    }

    @Test
    fun `should update market items and save portfolio`() = runTest {
        // Given
        val marketItem = Investment(
            symbol = "AAPL",
            name = "Apple",
            quantity = 1.0,
            price = 150.0,
            previousPrice = 140.0,
            currency = Currency("USD"),
            type = InvestmentType.STOCK,
            year = 2026,
            month = 4
        )
        val updatedPrice = 160.0
        val updatedItem = marketItem.copy(price = updatedPrice, previousPrice = 150.0)
        
        every { getPortfolioItemsUseCase() } returns flowOf(listOf(marketItem))
        coEvery { 
            getInvestmentPriceUseCase(
                symbol = marketItem.symbol,
                type = marketItem.type,
                name = marketItem.name,
                selectedCurrency = selectedCurrency,
                investmentCurrency = marketItem.currency,
                preferredApi = marketItem.preferredApi
            )
        } returns Result.success(updatedItem)

        // When
        val result = useCase()

        // Then
        assertEquals(1, result.size)
        assertEquals(updatedPrice, result[0].first.price)
        // (160 - 150) / 150 * 100 = 6.666...
        assertEquals(6.666666666666667, result[0].second)
        
        coVerify { saveMonthlyPortfolioUseCase(listOf(updatedItem)) }
    }

    @Test
    fun `should handle mixed market and manual items`() = runTest {
        // Given
        val marketItem = Investment(
            symbol = "BTC",
            name = "Bitcoin",
            quantity = 0.1,
            price = 50000.0,
            previousPrice = 49000.0,
            currency = Currency("USD"),
            type = InvestmentType.CRYPTO,
            year = 2026,
            month = 4
        )
        val manualItem = Investment(
            symbol = "CASH",
            name = "Savings",
            quantity = 1000.0,
            price = 1.0,
            previousPrice = 1.0,
            currency = Currency("EUR"),
            type = InvestmentType.BANK,
            year = 2026,
            month = 4
        )
        val updatedPrice = 51000.0
        val updatedMarketItem = marketItem.copy(price = updatedPrice, previousPrice = 50000.0)

        every { getPortfolioItemsUseCase() } returns flowOf(listOf(marketItem, manualItem))
        coEvery { 
            getInvestmentPriceUseCase(
                symbol = marketItem.symbol,
                type = marketItem.type,
                name = marketItem.name,
                selectedCurrency = selectedCurrency,
                investmentCurrency = marketItem.currency,
                preferredApi = marketItem.preferredApi
            )
        } returns Result.success(updatedMarketItem)

        // When
        val result = useCase()

        // Then
        assertEquals(1, result.size) // invoke returns updatedMarketItems
        assertEquals(updatedPrice, result[0].first.price)
        
        coVerify { saveMonthlyPortfolioUseCase(match { 
            it.size == 2 && it.contains(updatedMarketItem) && it.contains(manualItem)
        }) }
    }

    @Test
    fun `should keep old price when market update fails`() = runTest {
        // Given
        val marketItem = Investment(
            symbol = "AAPL",
            name = "Apple",
            quantity = 1.0,
            price = 150.0,
            previousPrice = 140.0,
            currency = Currency("USD"),
            type = InvestmentType.STOCK,
            year = 2026,
            month = 4
        )
        
        every { getPortfolioItemsUseCase() } returns flowOf(listOf(marketItem))
        coEvery { 
            getInvestmentPriceUseCase(any(), any(), any(), any(), any(), any(), any())
        } returns Result.failure(Exception("Network error"))

        // When
        val result = useCase()

        // Then
        assertEquals(1, result.size)
        assertEquals(150.0, result[0].first.price)
        assertEquals(0.0, result[0].second)
        
        coVerify { saveMonthlyPortfolioUseCase(listOf(marketItem)) }
    }
}
