package com.davidcrespo.onewallet.domain.usecase.portfolio

import com.davidcrespo.onewallet.domain.model.investment.Currency
import com.davidcrespo.onewallet.domain.model.investment.EUR
import com.davidcrespo.onewallet.domain.model.investment.Investment
import com.davidcrespo.onewallet.domain.model.investment.InvestmentType
import com.davidcrespo.onewallet.domain.repository.FinancialRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetInvestmentPriceUseCaseTest {

    private val repository = mockk<FinancialRepository>()
    private lateinit var useCase: GetInvestmentPriceUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetInvestmentPriceUseCase(repository)
    }

    @Test
    fun `cuando el repositorio devuelve exito, el UseCase retorna el mismo exito`() = runTest {
        // Given
        val mockInvestment = Investment(
            symbol = "AAPL",
            name = "Apple",
            quantity = 1.0,
            price = 150.0,
            previousPrice = 145.0,
            currency = Currency(EUR),
            type = InvestmentType.STOCK,
            year = 2024,
            month = 3
        )
        coEvery { 
            repository.getInvestmentPrice("AAPL", InvestmentType.STOCK, any(), any(), any(), any()) 
        } returns Result.success(mockInvestment)

        // When
        val result = useCase("AAPL", InvestmentType.STOCK)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(mockInvestment, result.getOrNull())
    }

    @Test
    fun `cuando el repositorio devuelve fallo, el UseCase retorna el mismo fallo`() = runTest {
        // Given
        val exception = Exception("Network Error")
        coEvery { 
            repository.getInvestmentPrice(any(), any(), any(), any(), any(), any()) 
        } returns Result.failure(exception)

        // When
        val result = useCase("AAPL", InvestmentType.STOCK)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `los parametros se pasan correctamente al repositorio`() = runTest {
        // Given
        val symbol = "TSLA"
        val type = InvestmentType.STOCK
        val name = "Tesla"
        val selectedCurrency = Currency(EUR)
        
        coEvery { 
            repository.getInvestmentPrice(symbol, type, name, selectedCurrency, null, null) 
        } returns Result.success(mockk())

        // When
        useCase(symbol, type, name, selectedCurrency)

        // Then
        // La verificación ya está implícita en el coEvery, si no coinciden fallaría el mock.
        // Pero podemos añadir un coVerify si queremos ser más explícitos.
        io.mockk.coVerify { repository.getInvestmentPrice(symbol, type, name, selectedCurrency, null, null) }
    }
}
