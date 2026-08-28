package com.davidcrespo.onewallet.domain.usecase.history

import com.davidcrespo.onewallet.domain.model.investment.InvestmentCategory
import com.davidcrespo.onewallet.domain.repository.PortfolioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ImportHistoryUseCaseTest {

    private val portfolioRepository = mockk<PortfolioRepository>(relaxed = true)
    private lateinit var useCase: ImportHistoryUseCase

    @BeforeEach
    fun setUp() {
        useCase = ImportHistoryUseCase(portfolioRepository)
    }

    @Test
    fun `cuando el CSV es valido, importa los datos correctamente`() = runTest {
        // Given
        val csv = "Symbol;Name;Quantity;Price;PreviousPrice;Currency;Type;Year;Month;Category\nAAPL;Apple;10,5;150,75;140,25;EUR;STOCK;2024;3;Tecnología"

        // When
        val result = useCase(csv)

        // Then
        assertTrue(result.isSuccess)
        coVerify { 
            portfolioRepository.addOrUpdateItems(match { 
                it.size == 1 && 
                it[0].symbol == "AAPL" && 
                it[0].quantity == 10.5 && 
                it[0].price == 150.75 && 
                it[0].previousPrice == 140.25 &&
                it[0].category.id == "Tecnología"
            }) 
        }
    }

    @Test
    fun `cuando el CSV no tiene categoria, usa la categoria Other`() = runTest {
        // Given
        val csv = "Symbol;Name;Quantity;Price;PreviousPrice;Currency;Type;Year;Month\nAAPL;Apple;10,5;150,75;140,25;EUR;STOCK;2024;3"

        // When
        val result = useCase(csv)

        // Then
        assertTrue(result.isSuccess)
        coVerify { 
            portfolioRepository.addOrUpdateItems(match { 
                it.size == 1 && it[0].category == InvestmentCategory.Other
            }) 
        }
    }

    @Test
    fun `cuando el CSV esta mal formado, retorna fallo`() = runTest {
        // Given
        val csv = "Symbol;Name;Quantity;Price;PreviousPrice;Currency;Type;Year;Month;Category\nAAPL;Apple;INVALID;150,0;140,0;EUR;STOCK;2024;3;Tecnología"

        // When
        val result = useCase(csv)

        // Then
        assertTrue(result.isFailure)
    }
}
