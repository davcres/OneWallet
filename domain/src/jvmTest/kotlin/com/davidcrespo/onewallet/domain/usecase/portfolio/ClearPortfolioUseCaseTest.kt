package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ClearPortfolioUseCaseTest {

    private val getPortfolioItemsUseCase = mockk<GetPortfolioItemsUseCase>()
    private val removePortfolioItemUseCase = mockk<RemovePortfolioItemUseCase>()
    private lateinit var clearPortfolioUseCase: ClearPortfolioUseCase

    @BeforeEach
    fun setUp() {
        clearPortfolioUseCase = ClearPortfolioUseCase(getPortfolioItemsUseCase, removePortfolioItemUseCase)
    }

    @Test
    fun `cuando se ejecuta, se obtienen los items y se borran uno a uno`() = runTest {
        // GIVEN
        val item1 = Investment(
            symbol = "AAPL",
            name = "Apple",
            quantity = 1.0,
            price = 150.0,
            previousPrice = 145.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            year = 2024,
            month = 1
        )
        val item2 = item1.copy(symbol = "MSFT", name = "Microsoft")
        val items = listOf(item1, item2)

        every { getPortfolioItemsUseCase() } returns flowOf(items)
        coEvery { removePortfolioItemUseCase(any()) } returns Unit

        // WHEN
        clearPortfolioUseCase()

        // THEN
        coVerify(exactly = 1) { removePortfolioItemUseCase(item1) }
        coVerify(exactly = 1) { removePortfolioItemUseCase(item2) }
    }

    @Test
    fun `cuando no hay items, no se llama a borrar`() = runTest {
        // GIVEN
        every { getPortfolioItemsUseCase() } returns flowOf(emptyList())

        // WHEN
        clearPortfolioUseCase()

        // THEN
        coVerify(exactly = 0) { removePortfolioItemUseCase(any()) }
    }
}
